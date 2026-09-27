package dev.naspo.packmanagerpro.commands

import dev.naspo.packmanagerpro.PackManagerPro
import dev.naspo.packmanagerpro.messages.sendPlayerMessage
import dev.naspo.packmanagerpro.messages.sendPlayerPrefixedMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(val plugin: PackManagerPro) : CommandExecutor {

    val didYouMeanReloadMessageFormatted = "<gray>Did you mean <gold>/pmp reload<gray>?"
    val didYouMeanReloadMessagePlain = "Did you mean /pmp reload?"

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        // Player commands
        if (sender is Player) {
            // The only command is the reload command, so perform the one-and-only permission check here.
            if (!sender.hasPermission("pmp.reload")) {
                sendPlayerMessage(sender, plugin.config.getString("messages.no-permission")
                    ?: "<red>You do not have permission!")
                return false
            }

            // If there is anything but 1 arg, send "did you mean /pmp reload?" message.
            if (args.size != 1) {
                sendPlayerPrefixedMessage(sender, didYouMeanReloadMessageFormatted, plugin.config)
                return false
            }

            // Reload command
            if (args[0].lowercase() == "reload") {
                plugin.reloadConfig()
                sendPlayerPrefixedMessage(
                    sender,
                    plugin.config.getString("messages.reload") ?: "<gray>PackManagerPro has been reloaded.",
                    plugin.config
                )
                return true
            } else {
                // If args[0] is not "reload", send "did you mean /pmp reload?" message.
                sendPlayerPrefixedMessage(sender, didYouMeanReloadMessageFormatted, plugin.config)
            }
        } else {
            // Console commands
            if (args.size != 1) {
                // If there is anything but 1 arg, send "did you mean /pmp reload?" message.
                sender.sendMessage(didYouMeanReloadMessagePlain)
                return false
            } else if (args[0].lowercase() == "reload") {
                // Reload command
                plugin.reloadConfig()
                sender.sendMessage("PackManagerPro has been reloaded.")
                return true
            } else {
                // If args[0] is not "reload", send "did you mean /pmp reload?" message.
                sender.sendMessage(didYouMeanReloadMessagePlain)
            }
        }
        return false
    }
}