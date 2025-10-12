package com.github.WatermanMC.WaterHomes;

import org.bukkit.plugin.java.JavaPlugin;

public class WaterHomes extends JavaPlugin {

    private HomeManager homeManager;
    private ConfigManager configManager;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.homeManager = new HomeManager(this);
        this.commandManager = new CommandManager(this);
        configManager.loadConfigs();
        commandManager.registerCommands();
        getLogger().info("Enabling WaterHomes...");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling WaterHomes...");
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}