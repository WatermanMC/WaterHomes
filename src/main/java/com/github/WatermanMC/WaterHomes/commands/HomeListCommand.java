package com.github.WatermanMC.WaterHomes.commands;

import com.github.WatermanMC.WaterHomes.managers.ConfigManager;
import com.github.WatermanMC.WaterHomes.managers.HomeManager;
import com.github.WatermanMC.WaterHomes.WaterHomes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class HomeListCommand implements CommandExecutor {

    private final WaterHomes plugin;
    private final HomeManager homeManager;
    private final ConfigManager configManager;
    private MiniMessage minimessage;

    public HomeListCommand(WaterHomes plugin,
                           HomeManager homeManager,
                           ConfigManager configManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.configManager = configManager;
        this.minimessage = MiniMessage.miniMessage();
        plugin.getCommand("homelist").setExecutor(this);
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

        if (!player.hasPermission("waterhomes.homelist")) {
            player.sendMessage(minimessage.deserialize(configManager.getMessage("nopermission")));
            return true;
        }

        Map<String, org.bukkit.Location> playerHomes = homeManager.getPlayerHomes(player.getUniqueId());

        if (playerHomes.isEmpty()) {
            String msg = configManager.getMessage("prefix") + configManager.getMessage("home.failed.nohomes");
            player.sendMessage(minimessage.deserialize(msg));
            return true;
        }

        String prefix = configManager.getMessage("prefix");
        String key = configManager.getMessage("homelist");
        String homesString = String.join(", ", playerHomes.keySet());

        player.sendMessage(minimessage.deserialize(prefix + key.replace("%homes%", homesString)));

        return true;
    }

    public WaterHomes getPlugin() {
        return plugin;
    }
}