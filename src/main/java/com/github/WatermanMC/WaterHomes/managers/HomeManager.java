package com.github.WatermanMC.WaterHomes.managers;

import com.github.WatermanMC.WaterHomes.WaterHomes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class HomeManager {

    private final WaterHomes plugin;
    private final ConfigManager configManager;
    private File homesFile;
    private YamlConfiguration homesConfig;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitRunnable> teleportTasks = new HashMap<>();
    private final Map<UUID, Integer> pendingTeleports = new ConcurrentHashMap<>();

    private final Object fileLock = new Object();

    public HomeManager(WaterHomes plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        setupHomesFile();
    }

    public void reloadHomes() {
        synchronized (fileLock) {
            homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        }
    }

    private void setupHomesFile() {
        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!homesFile.exists()) {
            try {
                homesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe(e.getMessage());
                plugin.getLogger().severe("Could not make homes.yml!");
                plugin.getLogger().warning("Make sure the file exists and the server has permission to read it.");
                plugin.getLogger().warning(plugin.getDiscordHelp());
            }
        }
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
    }

    public synchronized void saveHomes() {
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.getLogger().warning("FILE ERROR: Could not read " + homesFile + "!");
            plugin.getLogger().warning("Make sure the file exists and the server has permission to read it.");
            plugin.getLogger().warning(plugin.getDiscordHelp());
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Critical error during config reload!", e);
            plugin.getLogger().severe(plugin.getDiscordHelp());
        }
    }

    public Map<String, Location> getPlayerHomes(@NotNull UUID playerId) {
        Map<String, Location> homes = new HashMap<>();
        org.bukkit.configuration.ConfigurationSection section = homesConfig.getConfigurationSection(playerId.toString());

        if (section != null) {
            for (String homeName : section.getKeys(false)) {
                Object locObj = section.get(homeName);
                if (locObj instanceof Location) {
                    homes.put(homeName, (Location) locObj);
                } else if (section.isConfigurationSection(homeName)) {
                    homes.put(homeName, Location.deserialize(section.getConfigurationSection(homeName).getValues(false)));
                }
            }
        }
        return homes;
    }

    public void savePlayerHomes(@NotNull UUID playerId, @NotNull Map<String, Location> homes) {
        synchronized (fileLock) {
            String path = playerId.toString();
            homesConfig.set(path, null);

            for (Map.Entry<String, Location> entry : homes.entrySet()) {
                homesConfig.set(path + "." + entry.getKey(), entry.getValue());
            }

            saveHomes();
            reloadHomes();
        }
    }

    public boolean deleteHome(@NotNull UUID playerId, @NotNull String homeName) {
        synchronized (fileLock) {
            Map<String, Location> playerHomes = getPlayerHomes(playerId);

            if (playerHomes.containsKey(homeName)) {
                playerHomes.remove(homeName);
                savePlayerHomes(playerId, playerHomes);
                return true;
            }
            return false;
        }
    }

    public int getHomeLimit(@NotNull Player player) {
        FileConfiguration config = plugin.getConfig();

        if (player.hasPermission("waterhomes.sethome.unlimited")) {
            return -1;
        }

        String path = "home-group-limits";
        if (configManager.getConfig().getConfigurationSection(path) != null) {
            for (String group : configManager.getConfig().getConfigurationSection(path).getKeys(false)) {
                if (player.hasPermission("waterhomes.sethome." + group)) {
                    return configManager.getConfig().getInt(path + "." + group);
                }
            }
        }

        return configManager.getConfig().getInt(path + ".default", 1);
    }

    public boolean isLocationUnsafe(@NotNull Location location, @NotNull Player player) {
        if (player.hasPermission("waterhomes.unsafeblocks.bypass")) {
            return false;
        }

        FileConfiguration config = plugin.getConfig();
        List<String> unsafeBlocks = configManager.getConfig().getStringList("unsafe-blocks");
        int radius = 2;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location checkLoc = location.clone().add(x, y, z);
                    Material material = checkLoc.getBlock().getType();

                    String materialName = material.name();
                    if (unsafeBlocks.stream().anyMatch(s -> s.equalsIgnoreCase(materialName))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkBannedWords(@NotNull String homeName, @NotNull Player player) {
        if (player.hasPermission("waterhomes.bannedwords.bypass")) {
            return false;
        }

        FileConfiguration config = plugin.getConfig();
        List<String> bannedWords = configManager.getConfig().getStringList("banned-words");
        String lowerHomeName = homeName.toLowerCase();

        for (String bannedWord : bannedWords) {
            try {
                if (lowerHomeName.matches(".*" + bannedWord + ".*")) {
                    return true;
                }
            } catch (Exception e) {
                if (lowerHomeName.contains(bannedWord.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<UUID, Long> getCooldowns() {
        return cooldowns;
    }

    public Map<UUID, Integer> getPendingTeleports() {
        return pendingTeleports;
    }

    public Map<UUID, BukkitRunnable> getTeleportTasks() {
        return teleportTasks;
    }
}