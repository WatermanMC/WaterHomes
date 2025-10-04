package com.github.WatermanMC.WaterHomes;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {
    
    private final WaterHomes plugin;
    private FileConfiguration messages;
    
    public ConfigManager(WaterHomes plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfigs() {
        plugin.saveDefaultConfig();
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public FileConfiguration getMessages() {
        return messages;
    }
    
    public void reloadConfigs() {
        plugin.reloadConfig();
        
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public String colorize(String message) {
        return message.replace('&', '§');
    }
}