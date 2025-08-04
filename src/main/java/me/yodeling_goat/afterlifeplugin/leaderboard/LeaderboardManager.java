package me.yodeling_goat.afterlifeplugin.leaderboard;

import me.yodeling_goat.afterlifeplugin.stats.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardManager {
    private static LeaderboardManager instance;
    
    private LeaderboardManager() {}
    
    public static LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }
    
    public enum StatType {
        KILLS("Kills"),
        DEATHS("Deaths"),
        KDR("K/D Ratio"),
        ANIMALS_KILLED("Animals Killed"),
        HOSTILE_MOBS_KILLED("Hostile Mobs Killed"),
        BLOCKS_MINED("Blocks Mined"),
        WARDEN_KILLED("Warden Kills"),
        ENDER_DRAGON_KILLED("Ender Dragon Kills"),
        WITHER_KILLED("Wither Kills"),
        ITEMS_CRAFTED("Items Crafted"),
        XP_COLLECTED("XP Collected");
        
        private final String displayName;
        
        StatType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public List<LeaderboardEntry> getTopPlayers(StatType statType, int limit) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        
        // Get stats from all players who have data saved
        StatsManager statsManager = StatsManager.getInstance();
        
        // Load stats from saved data for all players
        for (UUID playerUuid : statsManager.getAllPlayerUuids()) {
            String playerName = statsManager.getPlayerName(playerUuid);
            if (playerName != null) {
                StatsManager.PlayerStats stats = statsManager.getPlayerStats(playerUuid);
                int value = getStatValue(stats, statType);
                entries.add(new LeaderboardEntry(playerName, value, playerUuid));
            }
        }
        
        // Sort by value (descending)
        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        // Return top N entries
        return entries.stream().limit(limit).collect(Collectors.toList());
    }
    
    private int getStatValue(StatsManager.PlayerStats stats, StatType statType) {
        switch (statType) {
            case KILLS:
                return stats.getKills();
            case DEATHS:
                return stats.getDeaths();
            case KDR:
                return (int) (stats.getKDRatio() * 100); // Convert to integer for sorting
            case ANIMALS_KILLED:
                return stats.getAnimalsKilled();
            case HOSTILE_MOBS_KILLED:
                return stats.getHostileMobsKilled();
            case BLOCKS_MINED:
                return stats.getBlocksMined();
            case WARDEN_KILLED:
                return stats.getWardenKilled();
            case ENDER_DRAGON_KILLED:
                return stats.getEnderDragonKilled();
            case WITHER_KILLED:
                return stats.getWitherKilled();
            case ITEMS_CRAFTED:
                return stats.getItemsCrafted();
            case XP_COLLECTED:
                return stats.getXpCollected();
            default:
                return 0;
        }
    }
    
    public static class LeaderboardEntry {
        private final String playerName;
        private final int value;
        private final UUID playerUuid;
        
        public LeaderboardEntry(String playerName, int value, UUID playerUuid) {
            this.playerName = playerName;
            this.value = value;
            this.playerUuid = playerUuid;
        }
        
        public String getPlayerName() {
            return playerName;
        }
        
        public int getValue() {
            return value;
        }
        
        public UUID getPlayerUuid() {
            return playerUuid;
        }
    }
} 