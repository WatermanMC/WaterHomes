package com.github.WatermanMC.WaterHomes;

import com.github.WatermanMC.WaterHomes.commands.*;
import com.github.WatermanMC.WaterHomes.commands.tabcompleter.*;
import com.github.WatermanMC.WaterHomes.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class WaterHomes extends JavaPlugin {

    private HomeManager homeManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.homeManager = new HomeManager(this, configManager);
        configManager.loadConfigs();
        registerCommands();
        registerCommandCompleter();
        getLogger().info("WaterHomes v" + getPluginMeta().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("WaterHomes v" + getPluginMeta().getVersion() + " disabled.");
    }

    public void registerCommands() {
        new DelHomeCommand(this, homeManager, configManager);
        new HomeCommand(this, homeManager, configManager);
        new HomeListCommand(this, homeManager, configManager);
        new SetHomeCommand(this, homeManager, configManager);
        new WaterHomesCommand(this, configManager);
    }

    public void registerCommandCompleter() {
        HomeCommandCompleter homeCompleter = new HomeCommandCompleter(this.homeManager);
        SetHomeCommandCompleter setHomeCommandCompleter = new SetHomeCommandCompleter(this);
        WaterHomesCommandCompleter waterHomesCommandCompleter = new WaterHomesCommandCompleter(this);

        getCommand("home").setTabCompleter(homeCompleter);

        getCommand("sethome").setTabCompleter(setHomeCommandCompleter);
        getCommand("delhome").setTabCompleter(homeCompleter);
        getCommand("waterhomes").setTabCompleter(waterHomesCommandCompleter);
    }

    public String getDiscordHelp() {
        return "Cant fix it? Join on our fast discord support: https://discord.gg/Scgqfm5EU4";
    }
}