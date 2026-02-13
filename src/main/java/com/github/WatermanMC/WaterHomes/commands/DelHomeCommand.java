package com.github.WatermanMC.WaterHomes.commands;

import com.github.WatermanMC.WaterHomes.managers.*;
import com.github.WatermanMC.WaterHomes.WaterHomes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Map;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class DelHomeCommand implements CommandExecutor {

    private final HomeManager homeManager;
    private final ConfigManager configManager;
    private MiniMessage minimessage;

    public DelHomeCommand(WaterHomes plugin,
                          HomeManager homeManager,
                          ConfigManager configManager) {
        this.homeManager = homeManager;
        this.configManager = configManager;
        this.minimessage = MiniMessage.miniMessage();
        plugin.getCommand("delhome").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (@NotNull Player) sender;

        if (!player.hasPermission("waterhomes.delhome")) {
            String msg = configManager.getMessage("prefix") + configManager.getMessage("nopermission");
            player.sendMessage(minimessage.deserialize(msg));
            return true;
        }

        Map<String, org.bukkit.Location> playerHomes = homeManager.getPlayerHomes(player.getUniqueId());

        if (playerHomes.isEmpty()) {
            String msg = configManager.getMessage("prefix") + configManager.getMessage("delhome.failed.nohomes");
            player.sendMessage(minimessage.deserialize(msg));
            return true;
        }

        String homeName = "home";

        boolean canHaveNamedHomes = player.hasPermission("waterhomes.sethome.unlimited") || homeManager.getHomeLimit(player) > 1;

        if (canHaveNamedHomes && playerHomes.size() > 1) {
            if (args.length == 0) {
                String msg = configManager.getMessage("prefix") + configManager.getMessage("delhome.failed.noname");
                player.sendMessage(minimessage.deserialize(msg));
                return true;
            }
            homeName = args[0].toLowerCase();
        } else if (args.length > 0) {
            homeName = args[0].toLowerCase();
        }

        if (!playerHomes.containsKey(homeName)) {
            String notFoundMessage = configManager.getMessage("delhome.failed.notfound");
            if (notFoundMessage != null) {
                notFoundMessage = notFoundMessage.replace("%home%", homeName);
            } else {
                notFoundMessage = "<red>Home '<yellow>" + homeName + "<red>' not found!";
            }
            player.sendMessage(minimessage.deserialize(configManager.getMessage("prefix") + notFoundMessage));

            if (canHaveNamedHomes && !playerHomes.isEmpty()) {
                player.sendMessage(minimessage.deserialize(configManager.getMessage("prefix") + "<gray>Your homes: <yellow>" + String.join(", ", playerHomes.keySet())));
            }
            return true;
        }

        if (homeManager.deleteHome(player.getUniqueId(), homeName)) {
            String successMessage = configManager.getMessage("delhome.success");
            if (successMessage != null) {
                successMessage = successMessage.replace("%home%", homeName);
            } else {
                successMessage = "<green>Home '<yellow>" + homeName + "<green>' has been deleted!";
            }
            player.sendMessage(minimessage.deserialize(configManager.getMessage("prefix") + successMessage));
        } else {
            String failMessage = configManager.getMessage("delhome.failed.fail");
            if (failMessage != null) {
                failMessage = failMessage.replace("%home%", homeName);
            } else {
                failMessage = "<red>Failed to delete <yellow>" + homeName + "<red>.";
            }
            player.sendMessage(minimessage.deserialize(configManager.getMessage("prefix") + failMessage));
        }
        return true;
    }
}