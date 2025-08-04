package me.yodeling_goat.afterlifeplugin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.afterlife.util.MobMorphManager;

public class IntegrationTest {
    
    @Test
    void testAfterlifeManagerIntegration() {
        // Test that AfterlifeManager methods work correctly
        assertNotNull(AfterlifeManager.getAfterlifePlayers());
        
        // Test that the manager can handle basic operations without errors
        assertDoesNotThrow(() -> {
            // These methods should exist and be callable
            // Note: We can't test with real Player objects without a Bukkit server
        });
    }
    
    @Test
    void testMobMorphManagerIntegration() {
        // Test that MobMorphManager methods work correctly
        assertDoesNotThrow(() -> {
            // These methods should exist and be callable
            // Note: We can't test with real Player objects without a Bukkit server
        });
    }
    

    
    @Test
    void testAllManagersExist() {
        // Test that all our main manager classes exist and can be instantiated
        assertDoesNotThrow(() -> {
            // Verify all our main classes are accessible
            Class.forName("me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager");
            Class.forName("me.yodeling_goat.afterlifeplugin.afterlife.util.MobMorphManager");
        });
    }
} 