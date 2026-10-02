package dev.naspo.packmanagerpro.resourcepack

import dev.naspo.packmanagerpro.PackManagerPro
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.Component
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level
import javax.naming.spi.ResolveResult

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

    // Private backing field for 'worlds'.
    // It's a ConcurrentHashMap as this is modified from another thread.
    // (Specifically in ResourcePackInfo.computeHashAndBuild()'s CompletableFuture. See this done below).
    private var _worlds: ConcurrentHashMap<String, ResourcePackRequest> = ConcurrentHashMap()
    /** The cached ResourcePackRequests for the world-specific resource packs. */
    val worlds: Map<String, ResourcePackRequest> get() = _worlds

    // TODO: is this even needed? What happens if I dont set a fallback prompt/prompt at all?
    // The fallback resource pack download prompt to use if one isn't set in the config.
    private val fallbackPrompt: String = "Please download the resource pack."

    fun refreshCache() {
        refreshGlobalCache()
        refreshWorldsCache()
    }

    private fun refreshGlobalCache() {
        val packUrl: String? = plugin.config.getString("global-application.pack-url")

        if (packUrl == null) {
            this.global = null
            return
        }

        // Extract more pack info.
        val required: Boolean = plugin.config.getBoolean("global-application.force-pack")
        val prompt: String? = plugin.config.getString("global-application.prompt-message")

        cachePackRequest(packUrl, required, prompt)
    }

    private fun refreshWorldsCache() {
        val worldKeys: Set<String>? = plugin.config
            .getConfigurationSection("per-world-application")
            ?.getKeys(false)

        if (worldKeys == null) {
            _worlds.clear()
            plugin.logger.log(
                Level.SEVERE, "Attempt to cache world-specific resource packs failed because" +
                        " the 'per-world-application' configuration section could not be found!"
            )
            return
        }

        // Only retain keys for worlds in the cache that still have a configuration section. (Drop everything else).
        _worlds.keys.retainAll(worldKeys)

        // For every world config section, extract its pack info and cache it.
        for (world in worldKeys) {
            val packUrl: String? = plugin.config.getString("per-world-application.$world.pack-url")

            if (packUrl == null) {
                _worlds.remove(world)
                continue
            }

            // Extract more pack info.
            val required: Boolean = plugin.config.getBoolean("per-world-application.$world.force-pack")
            val prompt: String? = plugin.config.getString("per-world-application.$world.prompt-message")

            cachePackRequest(packUrl, required, prompt, world)
        }
    }

    /**
     * Builds and caches a ResourcePackRequest from the provided parameters.
     * @param url - The url of the resource pack.
     * @param required - Whether the resource pack should be required or not.
     * @param prompt - The custom download resource pack prompt to use.
     * @param worldName - The name of the world that this resource pack belongs to. If null, this will be treated
     * as a global resource pack.
     */
    private fun cachePackRequest(
        url: String,
        required: Boolean,
        prompt: String? = null,
        worldName: String? = null
    ) {
        // URL parsing
        val uri: URI
        try {
            uri = URI.create(url)
        } catch (e: IllegalArgumentException) {
            val errorMessage = if (worldName != null) {
                "Failed to parse resource pack URL for world $worldName. Is its formatting valid?"
            } else {
                "Failed to parse global resource pack URL. Is its formatting valid?"
            }

            plugin.logger.log(Level.SEVERE, errorMessage, e)
            return
        }

        // Build ResourcePackInfo
        ResourcePackInfo.resourcePackInfo()
            .uri(uri)
            // A hash (SHA-1 hash of the resource pack ZIP file as a hex string) is required for a ResourcePackInfo.
            // We are computing this hash at runtime as we don't want to have users to provide their own.
            // In order to do this, computeHashAndBuild() needs to make a network request to download the pack
            // and compute the hash, hence the use of CompletableFuture.
            .computeHashAndBuild()
            .thenAccept { packInfo ->
                // Build ResourcePackRequest
                val resourcePackRequest = ResourcePackRequest.resourcePackRequest()
                    .packs(packInfo)
                    .required(required)
                    .prompt(Component.text(prompt ?: fallbackPrompt))
                    .build()

                if (worldName != null) {
                    this._worlds[worldName] = resourcePackRequest
                } else {
                    this.global = resourcePackRequest
                }
            }
            .exceptionally {
                val errorMessage = if (worldName != null) {
                    "Failed to compute hash for world $worldName resource pack."
                } else {
                    "Failed to compute hash for global resource pack."
                }

                plugin.logger.log(Level.SEVERE, errorMessage, it)
                null
            }
    }
}