package dev.naspo.packmanagerpro.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompleter : TabCompleter {

    // List of valid arguments to tab complete.
    // (It's only "reload").
    val arguments = mutableListOf("reload")

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String?>? {
        // Permission check.
        if (!sender.hasPermission("pmp.reload")) return null

        // Tab completion logic
        val result = mutableListOf<String>()
        if (args.size == 1) {
            for (s in arguments) {
                if (s.lowercase().startsWith(args[0].lowercase())) {
                    result.add(s)
                }
            }
            return result
        }
        return null
    }
}