package dev.naspo.packmanagerpro.resourcepack

import dev.naspo.packmanagerpro.PackManagerPro
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.Component
import java.net.URI
import java.util.logging.Level

class ResourcePackRequestCache(val plugin: PackManagerPro) {
    var global: ResourcePackRequest? = null
        private set

    var worlds = mapOf<String, ResourcePackRequest>()
        private set

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
        val prompt: String = plugin.config.getString("global-application.prompt-message") ?: fallbackPrompt

        // Build ResourcePackInfo
        ResourcePackInfo.resourcePackInfo()
            .uri(URI.create(packUrl))
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
                    .prompt(Component.text(prompt))
                    .build()

                this.global = resourcePackRequest
            }
            .exceptionally {
                plugin.logger.log(Level.SEVERE, "Failed to compute hash for global resource pack.")
                null
            }
    }

    private fun refreshWorldsCache() {

    }
}