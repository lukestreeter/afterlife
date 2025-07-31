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
    
    private int getPlayerStat(String playerUuid, String stat) {
        return statsConfig.getInt("players." + playerUuid + "." + stat, 0);
    }

    private void setPlayerStat(String playerUuid, String stat, int value) {
        statsConfig.set("players." + playerUuid + "." + stat, value);
    }

    private void loadPlayerStats() {
        if (statsConfig.contains("players")) {
            for (String uuidString : statsConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                int kills = getPlayerStat(uuidString, "kills");
                int deaths = getPlayerStat(uuidString, "deaths");
                int animalsKilled = getPlayerStat(uuidString, "animals_killed");
                int itemsCrafted = getPlayerStat(uuidString, "items_crafted");
                int xpCollected = getPlayerStat(uuidString, "xp_collected");
                playerStats.put(uuid, new PlayerStats(kills, deaths, animalsKilled, itemsCrafted, xpCollected));
            }
        }
    }
    
    public void saveStats() {
        for (Map.Entry<UUID, PlayerStats> entry : playerStats.entrySet()) {
            String uuidString = entry.getKey().toString();
            PlayerStats stats = entry.getValue();
            setPlayerStat(uuidString, "kills", stats.getKills());
            setPlayerStat(uuidString, "deaths", stats.getDeaths());
            setPlayerStat(uuidString, "animals_killed", stats.getAnimalsKilled());
            setPlayerStat(uuidString, "items_crafted", stats.getItemsCrafted());
            setPlayerStat(uuidString, "xp_collected", stats.getXpCollected());
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
    
    public void addXpCollected(Player player, int xpAmount) {
        PlayerStats stats = getPlayerStats(player);
        stats.addXpCollected(xpAmount);
        // Don't save immediately - will be saved on plugin disable
    }
    
    public static class PlayerStats {
        private int kills;
        private int deaths;
        private int animalsKilled;
        private int itemsCrafted;
        private int xpCollected;
        
        public PlayerStats(int kills, int deaths) {
            this.kills = kills;
            this.deaths = deaths;
            this.animalsKilled = 0;
            this.itemsCrafted = 0;
            this.xpCollected = 0;
        }
        
        public PlayerStats(int kills, int deaths, int animalsKilled) {
            this.kills = kills;
            this.deaths = deaths;
            this.animalsKilled = animalsKilled;
            this.itemsCrafted = 0;
            this.xpCollected = 0;
        }
        
        public PlayerStats(int kills, int deaths, int animalsKilled, int itemsCrafted) {
            this.kills = kills;
            this.deaths = deaths;
            this.animalsKilled = animalsKilled;
            this.itemsCrafted = itemsCrafted;
            this.xpCollected = 0;
        }
        
        public PlayerStats(int kills, int deaths, int animalsKilled, int itemsCrafted, int xpCollected) {
            this.kills = kills;
            this.deaths = deaths;
            this.animalsKilled = animalsKilled;
            this.itemsCrafted = itemsCrafted;
            this.xpCollected = xpCollected;
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
        
        public int getXpCollected() {
            return xpCollected;
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

        public void addXpCollected(int amount) {
            xpCollected += amount;
        }
    }
} 