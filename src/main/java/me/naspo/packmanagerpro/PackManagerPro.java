package me.naspo.packmanagerpro;

import me.naspo.packmanagerpro.commandstuff.Commands;
import me.naspo.packmanagerpro.commandstuff.TabCompleter;
import me.naspo.packmanagerpro.sendpacks.ApplicationManager;
import me.naspo.packmanagerpro.sendpacks.GlobalApplication;
import me.naspo.packmanagerpro.sendpacks.PerWorldApplication;
import org.bukkit.plugin.java.JavaPlugin;

public final class PackManagerPro extends JavaPlugin {

    private Utils utils;
    private Commands commands;
    private TabCompleter tabCompleter;

    private GlobalApplication globalApplication;
    private PerWorldApplication perWorldApplication;
    private ApplicationManager applicationManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.getLogger().info("PackManagerPro has been enabled!");

        instantiateClasses();
        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
        this.getLogger().info("PackManagerPro has been disabled.");
    }

    private void instantiateClasses() {
        utils = new Utils(this);
        commands = new Commands(this);
        tabCompleter = new TabCompleter();

        globalApplication = new GlobalApplication(this);
        perWorldApplication = new PerWorldApplication(this);
        applicationManager = new ApplicationManager(this, globalApplication, perWorldApplication);
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(applicationManager, this);
    }

    private void registerCommands() {
        this.getCommand("pmp").setExecutor(commands);
        this.getCommand("pmp").setTabCompleter(tabCompleter);
    }
}
