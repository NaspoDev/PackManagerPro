package dev.naspo.packmanagerpro.sendresourcepack

import dev.naspo.packmanagerpro.PackManagerPro
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.Component
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.net.URI
import java.util.logging.Level

// Deals with sending resource packs to the player.

/**
 * Send a resource pack to a player based on the current plugin configuration (i.e. global/per-world).
 * @param player - The player to send the resource pack to.
 * @param plugin - Instance of the plugin.
 */
fun sendResourcePack(player: Player, plugin: PackManagerPro) {
    // Get the appropriate ResourcePackRequest based on the application type.
    val applicationType: String? = plugin.config.getString("application-type")

    val resourcePackRequest: ResourcePackRequest? = when (applicationType?.lowercase()) {
        "global" -> globalResourcePackRequest(plugin.config)
        "per-world" -> worldSpecificResourcePackRequest(plugin, player)
        else -> {
            plugin.logger.log(Level.SEVERE, "Cannot send resource pack!")
            plugin.logger.log(Level.SEVERE, "'enable-pack' is true but 'application-type' is not valid.")
            return
        }
    }

    // A null ResourcePackRequest at this stage will trigger clearing of server resource packs for the player.
    // This is the preferred behaviour for a few reasons, but mainly for the following:
    // When a player switches worlds in a per-world application, if there is no resource pack set for that world,
    // any active packs from previous worlds should clear.
    if (resourcePackRequest == null) {
        player.clearResourcePacks()
        return
    }

    // Send the resource pack to the player.
    player.sendResourcePacks(resourcePackRequest)
}

/**
 * Builds and returns a [ResourcePackRequest] based on the global resource pack.
 */
private fun globalResourcePackRequest(config: FileConfiguration): ResourcePackRequest? {
    // Build ResourcePackInfo
    val packUrl: String = config.getString("global-application.pack-url") ?: return null
    val packInfo = ResourcePackInfo.resourcePackInfo()
        .uri(URI.create(packUrl))
        .build()

    // Build ResourcePackRequest
    val required: Boolean = config.getBoolean("global-application.force-pack")
    val prompt: String = config.getString("global-application.prompt-message") ?: "Please download the resource pack."
    return ResourcePackRequest.resourcePackRequest()
        .packs(packInfo)
        .required(required)
        .prompt(Component.text(prompt))
        .build()
}

/**
 * Builds and returns a [ResourcePackRequest] based on the world-specific resource pack for
 * the world of the provided player.
 */
private fun worldSpecificResourcePackRequest(plugin: PackManagerPro, player: Player): ResourcePackRequest? {
    // In the "per-world-application" config section, get the key for the world that matches the
    // one that the player is currently in.
    val worldKey: String? = plugin.config.getConfigurationSection("per-world-application")
        ?.getKeys(false)
        ?.firstOrNull { player.location.world.name == it }

    if (worldKey == null) return null

    val packUrl: String = plugin.config.getString("per-world-application.$worldKey.pack-url") ?: return null
    val packInfo = ResourcePackInfo.resourcePackInfo()
        .uri(URI.create(packUrl))
        .build()

    // Build ResourcePackRequest
    val required: Boolean = plugin.config.getBoolean("per-world-application.$worldKey.force-pack")
    val prompt: String = plugin.config.getString("per-world-application.$worldKey.prompt-message")
        ?: "Please download the resource pack."

    return ResourcePackRequest.resourcePackRequest()
        .packs(packInfo)
        .required(required)
        .prompt(Component.text(prompt))
        .build()
}