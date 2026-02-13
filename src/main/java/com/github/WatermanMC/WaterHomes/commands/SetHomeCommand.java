package com.github.WatermanMC.WaterHomes.commands;

import com.github.WatermanMC.WaterHomes.managers.ConfigManager;
import com.github.WatermanMC.WaterHomes.managers.HomeManager;
import com.github.WatermanMC.WaterHomes.WaterHomes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SetHomeCommand implements CommandExecutor {

    private final WaterHomes plugin;
    private final HomeManager homeManager;
    private final ConfigManager configManager;
    private MiniMessage minimessage;

    public SetHomeCommand(WaterHomes plugin,
                          HomeManager homeManager,
                          ConfigManager configManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.configManager = configManager;
        this.minimessage = MiniMessage.miniMessage();
        plugin.getCommand("sethome").setExecutor(this);
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

        if (!player.hasPermission("waterhomes.sethome")) {
            String msg = configManager.getMessage("prefix") + configManager.getMessage("nopermission");
            player.sendMessage(minimessage.deserialize(msg));
            return true;
        }

        boolean canSetNamedHomes = player.hasPermission("waterhomes.sethome.unlimited") ||
                homeManager.getHomeLimit(player) > 1;

        String homeName = "home";
        if (args.length > 0) {
            if (!canSetNamedHomes) {
                String msg = configManager.getMessage("prefix") + configManager.getMessage("sethome.failed.noname");
                player.sendMessage(minimessage.deserialize(msg));
                return true;
            }
            homeName = args[0].toLowerCase();
        }

        if (homeManager.checkBannedWords(homeName, player)) {
            String msg = configManager.getMessage("prefix") + configManager.getMessage("sethome.failed.banword");
            player.sendMessage(minimessage.deserialize(msg));
            return true;
        }

        if (homeManager.isLocationUnsafe(player.getLocation(), player)) {
            String msg = configManager.getMessage("prefix") + configManager.getMessage("sethome.failed.unsafe");
            player.sendMessage(minimessage.deserialize(msg));
            return true;
        }

        int homeLimit = homeManager.getHomeLimit(player);
        Map<String, org.bukkit.Location> playerHomes = homeManager.getPlayerHomes(player.getUniqueId());

        if (homeLimit > 0 && playerHomes.size() >= homeLimit && !playerHomes.containsKey(homeName)) {
            String msg = configManager.getMessage("prefix") + configManager.getMessage("sethome.failed.reachedmaximum");
            player.sendMessage(minimessage.deserialize(msg));
            return true;
        }

        playerHomes.put(homeName, player.getLocation());
        Map<String, org.bukkit.Location> snapshot = new java.util.HashMap<>(playerHomes);
        homeManager.savePlayerHomes(player.getUniqueId(), snapshot);

        String msg = configManager.getMessage("prefix") + configManager.getMessage("home.success");
        player.sendMessage(minimessage.deserialize(msg));

        return true;
    }

    public WaterHomes getPlugin() {
        return plugin;
    }
}