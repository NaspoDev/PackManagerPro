package me.naspo.packmanagerpro;

import org.bukkit.ChatColor;

public class Utils {

    public static String prefix;
    private static PackManagerPro plugin;

    Utils(PackManagerPro plugin) {
        Utils.plugin = plugin;

        reloadVars();
    }

    public static void reloadConfigs() {
        plugin.reloadConfig();
        reloadVars();
    }

    private static void reloadVars() {
        prefix = plugin.getConfig().getString("messages.prefix");
    }

    public static String chatColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
