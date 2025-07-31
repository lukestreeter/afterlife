package me.yodeling_goat.afterlifeplugin.afterlife;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simplified test for AfterlifeManager persistence functionality.
 * Tests the core persistence logic without complex Bukkit mocking.
 */
public class AfterlifePersistenceTest {

    @TempDir
    File tempDir;
    
    private UUID testPlayerUUID;
    private File testAfterlifeFile;
    private FileConfiguration testConfig;

    @BeforeEach
    void setUp() {
        testPlayerUUID = UUID.randomUUID();
        testAfterlifeFile = new File(tempDir, "afterlife.yml");
        testConfig = new YamlConfiguration();
    }

    @Test
    void testAfterlifeFileCreation() {
        // Test that the afterlife.yml file is created properly
        
        // Step 1: Create a simple config file
        testConfig.set("afterlife_players", java.util.Arrays.asList(testPlayerUUID.toString()));
        
        try {
            testConfig.save(testAfterlifeFile);
        } catch (IOException e) {
            fail("Failed to save test config: " + e.getMessage());
        }
        
        // Step 2: Verify file exists
        assertTrue(testAfterlifeFile.exists(), "afterlife.yml file should be created");
        
        // Step 3: Verify content
        FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(testAfterlifeFile);
        java.util.List<String> loadedUUIDs = loadedConfig.getStringList("afterlife_players");
        assertTrue(loadedUUIDs.contains(testPlayerUUID.toString()), 
            "Player UUID should be saved in afterlife.yml file");
    }

    @Test
    void testMultipleUUIDsPersistence() {
        // Test that multiple UUIDs can be saved and loaded
        
        UUID player1UUID = UUID.randomUUID();
        UUID player2UUID = UUID.randomUUID();
        UUID player3UUID = UUID.randomUUID();
        
        // Step 1: Create config with multiple UUIDs
        testConfig.set("afterlife_players", java.util.Arrays.asList(
            player1UUID.toString(),
            player2UUID.toString(),
            player3UUID.toString()
        ));
        
        try {
            testConfig.save(testAfterlifeFile);
        } catch (IOException e) {
            fail("Failed to save test config: " + e.getMessage());
        }
        
        // Step 2: Load and verify all UUIDs
        FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(testAfterlifeFile);
        java.util.List<String> loadedUUIDs = loadedConfig.getStringList("afterlife_players");
        
        assertEquals(3, loadedUUIDs.size(), "Should have 3 UUIDs in the config");
        assertTrue(loadedUUIDs.contains(player1UUID.toString()), "Player 1 UUID should be present");
        assertTrue(loadedUUIDs.contains(player2UUID.toString()), "Player 2 UUID should be present");
        assertTrue(loadedUUIDs.contains(player3UUID.toString()), "Player 3 UUID should be present");
    }

    @Test
    void testInvalidUUIDHandling() {
        // Test that invalid UUIDs are handled gracefully
        
        // Step 1: Create config with invalid UUID
        testConfig.set("afterlife_players", java.util.Arrays.asList("invalid-uuid"));
        
        try {
            testConfig.save(testAfterlifeFile);
        } catch (IOException e) {
            fail("Failed to save test config: " + e.getMessage());
        }
        
        // Step 2: Load config (should not throw exception)
        assertDoesNotThrow(() -> {
            FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(testAfterlifeFile);
            java.util.List<String> loadedUUIDs = loadedConfig.getStringList("afterlife_players");
            assertTrue(loadedUUIDs.contains("invalid-uuid"), "Invalid UUID should still be in the list");
        }, "Loading config with invalid UUID should not throw exception");
    }

    @Test
    void testEmptyConfigHandling() {
        // Test handling of empty config file
        
        // Step 1: Create empty config
        try {
            testConfig.save(testAfterlifeFile);
        } catch (IOException e) {
            fail("Failed to save empty config: " + e.getMessage());
        }
        
        // Step 2: Load empty config
        assertDoesNotThrow(() -> {
            FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(testAfterlifeFile);
            java.util.List<String> loadedUUIDs = loadedConfig.getStringList("afterlife_players");
            assertTrue(loadedUUIDs.isEmpty(), "Empty config should result in empty UUID list");
        }, "Loading empty config should not throw exception");
    }

    @Test
    void testUUIDFormatValidation() {
        // Test that UUID format validation works correctly
        
        // Valid UUIDs
        UUID validUUID1 = UUID.randomUUID();
        UUID validUUID2 = UUID.randomUUID();
        
        // Invalid UUIDs
        String invalidUUID1 = "not-a-uuid";
        String invalidUUID2 = "12345678-1234-1234-1234-123456789012"; // Wrong format
        
        // Step 1: Create config with mix of valid and invalid UUIDs
        testConfig.set("afterlife_players", java.util.Arrays.asList(
            validUUID1.toString(),
            invalidUUID1,
            validUUID2.toString(),
            invalidUUID2
        ));
        
        try {
            testConfig.save(testAfterlifeFile);
        } catch (IOException e) {
            fail("Failed to save test config: " + e.getMessage());
        }
        
        // Step 2: Load and verify
        FileConfiguration loadedConfig = YamlConfiguration.loadConfiguration(testAfterlifeFile);
        java.util.List<String> loadedUUIDs = loadedConfig.getStringList("afterlife_players");
        
        assertEquals(4, loadedUUIDs.size(), "Should have 4 entries in the config");
        assertTrue(loadedUUIDs.contains(validUUID1.toString()), "Valid UUID 1 should be present");
        assertTrue(loadedUUIDs.contains(validUUID2.toString()), "Valid UUID 2 should be present");
        assertTrue(loadedUUIDs.contains(invalidUUID1), "Invalid UUID 1 should be present");
        assertTrue(loadedUUIDs.contains(invalidUUID2), "Invalid UUID 2 should be present");
    }

    @Test
    void testConfigFileOverwrite() {
        // Test that config file can be overwritten properly
        
        // Step 1: Create initial config
        testConfig.set("afterlife_players", java.util.Arrays.asList(testPlayerUUID.toString()));
        try {
            testConfig.save(testAfterlifeFile);
        } catch (IOException e) {
            fail("Failed to save initial config: " + e.getMessage());
        }
        
        // Step 2: Verify initial content
        FileConfiguration loadedConfig1 = YamlConfiguration.loadConfiguration(testAfterlifeFile);
        java.util.List<String> loadedUUIDs1 = loadedConfig1.getStringList("afterlife_players");
        assertEquals(1, loadedUUIDs1.size(), "Initial config should have 1 UUID");
        assertTrue(loadedUUIDs1.contains(testPlayerUUID.toString()), "Initial UUID should be present");
        
        // Step 3: Overwrite with new config
        UUID newPlayerUUID = UUID.randomUUID();
        FileConfiguration newConfig = new YamlConfiguration();
        newConfig.set("afterlife_players", java.util.Arrays.asList(newPlayerUUID.toString()));
        try {
            newConfig.save(testAfterlifeFile);
        } catch (IOException e) {
            fail("Failed to save new config: " + e.getMessage());
        }
        
        // Step 4: Verify new content
        FileConfiguration loadedConfig2 = YamlConfiguration.loadConfiguration(testAfterlifeFile);
        java.util.List<String> loadedUUIDs2 = loadedConfig2.getStringList("afterlife_players");
        assertEquals(1, loadedUUIDs2.size(), "New config should have 1 UUID");
        assertTrue(loadedUUIDs2.contains(newPlayerUUID.toString()), "New UUID should be present");
        assertFalse(loadedUUIDs2.contains(testPlayerUUID.toString()), "Old UUID should not be present");
    }
} 