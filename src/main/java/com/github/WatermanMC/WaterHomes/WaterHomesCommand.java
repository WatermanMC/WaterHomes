package com.github.WatermanMC.WaterHomes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WaterHomesCommand implements CommandExecutor {

    private final WaterHomes plugin;
    private final ConfigManager configManager;

    public WaterHomesCommand(WaterHomes plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("waterhomes.admin")) {
            String message = configManager.getMessages().getString("prefix") + "&cYou don't have permission to use this command!";
            player.sendMessage(configManager.colorize(message));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(configManager.colorize("&bWaterHomes &f- &7Version " + plugin.getDescription().getVersion()));
            player.sendMessage(configManager.colorize("&7Authors: &f" + String.join(", ", plugin.getDescription().getAuthors())));
            player.sendMessage(configManager.colorize("&7Usage: &f/waterhomes <reload|info>"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                configManager.reloadConfigs();
                player.sendMessage(configManager.colorize(configManager.getMessages().getString("prefix") + "&aConfiguration reloaded!"));
                break;

            case "info":
                player.sendMessage(configManager.colorize("&bWaterHomes &f- &7Version " + plugin.getDescription().getVersion()));
                player.sendMessage(configManager.colorize("&7Authors: &f" + String.join(", ", plugin.getDescription().getAuthors())));
                player.sendMessage(configManager.colorize("&7Website: &f" + plugin.getDescription().getWebsite()));
                break;

            default:
                player.sendMessage(configManager.colorize(configManager.getMessages().getString("prefix") + "&cUsage: /waterhomes <reload|info>"));
                break;
        }

        return true;
    }
}