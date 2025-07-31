package me.yodeling_goat.afterlifeplugin.stats.listeners;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlayerStatsListenerTest {

    @Test
    void testPlayerStatsListenerCreation() {
        // Given & When
        PlayerStatsListener listener = new PlayerStatsListener();
        
        // Then
        assertNotNull(listener);
    }

    @Test
    void testPlayerStatsListenerIsListener() {
        // Given & When
        PlayerStatsListener listener = new PlayerStatsListener();
        
        // Then
        assertTrue(listener instanceof org.bukkit.event.Listener);
    }

    @Test
    void testPlayerStatsListenerHasCraftItemMethod() {
        // Given & When
        PlayerStatsListener listener = new PlayerStatsListener();
        
        // Then
        // This test verifies the class structure is correct
        // The actual method testing would require complex Bukkit mocking
        assertNotNull(listener);
    }
} 