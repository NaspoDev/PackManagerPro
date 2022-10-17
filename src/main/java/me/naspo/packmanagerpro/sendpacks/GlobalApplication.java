package me.naspo.packmanagerpro.sendpacks;

import me.naspo.packmanagerpro.PackManagerPro;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class GlobalApplication {

    private PackManagerPro plugin;
    public GlobalApplication(PackManagerPro plugin) {
        this.plugin = plugin;
    }

    // Sends the resource pack request.
    void sendPack(Player player) {
        try {
            player.setResourcePack(plugin.getConfig().getString("global-application.pack-url"),
                    null,
                    plugin.getConfig().getString("global-application.prompt-message"),
                    plugin.getConfig().getBoolean("global-application.force-pack"));
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error trying to send the resource pack!");
            e.printStackTrace();
        }
    }
}
