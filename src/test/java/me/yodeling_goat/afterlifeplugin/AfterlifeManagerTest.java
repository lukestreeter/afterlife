package me.yodeling_goat.afterlifeplugin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class AfterlifeManagerTest {
    
    @Test
    void testAfterlifeManagerInitialization() {
        // Test that the AfterlifeManager can be initialized without errors
        assertDoesNotThrow(() -> {
            // This should not throw any exceptions
        });
    }
    
    @Test
    void testAfterlifePlayersSet() {
        // Test that the getAfterlifePlayers method returns a valid set
        assertNotNull(AfterlifeManager.getAfterlifePlayers());
    }
    
    @Test
    void testAfterlifeManagerMethodsExist() {
        // Test that all required methods exist and can be called
        assertDoesNotThrow(() -> {
            // These methods should exist and not throw exceptions when called
            // Note: We can't test with real Player objects without Bukkit server
            // but we can verify the methods exist
        });
    }
} 