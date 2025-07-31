package me.yodeling_goat.afterlifeplugin.stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StatsManagerTest {

    @Test
    void testPlayerStatsConstructorWithItemsCrafted() {
        // Given
        int kills = 5;
        int deaths = 2;
        int animalsKilled = 10;
        int hostileMobsKilled = 3;
        int itemsCrafted = 15;
        int xpCollected = 1000;
        int blocksMined = 50;
        
        // When
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(kills, deaths, animalsKilled, hostileMobsKilled, itemsCrafted, xpCollected, blocksMined);
        
        // Then
        assertEquals(kills, stats.getKills());
        assertEquals(deaths, stats.getDeaths());
        assertEquals(animalsKilled, stats.getAnimalsKilled());
        assertEquals(itemsCrafted, stats.getItemsCrafted());
        assertEquals(xpCollected, stats.getXpCollected());
        assertEquals(blocksMined, stats.getBlocksMined());
    }

    @Test
    void testPlayerStatsConstructorWithoutItemsCrafted() {
        // Given
        int kills = 3;
        int deaths = 1;
        int animalsKilled = 7;
        int hostileMobsKilled = 2;
        int itemsCrafted = 0;
        int xpCollected = 0;
        int blocksMined = 0;
        
        // When
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(kills, deaths, animalsKilled, hostileMobsKilled, itemsCrafted, xpCollected, blocksMined);
        
        // Then
        assertEquals(kills, stats.getKills());
        assertEquals(deaths, stats.getDeaths());
        assertEquals(animalsKilled, stats.getAnimalsKilled());
        assertEquals(0, stats.getItemsCrafted()); // Should default to 0
    }

    @Test
    void testAddItemCraftedMethod() {
        // Given
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(0, 0, 0, 0, 0, 0, 0);
        
        // When
        stats.addItemCrafted();
        stats.addItemCrafted();
        
        // Then
        assertEquals(2, stats.getItemsCrafted());
    }

    @Test
    void testAddItemCraftedMultipleTimes() {
        // Given
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(0, 0, 0, 0, 0, 0, 0);
        
        // When
        for (int i = 0; i < 10; i++) {
            stats.addItemCrafted();
        }
        
        // Then
        assertEquals(10, stats.getItemsCrafted());
    }

    @Test
    void testItemsCraftedWithOtherStats() {
        // Given
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(5, 2, 8, 3, 12, 500, 25);
        
        // When
        stats.addKill();
        stats.addDeath();
        stats.addAnimalKill();
        stats.addItemCrafted();
        
        // Then
        assertEquals(6, stats.getKills());
        assertEquals(3, stats.getDeaths());
        assertEquals(9, stats.getAnimalsKilled());
        assertEquals(13, stats.getItemsCrafted());
    }

    @Test
    void testItemsCraftedDefaultValue() {
        // Given
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(0, 0, 0, 0, 0, 0, 0);
        
        // Then
        assertEquals(0, stats.getItemsCrafted());
    }

    @Test
    void testKDRatioWithItemsCrafted() {
        // Given
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(10, 5, 3, 2, 15, 750, 30);
        
        // Then
        assertEquals(2.0, stats.getKDRatio());
        assertEquals(15, stats.getItemsCrafted());
    }

    @Test
    void testXpCollectedConstructor() {
        // Given
        int kills = 5;
        int deaths = 2;
        int animalsKilled = 10;
        int hostileMobsKilled = 4;
        int itemsCrafted = 15;
        int xpCollected = 2500;
        int blocksMined = 100;
        
        // When
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(kills, deaths, animalsKilled, hostileMobsKilled, itemsCrafted, xpCollected, blocksMined);
        
        // Then
        assertEquals(kills, stats.getKills());
        assertEquals(deaths, stats.getDeaths());
        assertEquals(animalsKilled, stats.getAnimalsKilled());
        assertEquals(itemsCrafted, stats.getItemsCrafted());
        assertEquals(xpCollected, stats.getXpCollected());
        assertEquals(blocksMined, stats.getBlocksMined());
    }

    @Test
    void testAddXpCollected() {
        // Given
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(0, 0, 0, 0, 0, 0, 0);
        
        // When
        stats.addXpCollected(100);
        stats.addXpCollected(50);
        stats.addXpCollected(25);
        
        // Then
        assertEquals(175, stats.getXpCollected());
    }

    @Test
    void testXpCollectedDefaultValue() {
        // Given
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(0, 0, 0, 0, 0, 0, 0);
        
        // Then
        assertEquals(0, stats.getXpCollected());
    }

    @Test
    void testXpCollectedWithOtherStats() {
        // Given
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(5, 2, 8, 3, 12, 1000, 75);
        
        // When
        stats.addKill();
        stats.addDeath();
        stats.addAnimalKill();
        stats.addItemCrafted();
        stats.addXpCollected(500);
        
        // Then
        assertEquals(6, stats.getKills());
        assertEquals(3, stats.getDeaths());
        assertEquals(9, stats.getAnimalsKilled());
        assertEquals(13, stats.getItemsCrafted());
        assertEquals(1500, stats.getXpCollected());
    }
} 