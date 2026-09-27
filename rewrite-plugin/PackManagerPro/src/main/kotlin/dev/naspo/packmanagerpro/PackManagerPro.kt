package dev.naspo.packmanagerpro

import dev.naspo.packmanagerpro.commands.Commands
import dev.naspo.packmanagerpro.commands.TabCompleter
import dev.naspo.packmanagerpro.listeners.PlayerChangedWorldListener
import dev.naspo.packmanagerpro.listeners.PlayerJoinListener
import org.bukkit.plugin.java.JavaPlugin

class PackManagerPro : JavaPlugin() {

    override fun onEnable() {
        this.saveDefaultConfig()
        this.config.options().copyDefaults(true)
        this.saveConfig()

        this.logger.info("PackManagerPro has been enabled!")

        registerEvents()
        registerCommands()
    }

    override fun onDisable() {
        this.logger.info("PackManagerPro has been disabled.")
    }

    private fun registerEvents() {
        this.server.pluginManager.registerEvents(PlayerJoinListener(this), this)
        this.server.pluginManager.registerEvents(PlayerChangedWorldListener(this), this)
    }

    private fun registerCommands() {
        this.getCommand("pmp")?.setExecutor(Commands(this))
        this.getCommand("pmp")?.tabCompleter = TabCompleter()
    }
}
