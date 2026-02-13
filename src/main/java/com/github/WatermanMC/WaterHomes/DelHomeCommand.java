package com.github.WatermanMC.WaterHomes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Map;

public class DelHomeCommand implements CommandExecutor {

    private final WaterHomes plugin;
    private final HomeManager homeManager;
    private final ConfigManager configManager;

    public DelHomeCommand(WaterHomes plugin) {
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

        if (!player.hasPermission("waterhomes.delhome")) {
            String message = configManager.getMessages().getString("prefix") + "&cYou don't have permission to delete homes!";
            player.sendMessage(configManager.colorize(message));
            return true;
        }

        Map<String, org.bukkit.Location> playerHomes = homeManager.getPlayerHomes(player.getUniqueId());

        if (playerHomes.isEmpty()) {
            player.sendMessage(configManager.colorize(configManager.getMessages().getString("delhome.failed.nohomes")));
            return true;
        }

        String homeName = "home";

        boolean canHaveNamedHomes = player.hasPermission("waterhomes.sethome.unlimited") ||
                homeManager.getHomeLimit(player) > 1;

        if (canHaveNamedHomes && playerHomes.size() > 1) {
            if (args.length == 0) {
                player.sendMessage(configManager.colorize(configManager.getMessages().getString("delhome.failed.noname")));
                player.sendMessage(configManager.colorize("&7Available homes: &e" + String.join(", ", playerHomes.keySet())));
                return true;
            }
            homeName = args[0].toLowerCase();
        } else if (args.length > 0) {
            homeName = args[0].toLowerCase();
        }

        if (!playerHomes.containsKey(homeName)) {
            String message = configManager.getMessages().getString("delhome.failed.notfound");
            if (message != null) {
                message = message.replace("%home%", homeName);
            } else {
                message = "&cHome '&e" + homeName + "&c' not found!";
            }
            player.sendMessage(configManager.colorize(configManager.getMessages().getString("prefix") + message));

            if (canHaveNamedHomes && !playerHomes.isEmpty()) {
                player.sendMessage(configManager.colorize("&7Your homes: &e" + String.join(", ", playerHomes.keySet())));
            }
            return true;
        }

        if (homeManager.deleteHome(player.getUniqueId(), homeName)) {
            String successMessage = configManager.getMessages().getString("delhome.success");
            if (successMessage != null) {
                successMessage = successMessage.replace("%home%", homeName);
            } else {
                successMessage = "&aHome '&e" + homeName + "&a' has been deleted!";
            }
            player.sendMessage(configManager.colorize(configManager.getMessages().getString("prefix") + successMessage));
        } else {
            player.sendMessage(configManager.colorize(configManager.getMessages().getString("prefix") + "&cFailed to delete home!"));
        }

        return true;
    }
}