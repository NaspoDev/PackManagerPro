package me.naspo.packmanagerpro.sendpacks;

import me.naspo.packmanagerpro.PackManagerPro;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class PerWorldApplication {

    private PackManagerPro plugin;
    public PerWorldApplication(PackManagerPro plugin) {
        this.plugin = plugin;
    }

    // Sends the resource pack request.
    void sendPack(Player player) {
        World world = player.getWorld();

        // Gets and cycles through all the configured worlds.
        plugin.getConfig().getConfigurationSection("per-world-application")
                .getKeys(false)
                .forEach(key -> {
                    // If the player's world matches the key, send the pack with the key's settings.
                    if (key.equalsIgnoreCase(world.getName())) {
                        try {
                            player.setResourcePack(plugin.getConfig().getString("per-world-application."
                                            + key + ".pack-url"),
                                    null,
                                    plugin.getConfig().getString("per-world-application." + key
                                            + ".prompt-message"),
                                    plugin.getConfig().getBoolean("per-world-application." + key
                                            + ".force-pack"));
                        } catch (Exception e) {
                            plugin.getLogger().log(Level.WARNING, "Error trying to send the resource pack!");
                            e.printStackTrace();
                        }
                    }
                });
    }
}
