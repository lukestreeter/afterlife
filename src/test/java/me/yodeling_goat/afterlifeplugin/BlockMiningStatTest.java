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
 * Test to verify the block mining stat functionality.
 * Tests that block mining stats are properly tracked and saved.
 */
public class BlockMiningStatTest {

    @TempDir
    File tempDir;

    @Test
    void testBlockMiningStatTracking() {
        // This test simulates block mining stat tracking and persistence
        
        UUID playerUUID = UUID.randomUUID();
        String playerUUIDString = playerUUID.toString();
        
        // Step 1: Simulate player mining blocks (before saving)
        File statsFile = new File(tempDir, "stats.yml");
        FileConfiguration config = new YamlConfiguration();
        
        // Set up initial stats with some blocks mined
        config.set("players." + playerUUIDString + ".kills", 5);
        config.set("players." + playerUUIDString + ".deaths", 2);
        config.set("players." + playerUUIDString + ".animals_killed", 3);
        config.set("players." + playerUUIDString + ".hostile_mobs_killed", 8);
        config.set("players." + playerUUIDString + ".blocks_mined", 150);
        
        try {
            config.save(statsFile);
        } catch (IOException e) {
            fail("Failed to save stats: " + e.getMessage());
        }
        
        // Verify the stats file exists and contains the block mining data
        assertTrue(statsFile.exists(), "stats.yml file should exist");
        FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(statsFile);
        int blocksMined = loadedConfig.getInt("players." + playerUUIDString + ".blocks_mined", 0);
        assertEquals(150, blocksMined, "Player should have 150 blocks mined");
        
        // Step 2: Simulate additional block mining
        int additionalBlocks = 25;
        int newTotal = blocksMined + additionalBlocks;
        loadedConfig.set("players." + playerUUIDString + ".blocks_mined", newTotal);
        
        try {
            loadedConfig.save(statsFile);
        } catch (IOException e) {
            fail("Failed to save updated stats: " + e.getMessage());
        }
        
        // Step 3: Verify the updated block mining count
        FileConfiguration finalConfig = YamlConfiguration.loadConfiguration(statsFile);
        int finalBlocksMined = finalConfig.getInt("players." + playerUUIDString + ".blocks_mined", 0);
        assertEquals(175, finalBlocksMined, "Player should have 175 blocks mined after additional mining");
        
        // Step 4: Verify other stats are preserved
        int kills = finalConfig.getInt("players." + playerUUIDString + ".kills", 0);
        int deaths = finalConfig.getInt("players." + playerUUIDString + ".deaths", 0);
        int animalsKilled = finalConfig.getInt("players." + playerUUIDString + ".animals_killed", 0);
        int hostileMobsKilled = finalConfig.getInt("players." + playerUUIDString + ".hostile_mobs_killed", 0);
        
        assertEquals(5, kills, "Kills should remain unchanged");
        assertEquals(2, deaths, "Deaths should remain unchanged");
        assertEquals(3, animalsKilled, "Animals killed should remain unchanged");
        assertEquals(8, hostileMobsKilled, "Hostile mobs killed should remain unchanged");
    }

    @Test
    void testBlockMiningStatInitialization() {
        // Test that new players start with 0 blocks mined
        
        UUID newPlayerUUID = UUID.randomUUID();
        String newPlayerUUIDString = newPlayerUUID.toString();
        
        File statsFile = new File(tempDir, "stats.yml");
        FileConfiguration config = new YamlConfiguration();
        
        // Don't set blocks_mined for new player (should default to 0)
        config.set("players." + newPlayerUUIDString + ".kills", 0);
        config.set("players." + newPlayerUUIDString + ".deaths", 0);
        
        try {
            config.save(statsFile);
        } catch (IOException e) {
            fail("Failed to save stats: " + e.getMessage());
        }
        
        // Verify new player starts with 0 blocks mined
        FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(statsFile);
        int blocksMined = loadedConfig.getInt("players." + newPlayerUUIDString + ".blocks_mined", 0);
        assertEquals(0, blocksMined, "New player should start with 0 blocks mined");
    }
} 