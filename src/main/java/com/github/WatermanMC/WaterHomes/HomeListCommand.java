package com.github.WatermanMC.WaterHomes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Map;

public class HomeListCommand implements CommandExecutor {
    
    private final WaterHomes plugin;
    private final HomeManager homeManager;
    private final ConfigManager configManager;
    
    public HomeListCommand(WaterHomes plugin) {
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
        
        if (!player.hasPermission("waterhomes.homelist")) {
            String message = configManager.getMessages().getString("prefix") + "&cYou don't have permission to view home list!";
            player.sendMessage(configManager.colorize(message));
            return true;
        }

        Map<String, org.bukkit.Location> playerHomes = homeManager.getPlayerHomes(player.getUniqueId());
        
        List<String> homelistMessages = configManager.getMessages().getStringList("homelist");
        for (String message : homelistMessages) {
            if (message.contains("%home%")) {
                for (String homeName : playerHomes.keySet()) {
                    player.sendMessage(configManager.colorize(message.replace("%home%", homeName)));
                }
            } else {
                player.sendMessage(configManager.colorize(message));
            }
        }
        
        return true;
    }
}