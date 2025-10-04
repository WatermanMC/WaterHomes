package com.github.WatermanMC.WaterHomes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Map;

public class SetHomeCommand implements CommandExecutor {
    
    private final WaterHomes plugin;
    private final HomeManager homeManager;
    private final ConfigManager configManager;
    
    public SetHomeCommand(WaterHomes plugin) {
        this.plugin = plugin;
        this.homeManager = plugin.getHomeManager();
        this.configManager = plugin.getConfigManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        
        if (!player.hasPermission("waterhomes.sethome")) {
            String message = configManager.getMessages().getString("prefix") + "&cYou don't have permission to set homes!";
            player.sendMessage(configManager.colorize(message));
            return true;
        }

        boolean canSetNamedHomes = player.hasPermission("waterhomes.sethome.unlimited") || 
                                  homeManager.getHomeLimit(player) > 1;

        String homeName = "home";
        if (args.length > 0) {
            if (!canSetNamedHomes) {
                player.sendMessage(configManager.colorize(configManager.getMessages().getString("sethome.failed.noname")));
                return true;
            }
            homeName = args[0].toLowerCase();
        }

        if (homeManager.checkBannedWords(homeName, player)) {
            player.sendMessage(configManager.colorize(configManager.getMessages().getString("sethome.failed.banword")));
            return true;
        }

        if (homeManager.isLocationUnsafe(player.getLocation(), player)) {
            player.sendMessage(configManager.colorize(configManager.getMessages().getString("sethome.failed.unsafe")));
            return true;
        }

        int homeLimit = homeManager.getHomeLimit(player);
        Map<String, org.bukkit.Location> playerHomes = homeManager.getPlayerHomes(player.getUniqueId());
        
        if (homeLimit > 0 && playerHomes.size() >= homeLimit && !playerHomes.containsKey(homeName)) {
            player.sendMessage(configManager.colorize(configManager.getMessages().getString("prefix") + "&cYou've reached your home limit!"));
            return true;
        }

        playerHomes.put(homeName, player.getLocation());
        homeManager.savePlayerHomes(player.getUniqueId(), playerHomes);
        
        player.sendMessage(configManager.colorize(configManager.getMessages().getString("sethome.success")));
        return true;
    }
}