package com.github.WatermanMC.WaterHomes;

import org.bukkit.command.PluginCommand;

public class CommandManager {

    private final WaterHomes plugin;

    public CommandManager(WaterHomes plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        PluginCommand homeCommand = plugin.getCommand("home");
        PluginCommand sethomeCommand = plugin.getCommand("sethome");
        PluginCommand delhomeCommand = plugin.getCommand("delhome"); // Add this
        PluginCommand homelistCommand = plugin.getCommand("homelist");
        PluginCommand waterhomesCommand = plugin.getCommand("waterhomes");

        if (homeCommand != null) {
            homeCommand.setExecutor(new HomeCommand(plugin));
        }
        if (sethomeCommand != null) {
            sethomeCommand.setExecutor(new SetHomeCommand(plugin));
        }
        if (delhomeCommand != null) { // Add this
            delhomeCommand.setExecutor(new DelHomeCommand(plugin));
        }
        if (homelistCommand != null) {
            homelistCommand.setExecutor(new HomeListCommand(plugin));
        }
        if (waterhomesCommand != null) {
            waterhomesCommand.setExecutor(new WaterHomesCommand(plugin));
        }
    }
}