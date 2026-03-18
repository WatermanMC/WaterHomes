package com.github.WatermanMC.WaterHomes.commands;

import com.github.WatermanMC.WaterHomes.managers.ConfigManager;
import com.github.WatermanMC.WaterHomes.managers.HomeManager;
import com.github.WatermanMC.WaterHomes.WaterHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class HomeCommand implements CommandExecutor, Listener {

    private final WaterHomes plugin;
    private final HomeManager homeManager;
    private final ConfigManager configManager;
    private MiniMessage minimessage;
    private final Map<UUID, Location> teleportLocation = new HashMap<>();

    public HomeCommand(WaterHomes plugin,
                       HomeManager homeManager,
                       ConfigManager configManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.configManager = configManager;
        this.minimessage = MiniMessage.miniMessage();
        plugin.getCommand("home").setExecutor(this);
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

        if (!player.hasPermission("waterhomes.home")) {
            player.sendMessage(minimessage.deserialize(configManager.getMessage("nopermission")));
            return true;
        }

        FileConfiguration config =  configManager.getConfig();
        if (!player.hasPermission("waterhomes.cooldown.bypass") && configManager.getConfig().getBoolean("tp-cooldown.enabled")) {
            Map<UUID, Long> cooldowns = homeManager.getCooldowns();
            long cooldownTime = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            long currentTime = System.currentTimeMillis();
            long cooldownDuration = configManager.getConfig().getLong("tp-cooldown.duration") * 1000;

            if (currentTime - cooldownTime < cooldownDuration) {
                long remaining = (cooldownDuration - (currentTime - cooldownTime)) / 1000;
                String msg = configManager.getMessage("prefix") + configManager.getMessage("home.cooldown").replace("%seconds%", String.valueOf(remaining));
                player.sendMessage(minimessage.deserialize(msg));
                return true;
            }
        }

        Map<String, Location> playerHomes = homeManager.getPlayerHomes(player.getUniqueId());
        String homeName = (args.length > 0) ? args[0].toLowerCase() : "home";

        if (args.length > 0) {
            if (playerHomes.size() > 1 || player.hasPermission("waterhomes.sethome.unlimited") ||
                    homeManager.getHomeLimit(player) > 1) {
                homeName = args[0].toLowerCase();
            }
        }

        Location home = playerHomes.get(homeName);
        if (home == null) {
            String msg = configManager.getMessage("prefix") + configManager.getMessage("home.failed.invalidhome");
            player.sendMessage(minimessage.deserialize(msg));
            return true;
        }

        if (!player.hasPermission("waterhomes.tpdelay.bypass") && configManager.getConfig().getBoolean("tp-delay.enabled")) {
            int delay = configManager.getConfig().getInt("tp-delay.duration");
            String msg = configManager.getMessage("prefix") + configManager.getMessage("home.delay");
            player.sendMessage(minimessage.deserialize(msg));

            int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                homeManager.getTeleportTasks().remove(player.getUniqueId());

                performTeleport(player, home);

                if (!player.hasPermission("waterhomes.cooldown.bypass") && configManager.getConfig().getBoolean("tp-cooldown.enabled")) {
                    homeManager.getCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
                }
            }, delay * 20L).getTaskId();

            homeManager.getPendingTeleports().put(player.getUniqueId(), taskId);
        } else {
            player.teleportAsync(home);
            String msg = configManager.getMessage("prefix") + configManager.getMessage("home.success");
            player.sendMessage(minimessage.deserialize(msg));

            if (!player.hasPermission("waterhomes.cooldown.bypass") && configManager.getConfig().getBoolean("tp-cooldown.enabled")) {
                homeManager.getCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
        return true;
    }

    private void performTeleport(Player player, Location home) {
        player.teleportAsync(home);
        String msg = configManager.getMessage("prefix") + configManager.getMessage("home.success");
        player.sendMessage(minimessage.deserialize(msg));
    }

    private void cancelTeleport(Player player, String reason) {
        if (homeManager.getTeleportTasks().containsKey(player.getUniqueId())) {
            homeManager.getTeleportTasks().get(player.getUniqueId()).cancel();
            homeManager.getTeleportTasks().remove(player.getUniqueId());
            homeManager.getTeleportTasks().remove(player.getUniqueId());
            String msg = configManager.getMessage("prefix") + configManager.getMessage(reason);
            player.sendMessage(minimessage.deserialize(msg));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("waterhomes.tpdelay.bypass")) {
            return;
        }

        if (configManager.getConfig().getBoolean("tp-delay.cancel-on-move")) {
            if (teleportLocation.containsKey(player.getUniqueId())) {
                Location from = event.getFrom();
                Location to = event.getTo();
                if (event.hasChangedPosition()) {
                    cancelTeleport(player, configManager.getMessage("home.failed.delaymoved"));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Player player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;

        if (player.hasPermission("waterhomes.tpdelay.bypass")) {
            return;
        }

        if (configManager.getConfig().getBoolean("tp-delay.cancel-on-move")) {
            if (event.getEntity() instanceof Player) {
                if (teleportLocation.containsKey(player.getUniqueId())) {
                    cancelTeleport(player, configManager.getMessage("home.failed.delaymoved"));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (homeManager.getTeleportTasks().containsKey(uuid)) {
            homeManager.getTeleportTasks().get(uuid).cancel();
            homeManager.getTeleportTasks().remove(uuid);
        }
        teleportLocation.remove(uuid);
    }
}