package com.github.WatermanMC.WaterHomes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Map;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {

    private final WaterHomes plugin;
    private final HomeManager homeManager;
    private final ConfigManager configManager;

    public HomeCommand(WaterHomes plugin) {
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

        if (!player.hasPermission("waterhomes.home")) {
            String message = configManager.getMessages().getString("prefix") + "&cYou don't have permission to use homes!";
            player.sendMessage(configManager.colorize(message));
            return true;
        }

        FileConfiguration config = plugin.getConfig();
        if (!player.hasPermission("waterhomes.cooldown.bypass") && config.getBoolean("tp-cooldown.enabled")) {
            Map<UUID, Long> cooldowns = homeManager.getCooldowns();
            long cooldownTime = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            long currentTime = System.currentTimeMillis();
            long cooldownDuration = config.getLong("tp-cooldown.duration") * 1000;

            if (currentTime - cooldownTime < cooldownDuration) {
                long remaining = (cooldownDuration - (currentTime - cooldownTime)) / 1000;
                String message = configManager.getMessages().getString("home.cooldown").replace("%seconds%", String.valueOf(remaining));
                player.sendMessage(configManager.colorize(message));
                return true;
            }
        }

        Map<String, Location> playerHomes = homeManager.getPlayerHomes(player.getUniqueId());
        String homeName = "home";

        if (args.length > 0) {
            if (playerHomes.size() > 1 || player.hasPermission("waterhomes.sethome.unlimited") ||
                    homeManager.getHomeLimit(player) > 1) {
                homeName = args[0].toLowerCase();
            }
        }

        Location home = playerHomes.get(homeName);
        if (home == null) {
            player.sendMessage(configManager.colorize(configManager.getMessages().getString("home.failed.invalidhome")));
            return true;
        }

        if (!player.hasPermission("waterhomes.tpdelay.bypass") && config.getBoolean("tp-delay.enabled")) {
            int delay = config.getInt("tp-delay.duration");
            player.sendMessage(configManager.colorize(configManager.getMessages().getString("home.delay")));

            int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                homeManager.getPendingTeleports().remove(player.getUniqueId());
                player.teleport(home);
                player.sendMessage(configManager.colorize(configManager.getMessages().getString("home.success")));

                if (!player.hasPermission("waterhomes.cooldown.bypass") && config.getBoolean("tp-cooldown.enabled")) {
                    homeManager.getCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
                }
            }, delay * 20L).getTaskId();

            homeManager.getPendingTeleports().put(player.getUniqueId(), taskId);
        } else {
            player.teleport(home);
            player.sendMessage(configManager.colorize(configManager.getMessages().getString("home.success")));

            if (!player.hasPermission("waterhomes.cooldown.bypass") && config.getBoolean("tp-cooldown.enabled")) {
                homeManager.getCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
            }
        }

        return true;
    }
}