package me.naspo.packmanagerpro.commandstuff;

import me.naspo.packmanagerpro.PackManagerPro;
import me.naspo.packmanagerpro.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private PackManagerPro plugin;
    public Commands(PackManagerPro plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // --- PLAYER COMMAND LOGIC ---

        if (sender instanceof Player) {
            Player player = (Player) sender;

            // If no args.
            if (args.length == 0) {
                player.sendMessage(Utils.chatColor(Utils.prefix + "&7Did you mean &2/pmp reload&7?"));
                return true;
            }

            // Reload command.
            if (args[0].equalsIgnoreCase("reload")) {
                // Negative permission check.
                if (!(player.hasPermission("pmp.reload"))) {
                    player.sendMessage(Utils.prefix +
                            plugin.getConfig().getString("messages.no-permission"));
                    return true;
                }

                // Has permission, reload logic.
                Utils.reloadConfigs();
                player.sendMessage(Utils.chatColor(Utils.prefix +
                        plugin.getConfig().getString("messages.reload")));
                return true;
            }
        }

        // --- CONSOLE COMMAND LOGIC ---

        if (args.length == 0) {
            sender.sendMessage("Did you mean /pmp reload?");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            Utils.reloadConfigs();
            sender.sendMessage("PackManagerPro has been reloaded.");
            return true;
        }

        return false;
    }
}
