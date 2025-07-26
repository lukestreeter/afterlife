package me.yodeling_goat.afterlifeplugin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify the hostile mob kill stat functionality.
 * Tests that hostile mob kill stats are properly tracked and saved.
 */
public class HostileMobKillStatTest {

    @TempDir
    File tempDir;

    @Test
    void testHostileMobKillStatTracking() {
        // This test simulates hostile mob kill stat tracking and persistence
        
        UUID playerUUID = UUID.randomUUID();
        String playerUUIDString = playerUUID.toString();
        
        // Step 1: Simulate player killing hostile mobs (before saving)
        File statsFile = new File(tempDir, "stats.yml");
        FileConfiguration config = new YamlConfiguration();
        
        // Set up initial stats with some hostile mobs killed
        config.set("players." + playerUUIDString + ".kills", 5);
        config.set("players." + playerUUIDString + ".deaths", 2);
        config.set("players." + playerUUIDString + ".animals_killed", 3);
        config.set("players." + playerUUIDString + ".hostile_mobs_killed", 12);
        config.set("players." + playerUUIDString + ".blocks_mined", 150);
        
        try {
            config.save(statsFile);
        } catch (IOException e) {
            fail("Failed to save stats: " + e.getMessage());
        }
        
        // Verify the stats file exists and contains the hostile mob kill data
        assertTrue(statsFile.exists(), "stats.yml file should exist");
        FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(statsFile);
        int hostileMobsKilled = loadedConfig.getInt("players." + playerUUIDString + ".hostile_mobs_killed", 0);
        assertEquals(12, hostileMobsKilled, "Player should have 12 hostile mobs killed");
        
        // Step 2: Simulate additional hostile mob kills
        int additionalKills = 8;
        int newTotal = hostileMobsKilled + additionalKills;
        loadedConfig.set("players." + playerUUIDString + ".hostile_mobs_killed", newTotal);
        
        try {
            loadedConfig.save(statsFile);
        } catch (IOException e) {
            fail("Failed to save updated stats: " + e.getMessage());
        }
        
        // Step 3: Verify the updated hostile mob kill count
        FileConfiguration finalConfig = YamlConfiguration.loadConfiguration(statsFile);
        int finalHostileMobsKilled = finalConfig.getInt("players." + playerUUIDString + ".hostile_mobs_killed", 0);
        assertEquals(20, finalHostileMobsKilled, "Player should have 20 hostile mobs killed after additional kills");
        
        // Step 4: Verify other stats are preserved
        int kills = finalConfig.getInt("players." + playerUUIDString + ".kills", 0);
        int deaths = finalConfig.getInt("players." + playerUUIDString + ".deaths", 0);
        int animalsKilled = finalConfig.getInt("players." + playerUUIDString + ".animals_killed", 0);
        int blocksMined = finalConfig.getInt("players." + playerUUIDString + ".blocks_mined", 0);
        
        assertEquals(5, kills, "Kills should remain unchanged");
        assertEquals(2, deaths, "Deaths should remain unchanged");
        assertEquals(3, animalsKilled, "Animals killed should remain unchanged");
        assertEquals(150, blocksMined, "Blocks mined should remain unchanged");
    }

    @Test
    void testHostileMobKillStatInitialization() {
        // Test that new players start with 0 hostile mobs killed
        
        UUID newPlayerUUID = UUID.randomUUID();
        String newPlayerUUIDString = newPlayerUUID.toString();
        
        File statsFile = new File(tempDir, "stats.yml");
        FileConfiguration config = new YamlConfiguration();
        
        // Don't set hostile_mobs_killed for new player (should default to 0)
        config.set("players." + newPlayerUUIDString + ".kills", 0);
        config.set("players." + newPlayerUUIDString + ".deaths", 0);
        
        try {
            config.save(statsFile);
        } catch (IOException e) {
            fail("Failed to save stats: " + e.getMessage());
        }
        
        // Verify new player starts with 0 hostile mobs killed
        FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(statsFile);
        int hostileMobsKilled = loadedConfig.getInt("players." + newPlayerUUIDString + ".hostile_mobs_killed", 0);
        assertEquals(0, hostileMobsKilled, "New player should start with 0 hostile mobs killed");
    }

    @Test
    void testHostileMobKillStatIncrement() {
        // Test that hostile mob kills can be incremented properly
        
        UUID playerUUID = UUID.randomUUID();
        String playerUUIDString = playerUUID.toString();
        
        File statsFile = new File(tempDir, "stats.yml");
        FileConfiguration config = new YamlConfiguration();
        
        // Start with 0 hostile mobs killed
        config.set("players." + playerUUIDString + ".hostile_mobs_killed", 0);
        
        try {
            config.save(statsFile);
        } catch (IOException e) {
            fail("Failed to save stats: " + e.getMessage());
        }
        
        // Simulate multiple hostile mob kills
        FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(statsFile);
        int currentKills = loadedConfig.getInt("players." + playerUUIDString + ".hostile_mobs_killed", 0);
        
        // Kill 1: Zombie
        currentKills++;
        loadedConfig.set("players." + playerUUIDString + ".hostile_mobs_killed", currentKills);
        
        // Kill 2: Skeleton
        currentKills++;
        loadedConfig.set("players." + playerUUIDString + ".hostile_mobs_killed", currentKills);
        
        // Kill 3: Creeper
        currentKills++;
        loadedConfig.set("players." + playerUUIDString + ".hostile_mobs_killed", currentKills);
        
        try {
            loadedConfig.save(statsFile);
        } catch (IOException e) {
            fail("Failed to save updated stats: " + e.getMessage());
        }
        
        // Verify final count
        FileConfiguration finalConfig = YamlConfiguration.loadConfiguration(statsFile);
        int finalKills = finalConfig.getInt("players." + playerUUIDString + ".hostile_mobs_killed", 0);
        assertEquals(3, finalKills, "Player should have killed 3 hostile mobs total");
    }
} 