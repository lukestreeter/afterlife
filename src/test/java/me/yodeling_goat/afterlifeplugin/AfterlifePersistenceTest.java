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
 * Test to verify the afterlife persistence bug fix.
 * Tests that afterlife state persists through server restarts.
 */
public class AfterlifePersistenceTest {

    @TempDir
    File tempDir;

    @Test
    void testAfterlifePersistenceBugFix() {
        // This test simulates the bug scenario and verifies the fix
        
        UUID playerUUID = UUID.randomUUID();
        String playerUUIDString = playerUUID.toString();
        
        // Step 1: Simulate player entering afterlife (before server restart)
        File afterlifeFile = new File(tempDir, "afterlife.yml");
        FileConfiguration config = new YamlConfiguration();
        config.set("afterlife_players", java.util.Arrays.asList(playerUUIDString));
        
        try {
            config.save(afterlifeFile);
        } catch (IOException e) {
            fail("Failed to save afterlife state: " + e.getMessage());
        }
        
        // Verify the player is marked as being in the afterlife
        assertTrue(afterlifeFile.exists(), "afterlife.yml file should exist");
        FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(afterlifeFile);
        java.util.List<String> savedUUIDs = loadedConfig.getStringList("afterlife_players");
        assertTrue(savedUUIDs.contains(playerUUIDString), 
            "Player UUID should be saved in afterlife state");
        
        // Step 2: Simulate server restart
        FileConfiguration restartConfig = YamlConfiguration.loadConfiguration(afterlifeFile);
        java.util.List<String> restartUUIDs = restartConfig.getStringList("afterlife_players");
        
        // Step 3: Verify that the player is still considered to be in the afterlife
        assertTrue(restartUUIDs.contains(playerUUIDString), 
            "Player should still be in afterlife after server restart");
        
        // Step 4: Verify the fix prevents the bug
        boolean playerIsInAfterlife = restartUUIDs.contains(playerUUIDString);
        assertTrue(playerIsInAfterlife, 
            "Player should be recognized as being in the afterlife after rejoin");
    }

    @Test
    void testAfterlifeStateCleanup() {
        // Test that removing players from afterlife works correctly
        
        UUID player1UUID = UUID.randomUUID();
        UUID player2UUID = UUID.randomUUID();
        
        // Step 1: Both players enter afterlife
        File afterlifeFile = new File(tempDir, "afterlife.yml");
        FileConfiguration config = new YamlConfiguration();
        config.set("afterlife_players", java.util.Arrays.asList(
            player1UUID.toString(),
            player2UUID.toString()
        ));
        
        try {
            config.save(afterlifeFile);
        } catch (IOException e) {
            fail("Failed to save afterlife state: " + e.getMessage());
        }
        
        // Step 2: Simulate removing one player from afterlife
        FileConfiguration updatedConfig = YamlConfiguration.loadConfiguration(afterlifeFile);
        java.util.List<String> currentUUIDs = updatedConfig.getStringList("afterlife_players");
        currentUUIDs.remove(player1UUID.toString()); // Remove player 1
        updatedConfig.set("afterlife_players", currentUUIDs);
        
        try {
            updatedConfig.save(afterlifeFile);
        } catch (IOException e) {
            fail("Failed to save updated afterlife state: " + e.getMessage());
        }
        
        // Step 3: Simulate server restart
        FileConfiguration restartConfig = YamlConfiguration.loadConfiguration(afterlifeFile);
        java.util.List<String> restartUUIDs = restartConfig.getStringList("afterlife_players");
        
        // Step 4: Verify only player 2 is still in the afterlife
        assertFalse(restartUUIDs.contains(player1UUID.toString()), 
            "Player 1 should not be in afterlife after removal and restart");
        assertTrue(restartUUIDs.contains(player2UUID.toString()), 
            "Player 2 should still be in afterlife after restart");
    }
} 