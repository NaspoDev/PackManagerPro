package me.naspo.packmanagerpro.sendpacks;

import me.naspo.packmanagerpro.PackManagerPro;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

public class ApplicationManager implements Listener {

    private PackManagerPro plugin;
    private GlobalApplication globalApp;
    private PerWorldApplication perWorldApp;

    public ApplicationManager(PackManagerPro plugin, GlobalApplication globalApp, PerWorldApplication perWorldApp) {
        this.plugin = plugin;
        this.globalApp = globalApp;
        this.perWorldApp = perWorldApp;
    }

    // PlayerJoinEvent can be applied to both application types.
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // If enable-pack is false.
        if (!(plugin.getConfig().getBoolean("enable-pack"))) {
            return;
        }

        // Check the application type, then send the pack respectively.
        switch (plugin.getConfig().getString("application-type").toLowerCase()) {
            case "global": {
                globalApp.sendPack(player);
                break;
            }
            case "per-world": {
                perWorldApp.sendPack(player);
                break;
            }
            default: {
                plugin.getLogger().log(Level.WARNING, "Will not try to send resource pack...");
                plugin.getLogger().log(Level.WARNING, "enable-pack is true but application-type is not valid!");
                break;
            }
        }
    }

    // PlayerChangedWorldEvent used only for per world application type.
    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {
        // If enable-pack is false.
        if (!(plugin.getConfig().getBoolean("enable-pack"))) {
            return;
        }

        // If application type is per world, send the pack.
        if (plugin.getConfig().getString("application-type").equalsIgnoreCase("per-world")) {
            perWorldApp.sendPack(event.getPlayer());
        }
    }
}
