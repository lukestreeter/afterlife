package me.yodeling_goat.afterlifeplugin.stats;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsManager {
    private static StatsManager instance;
    private final Map<UUID, PlayerStats> playerStats = new HashMap<>();
    private File statsFile;
    private FileConfiguration statsConfig;
    
    private StatsManager() {
        loadStats();
    }
    
    public static StatsManager getInstance() {
        if (instance == null) {
            instance = new StatsManager();
        }
        return instance;
    }
    
    private void loadStats() {
        statsFile = new File(org.bukkit.Bukkit.getPluginManager().getPlugin("AfterLifePlugin").getDataFolder(), "stats.yml");
        if (!statsFile.exists()) {
            try {
                statsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
        loadPlayerStats();
    }
    
    private void loadPlayerStats() {
        if (statsConfig.contains("players")) {
            for (String uuidString : statsConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                int kills = statsConfig.getInt("players." + uuidString + ".kills", 0);
                int deaths = statsConfig.getInt("players." + uuidString + ".deaths", 0);
                int animalsKilled = statsConfig.getInt("players." + uuidString + ".animals_killed", 0);
                int itemsCrafted = statsConfig.getInt("players." + uuidString + ".items_crafted", 0);
                playerStats.put(uuid, new PlayerStats(kills, deaths, animalsKilled, itemsCrafted));
            }
        }
    }
    
    public void saveStats() {
        for (Map.Entry<UUID, PlayerStats> entry : playerStats.entrySet()) {
            String uuidString = entry.getKey().toString();
            PlayerStats stats = entry.getValue();
            statsConfig.set("players." + uuidString + ".kills", stats.getKills());
            statsConfig.set("players." + uuidString + ".deaths", stats.getDeaths());
            statsConfig.set("players." + uuidString + ".animals_killed", stats.getAnimalsKilled());
            statsConfig.set("players." + uuidString + ".items_crafted", stats.getItemsCrafted());
        }
        
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public PlayerStats getPlayerStats(Player player) {
        return playerStats.computeIfAbsent(player.getUniqueId(), k -> new PlayerStats(0, 0));
    }
    
    public void addKill(Player player) {
        PlayerStats stats = getPlayerStats(player);
        stats.addKill();
        // Don't save immediately - will be saved on plugin disable
    }
    
    public void addDeath(Player player) {
        PlayerStats stats = getPlayerStats(player);
        stats.addDeath();
        // Don't save immediately - will be saved on plugin disable
    }
    
    public void addAnimalKill(Player player) {
        PlayerStats stats = getPlayerStats(player);
        stats.addAnimalKill();
        // Don't save immediately - will be saved on plugin disable
    }

    public void addItemCrafted(Player player) {
        PlayerStats stats = getPlayerStats(player);
        stats.addItemCrafted();
        // Don't save immediately - will be saved on plugin disable
    }
    
    public static class PlayerStats {
        private int kills;
        private int deaths;
        private int animalsKilled;
        private int itemsCrafted;
        
        public PlayerStats(int kills, int deaths) {
            this.kills = kills;
            this.deaths = deaths;
            this.animalsKilled = 0;
            this.itemsCrafted = 0;
        }
        
        public PlayerStats(int kills, int deaths, int animalsKilled) {
            this.kills = kills;
            this.deaths = deaths;
            this.animalsKilled = animalsKilled;
            this.itemsCrafted = 0;
        }
        
        public PlayerStats(int kills, int deaths, int animalsKilled, int itemsCrafted) {
            this.kills = kills;
            this.deaths = deaths;
            this.animalsKilled = animalsKilled;
            this.itemsCrafted = itemsCrafted;
        }
        
        public int getKills() {
            return kills;
        }
        
        public int getDeaths() {
            return deaths;
        }
        
        public int getAnimalsKilled() {
            return animalsKilled;
        }
        
        public int getItemsCrafted() {
            return itemsCrafted;
        }
        
        public double getKDRatio() {
            if (deaths == 0) {
                return kills;
            }
            return (double) kills / deaths;
        }
        
        public void addKill() {
            kills++;
        }
        
        public void addDeath() {
            deaths++;
        }
        
        public void addAnimalKill() {
            animalsKilled++;
        }

        public void addItemCrafted() {
            itemsCrafted++;
        }
    }
} 