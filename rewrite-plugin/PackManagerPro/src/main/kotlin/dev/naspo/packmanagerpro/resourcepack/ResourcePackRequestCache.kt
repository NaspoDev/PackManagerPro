package dev.naspo.packmanagerpro.resourcepack

import dev.naspo.packmanagerpro.PackManagerPro
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.Component
import org.bukkit.configuration.ConfigurationSection
import java.net.URI
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level

/**
 * Caches [ResourcePackRequest]s for the global resource pack and the world-specific resource packs.
 *
 * A cache is needed because a network request is needed to compute the resource pack hash every time a
 * [ResourcePackRequest] is created. Instead of doing this for every player every time, we will instead cache the
 * [ResourcePackRequest] here.
 *
 * The cache is refreshed upon server startup and every config reload.
 */
class ResourcePackRequestCache(val plugin: PackManagerPro) {
    // Volatile as this variable is set from another thread.
    // (Specifically in ResourcePackInfo.computeHashAndBuild()'s CompletableFuture. See this done below).
    /** The cached ResourcePackRequest for the global resource pack. */
    @Volatile
    var global: ResourcePackRequest? = null
        private set

    // Backing field is a ConcurrentHashMap as this is modified from another thread.
    // (Specifically in ResourcePackInfo.computeHashAndBuild()'s CompletableFuture. See this done below).
    /** The cached ResourcePackRequests for the world-specific resource packs. (World names converted to lowercase). */
    val worlds: Map<String, ResourcePackRequest>
        field: ConcurrentHashMap<String, ResourcePackRequest> = ConcurrentHashMap()

    // TODO: is this even needed? What happens if I dont set a fallback prompt/prompt at all?
    // The fallback resource pack download prompt to use if one isn't set in the config.
    private val fallbackPrompt: String = "Please download the resource pack."

    /**
     * Refresh the global and per-world cache.
     */
    fun refreshCache() {
        plugin.logger.info("Refreshing ResourcePackRequest cache.")
        refreshGlobalCache()
        refreshWorldsCache()
    }

    private fun refreshGlobalCache() {
        val packUrl: String? = plugin.config.getString("global-application.pack-url")

        if (packUrl == null) {
            // Clear the global pack cache.
            global = null
            return
        }

        // Extract more pack info.
        val required: Boolean = plugin.config.getBoolean("global-application.force-pack")
        val prompt: String? = plugin.config.getString("global-application.prompt-message")

        buildRequest(packUrl, required, prompt)
            .thenAccept { cache(it) }
            .exceptionally {
                plugin.logger.log(
                    Level.SEVERE,
                    "Failed to build ResourcePackRequest for global resource pack.",
                    it)
                null
            }
    }

    private fun refreshWorldsCache() {
        // Get the 'per-world-application' configuration section.
        val perWorldApplicationConfigurationSection: ConfigurationSection? =
            plugin.config.getConfigurationSection("per-world-application")

        if (perWorldApplicationConfigurationSection == null) {
            // Clear the world-specific resource pack cache.
            worlds.clear()
            plugin.logger.log(
                Level.SEVERE,
                "Attempt to cache world-specific resource packs failed because" +
                        " the 'per-world-application' configuration section could not be found!"
            )
            return
        }

        // Get world-specific keys from the config. (i.e. the children of 'per-world-application' section,
        // which each represent a world-specific resource pack).
        val worldKeys: Set<String> = perWorldApplicationConfigurationSection.getKeys(false)

        // Only retain keys for worlds in the cache that still have a configuration section. (Drop everything else).
        worlds.keys.retainAll(worldKeys)

        // For every world config section, extract its pack info and cache it.
        for (world in worldKeys) {
            val packUrl: String? = plugin.config.getString("per-world-application.$world.pack-url")

            if (packUrl == null) {
                worlds.remove(world)
                continue
            }

            // Extract more pack info.
            val required: Boolean = plugin.config.getBoolean("per-world-application.$world.force-pack")
            val prompt: String? = plugin.config.getString("per-world-application.$world.prompt-message")

            buildRequest(packUrl, required, prompt, world)
                .thenAccept { cache(it, world) }
                .exceptionally {
                    plugin.logger.log(
                        Level.SEVERE,
                        "Failed to build ResourcePackRequest for world '$world' resource pack.",
                        it)
                    null
                }
        }
    }

    /**
     * Builds ResourcePackRequest from the provided parameters.
     * @param url - The url of the resource pack.
     * @param required - Whether the resource pack should be required or not.
     * @param prompt - The custom download resource pack prompt to use.
     * @param worldName - The name of the world that this resource pack belongs to. If null, this will be treated
     * as a global resource pack.
     */
    private fun buildRequest(
        url: String,
        required: Boolean,
        prompt: String? = null,
        worldName: String? = null
    ): CompletableFuture<ResourcePackRequest> {
        // URL parsing
        val uri: URI = try {
            URI.create(url)
        } catch (e: IllegalArgumentException) {
            val errorMessage = if (worldName != null) {
                "Failed to parse resource pack URL for world $worldName. Is its formatting valid?"
            } else {
                "Failed to parse global resource pack URL. Is its formatting valid?"
            }

            plugin.logger.log(Level.SEVERE, errorMessage, e)
            return CompletableFuture.failedFuture(e)
        }

        // Build ResourcePackInfo
        return ResourcePackInfo.resourcePackInfo()
            .uri(uri)
            // A hash (SHA-1 hash of the resource pack ZIP file as a hex string) is required for a ResourcePackInfo.
            // We are computing this hash at runtime as we don't want to have users to provide their own.
            // In order to do this, computeHashAndBuild() needs to make a network request to download the pack
            // and compute the hash, hence the use of CompletableFuture.
            .computeHashAndBuild()
            .thenApply { packInfo ->
                // Build ResourcePackRequest
                ResourcePackRequest.resourcePackRequest()
                    .packs(packInfo)
                    .required(required)
                    .prompt(Component.text(prompt ?: fallbackPrompt))
                    .build()
            }
    }

    /**
     * Caches a [ResourcePackRequest].
     * @param worldName - The name of the world that this [ResourcePackRequest] belongs to.
     * If null, this will be treated as a [ResourcePackRequest] for global.
     */
    private fun cache(request: ResourcePackRequest, worldName: String? = null) {
        if (worldName != null) {
            worlds[worldName.lowercase()] = request
        } else {
            global = request
        }
    }
}