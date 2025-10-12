package com.github.WatermanMC.WaterHomes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {

    private final WaterHomes plugin;
    private File homesFile;
    private YamlConfiguration homesConfig;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> pendingTeleports = new ConcurrentHashMap<>();

    public HomeManager(WaterHomes plugin) {
        this.plugin = plugin;
        setupHomesFile();
    }

    private void setupHomesFile() {
        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!homesFile.exists()) {
            try {
                homesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
    }

    public void saveHomes() {
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Location> getPlayerHomes(UUID playerId) {
        Map<String, Location> homes = new HashMap<>();

        if (homesConfig.contains(playerId.toString())) {
            Map<String, Object> serializedHomes = (Map<String, Object>) homesConfig.get(playerId.toString());
            for (Map.Entry<String, Object> entry : serializedHomes.entrySet()) {
                homes.put(entry.getKey(), Location.deserialize((Map<String, Object>) entry.getValue()));
            }
        }

        return homes;
    }

    public void savePlayerHomes(UUID playerId, Map<String, Location> homes) {
        Map<String, Object> serializedHomes = new HashMap<>();
        for (Map.Entry<String, Location> entry : homes.entrySet()) {
            serializedHomes.put(entry.getKey(), entry.getValue().serialize());
        }

        homesConfig.set(playerId.toString(), serializedHomes);
        saveHomes();
    }

    public boolean deleteHome(UUID playerId, String homeName) {
        Map<String, Location> playerHomes = getPlayerHomes(playerId);

        if (playerHomes.containsKey(homeName)) {
            playerHomes.remove(homeName);
            savePlayerHomes(playerId, playerHomes);
            return true;
        }

        return false;
    }

    public int getHomeLimit(Player player) {
        FileConfiguration config = plugin.getConfig();

        if (player.hasPermission("waterhomes.sethome.unlimited")) {
            return -1;
        }

        if (config.getConfigurationSection("home-limits") != null) {
            for (String rank : config.getConfigurationSection("home-limits").getKeys(false)) {
                if (player.hasPermission("waterhomes.sethome." + rank)) {
                    return config.getInt("home-limits." + rank);
                }
            }
        }

        return config.getInt("home-limits.default", 1);
    }

    public boolean isLocationUnsafe(Location location, Player player) {
        if (player.hasPermission("waterhomes.unsafeblocks.bypass")) {
            return false;
        }

        FileConfiguration config = plugin.getConfig();
        List<String> unsafeBlocks = config.getStringList("unsafe-blocks");
        int radius = 2;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location checkLoc = location.clone().add(x, y, z);
                    Material material = checkLoc.getBlock().getType();

                    if (unsafeBlocks.contains(material.name())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkBannedWords(String homeName, Player player) {
        if (player.hasPermission("waterhomes.bannedwords.bypass")) {
            return false;
        }

        FileConfiguration config = plugin.getConfig();
        List<String> bannedWords = config.getStringList("banned-words");
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

    public void cancelPendingTeleport(Player player) {
        Integer taskId = pendingTeleports.remove(player.getUniqueId());
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}