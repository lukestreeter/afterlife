package me.yodeling_goat.afterlifeplugin;

import me.yodeling_goat.afterlifeplugin.stats.StatsManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BossKillStatTest {
    
    @Test
    public void testBossKillStatInitialization() {
        // Test that new PlayerStats start with 0 boss kills
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(0, 0);
        
        assertEquals(0, stats.getWardenKilled());
        assertEquals(0, stats.getEnderDragonKilled());
        assertEquals(0, stats.getWitherKilled());
    }
    
    @Test
    public void testWardenKillStatIncrement() {
        // Test warden kill increment
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(0, 0);
        
        stats.addWardenKill();
        assertEquals(1, stats.getWardenKilled());
        assertEquals(0, stats.getEnderDragonKilled());
        assertEquals(0, stats.getWitherKilled());
        
        // Test multiple kills
        stats.addWardenKill();
        stats.addWardenKill();
        
        assertEquals(3, stats.getWardenKilled());
    }
    
    @Test
    public void testEnderDragonKillStatIncrement() {
        // Test ender dragon kill increment
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(0, 0);
        
        stats.addEnderDragonKill();
        assertEquals(0, stats.getWardenKilled());
        assertEquals(1, stats.getEnderDragonKilled());
        assertEquals(0, stats.getWitherKilled());
        
        // Test multiple kills
        stats.addEnderDragonKill();
        
        assertEquals(2, stats.getEnderDragonKilled());
    }
    
    @Test
    public void testWitherKillStatIncrement() {
        // Test wither kill increment
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(0, 0);
        
        stats.addWitherKill();
        assertEquals(0, stats.getWardenKilled());
        assertEquals(0, stats.getEnderDragonKilled());
        assertEquals(1, stats.getWitherKilled());
        
        // Test multiple kills
        stats.addWitherKill();
        stats.addWitherKill();
        stats.addWitherKill();
        
        assertEquals(4, stats.getWitherKilled());
    }
    
    @Test
    public void testAllBossKillsIndependent() {
        // Test that all boss kill stats are independent
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats(0, 0);
        
        stats.addWardenKill();
        stats.addEnderDragonKill();
        stats.addWitherKill();
        stats.addWardenKill();
        stats.addEnderDragonKill();
        
        assertEquals(2, stats.getWardenKilled());
        assertEquals(2, stats.getEnderDragonKilled());
        assertEquals(1, stats.getWitherKilled());
    }
} 