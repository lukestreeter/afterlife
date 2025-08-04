package me.yodeling_goat.afterlifeplugin.stats;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
                int kills = statsConfig.getInt("players." + uuidString + ".kills", 0);
                int deaths = statsConfig.getInt("players." + uuidString + ".deaths", 0);
                int animalsKilled = statsConfig.getInt("players." + uuidString + ".animals_killed", 0);
                int hostileMobsKilled = statsConfig.getInt("players." + uuidString + ".hostile_mobs_killed", 0);
                int blocksMined = statsConfig.getInt("players." + uuidString + ".blocks_mined", 0);
                int wardenKilled = statsConfig.getInt("players." + uuidString + ".warden_killed", 0);
                int enderDragonKilled = statsConfig.getInt("players." + uuidString + ".ender_dragon_killed", 0);
                int witherKilled = statsConfig.getInt("players." + uuidString + ".wither_killed", 0);
                int itemsCrafted = statsConfig.getInt("players." + uuidString + ".items_crafted", 0);
                int xpCollected = statsConfig.getInt("players." + uuidString + ".xp_collected", 0);
                playerStats.put(uuid, new PlayerStats(kills, deaths, animalsKilled, itemsCrafted, xpCollected, hostileMobsKilled, blocksMined, wardenKilled, enderDragonKilled, witherKilled));
            }
        }
    }
    
    public void saveStats() {
        for (Map.Entry<UUID, PlayerStats> entry : playerStats.entrySet()) {
            String uuidString = entry.getKey().toString();
            PlayerStats stats = entry.getValue();
            
            // Save player name if available
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                statsConfig.set("players." + uuidString + ".name", player.getName());
            }
            
            statsConfig.set("players." + uuidString + ".kills", stats.getKills());
            statsConfig.set("players." + uuidString + ".deaths", stats.getDeaths());
            statsConfig.set("players." + uuidString + ".animals_killed", stats.getAnimalsKilled());
            statsConfig.set("players." + uuidString + ".hostile_mobs_killed", stats.getHostileMobsKilled());
            statsConfig.set("players." + uuidString + ".blocks_mined", stats.getBlocksMined());
            statsConfig.set("players." + uuidString + ".warden_killed", stats.getWardenKilled());
            statsConfig.set("players." + uuidString + ".ender_dragon_killed", stats.getEnderDragonKilled());
            statsConfig.set("players." + uuidString + ".wither_killed", stats.getWitherKilled());
            statsConfig.set("players." + uuidString + ".items_crafted", stats.getItemsCrafted());
            statsConfig.set("players." + uuidString + ".xp_collected", stats.getXpCollected());
        }
        
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public PlayerStats getPlayerStats(Player player) {
        return playerStats.computeIfAbsent(player.getUniqueId(), k -> new PlayerStats(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
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

    public void addHostileMobKill(Player player) {
        PlayerStats stats = getPlayerStats(player);
        stats.addHostileMobKill();
        // Don't save immediately - will be saved on plugin disable
    }

    public void addBlockMined(Player player) {
        PlayerStats stats = getPlayerStats(player);
        stats.addBlockMined();
        // Don't save immediately - will be saved on plugin disable
    }

    public void addWardenKill(Player player) {
        PlayerStats stats = getPlayerStats(player);
        stats.addWardenKill();
        // Don't save immediately - will be saved on plugin disable
    }

    public void addEnderDragonKill(Player player) {
        PlayerStats stats = getPlayerStats(player);
        stats.addEnderDragonKill();
        // Don't save immediately - will be saved on plugin disable
    }

    public void addWitherKill(Player player) {
        PlayerStats stats = getPlayerStats(player);
        stats.addWitherKill();
        // Don't save immediately - will be saved on plugin disable
    }

    public void resetPlayerStats(Player player) {
        playerStats.put(player.getUniqueId(), new PlayerStats(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        saveStats();
    }

    public void clearPlayerStats(Player player) {
        playerStats.remove(player.getUniqueId());
        saveStats();
    }

    public void clearAllStats() {
        playerStats.clear();
        saveStats();
    }
    
    public Set<UUID> getAllPlayerUuids() {
        return new HashSet<>(playerStats.keySet());
    }
    
    public String getPlayerName(UUID playerUuid) {
        // Try to get the player name from the config file
        if (statsConfig.contains("players." + playerUuid.toString() + ".name")) {
            return statsConfig.getString("players." + playerUuid.toString() + ".name");
        }
        
        // If not found in config, try to get from online player
        Player player = Bukkit.getPlayer(playerUuid);
        if (player != null) {
            return player.getName();
        }
        
        return null;
    }
    
    public PlayerStats getPlayerStats(UUID playerUuid) {
        return playerStats.getOrDefault(playerUuid, new PlayerStats(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    }

    public static class PlayerStats {
        private int kills;
        private int deaths;
        private int animalsKilled;
        private int itemsCrafted;
        private int xpCollected;
        private int hostileMobsKilled;
        private int blocksMined;
        private int wardenKilled;
        private int enderDragonKilled;
        private int witherKilled;

        public PlayerStats(int kills, int deaths, int animalsKilled, int itemsCrafted, int xpCollected, int hostileMobsKilled, int blocksMined, int wardenKilled, int enderDragonKilled, int witherKilled) {
            this.kills = kills;
            this.deaths = deaths;
            this.animalsKilled = animalsKilled;
            this.itemsCrafted = itemsCrafted;
            this.xpCollected = xpCollected;
            this.hostileMobsKilled = hostileMobsKilled;
            this.blocksMined = blocksMined;
            this.wardenKilled = wardenKilled;
            this.enderDragonKilled = enderDragonKilled;
            this.witherKilled = witherKilled;
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
        
        public int getHostileMobsKilled() {
            return hostileMobsKilled;
        }

        public int getBlocksMined() {
            return blocksMined;
        }

        public int getWardenKilled() {
            return wardenKilled;
        }

        public int getEnderDragonKilled() {
            return enderDragonKilled;
        }

        public int getWitherKilled() {
            return witherKilled;
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

        public void addHostileMobKill() {
            hostileMobsKilled++;
        }

        public void addBlockMined() {
            blocksMined++;
        }

        public void addWardenKill() {
            wardenKilled++;
        }

        public void addEnderDragonKill() {
            enderDragonKilled++;
        }

        public void addWitherKill() {
            witherKilled++;
        }
    }
} 