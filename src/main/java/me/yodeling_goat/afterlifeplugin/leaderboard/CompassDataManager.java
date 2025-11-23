package me.yodeling_goat.afterlifeplugin.leaderboard;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CompassDataManager {
    private static CompassDataManager instance;
    private final Set<UUID> playersWithCompass = new HashSet<>();
    private File compassDataFile;
    private FileConfiguration compassDataConfig;
    
    private CompassDataManager() {
        loadCompassData();
    }
    
    public static CompassDataManager getInstance() {
        if (instance == null) {
            instance = new CompassDataManager();
        }
        return instance;
    }
    
    private void loadCompassData() {
        compassDataFile = new File(org.bukkit.Bukkit.getPluginManager().getPlugin("AfterLifePlugin").getDataFolder(), "compass_data.yml");
        if (!compassDataFile.exists()) {
            try {
                compassDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compassDataConfig = YamlConfiguration.loadConfiguration(compassDataFile);
        loadPlayersWithCompass();
    }
    
    private void loadPlayersWithCompass() {
        if (compassDataConfig.contains("players_with_compass")) {
            for (String uuidString : compassDataConfig.getStringList("players_with_compass")) {
                try {
                    playersWithCompass.add(UUID.fromString(uuidString));
                } catch (IllegalArgumentException e) {
                    // Invalid UUID, skip
                }
            }
        }
    }
    
    public boolean hasPlayerReceivedCompass(Player player) {
        return playersWithCompass.contains(player.getUniqueId());
    }
    
    public void markPlayerAsReceivedCompass(Player player) {
        playersWithCompass.add(player.getUniqueId());
        saveCompassData();
    }
    
    private void saveCompassData() {
        compassDataConfig.set("players_with_compass", 
            playersWithCompass.stream()
                .map(UUID::toString)
                .toList());
        
        try {
            compassDataConfig.save(compassDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 