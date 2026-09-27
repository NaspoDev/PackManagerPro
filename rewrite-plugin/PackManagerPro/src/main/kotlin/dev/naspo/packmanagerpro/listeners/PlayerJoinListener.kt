package dev.naspo.packmanagerpro.listeners

import dev.naspo.packmanagerpro.PackManagerPro
import dev.naspo.packmanagerpro.sendresourcepack.sendResourcePack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(val plugin: PackManagerPro) : Listener {

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        val player: Player = event.player

        // If enable-pack is true, send the player a resource pack.
        if (!plugin.config.getBoolean("enable-pack")) {
            sendResourcePack(player, plugin)
        } else {
            // TODO: test without this. i.e. test if players automatically keep the resource pack on after leaving and re-joining
            // Otherwise clear any active server resource packs that the player may have already had activated.
            player.clearResourcePacks()
        }
    }
}