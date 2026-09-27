package dev.naspo.packmanagerpro.listeners

import dev.naspo.packmanagerpro.PackManagerPro
import dev.naspo.packmanagerpro.sendresourcepack.sendResourcePack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class PlayerChangedWorldListener(val plugin: PackManagerPro) : Listener {

    @EventHandler
    private fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        val applicationType: String? = plugin.config.getString("application-type")

        if (plugin.config.getBoolean("enable-pack") && applicationType?.lowercase() == "per-world") {
            sendResourcePack(event.player, plugin)
        }
    }
}