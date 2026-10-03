package dev.naspo.packmanagerpro.resourcepack

import dev.naspo.packmanagerpro.PackManagerPro
import dev.naspo.packmanagerpro.applicationtype.ApplicationType
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.text.Component
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
fun sendResourcePack(player: Player, resourcePackRequestCache: ResourcePackRequestCache, plugin: PackManagerPro) {
    // Get the appropriate ResourcePackRequest based on the application type.
    val applicationType: ApplicationType? = applicationType(plugin)
    if (applicationType == null) {
        plugin.logger.log(Level.SEVERE, "Cannot send resource pack! 'application-type' is not valid.")
        return
    }

    val resourcePackRequest: ResourcePackRequest? = when (applicationType) {
        ApplicationType.GLOBAL -> resourcePackRequestCache.global
        ApplicationType.PER_WORLD -> resourcePackRequestCache.worlds[player.location.world.name.lowercase()]
    }

    // A null ResourcePackRequest at this stage will trigger clearing of server resource packs for the player.
    // This is the preferred behaviour for a few reasons, but mainly for the following:
    // When a player switches worlds in a per-world application, if there is no resource pack set for that world,
    // any active packs from previous worlds should clear.
    if (resourcePackRequest == null) {
        player.clearResourcePacks()
        return
    } else {
        // Send the resource pack to the player.
        player.sendResourcePacks(resourcePackRequest)
    }
}

// -- Private Helpers --

/**
 * Reads, parses, and returns the application type from the config as a type-safe [ApplicationType].
 * @return The parsed, valid application type. Or null if it's invalid.
 */
private fun applicationType(plugin: PackManagerPro): ApplicationType? {
    val applicationType: String = plugin.config.getString("application-type") ?: return null
    return ApplicationType.fromString(applicationType)
}