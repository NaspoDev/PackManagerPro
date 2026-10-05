package dev.naspo.packmanagerpro.listeners

import dev.naspo.packmanagerpro.PackManagerPro
import dev.naspo.packmanagerpro.applicationtype.ApplicationType
import dev.naspo.packmanagerpro.resourcepack.ResourcePackRequestCache
import dev.naspo.packmanagerpro.resourcepack.sendResourcePack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangedWorldListener(
    private val plugin: PackManagerPro,
    private val resourcePackRequestCache: ResourcePackRequestCache
) : Listener {

    @EventHandler
    private fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        val rawApplicationType: String = plugin.config.getString("application-type") ?: return
        val applicationType: ApplicationType = ApplicationType.fromString(rawApplicationType) ?: return

        if (plugin.config.getBoolean("enable-pack") && applicationType == ApplicationType.PER_WORLD) {
            sendResourcePack(event.player, resourcePackRequestCache, plugin)
        }
    }
}