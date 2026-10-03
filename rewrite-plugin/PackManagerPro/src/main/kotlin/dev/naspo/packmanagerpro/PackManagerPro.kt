package dev.naspo.packmanagerpro

import dev.naspo.packmanagerpro.commands.Commands
import dev.naspo.packmanagerpro.commands.TabCompleter
import dev.naspo.packmanagerpro.listeners.PlayerChangedWorldListener
import dev.naspo.packmanagerpro.listeners.PlayerJoinListener
import dev.naspo.packmanagerpro.resourcepack.ResourcePackRequestCache
import org.bukkit.plugin.java.JavaPlugin

class PackManagerPro : JavaPlugin() {
    private lateinit var resourcePackRequestCache: ResourcePackRequestCache

    override fun onEnable() {
        this.saveDefaultConfig()
        this.config.options().copyDefaults(true)
        this.saveConfig()

        initializeClasses()
        registerEvents()
        registerCommands()

        resourcePackRequestCache.refreshCache()

        this.logger.info("PackManagerPro has been enabled!")
    }

    override fun onDisable() {
        this.logger.info("PackManagerPro has been disabled.")
    }

    private fun initializeClasses() {
        resourcePackRequestCache = ResourcePackRequestCache(this)
    }

    private fun registerEvents() {
        this.server.pluginManager.registerEvents(PlayerJoinListener(this), this)
        this.server.pluginManager.registerEvents(PlayerChangedWorldListener(this), this)
    }

    private fun registerCommands() {
        this.getCommand("pmp")?.setExecutor(Commands(this, resourcePackRequestCache))
        this.getCommand("pmp")?.tabCompleter = TabCompleter()
    }
}
