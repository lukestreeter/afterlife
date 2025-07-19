package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.AfterLifePlugin;

public class AfterlifeTimeListener implements Listener {
    
    @EventHandler
    public void onTimeSkip(TimeSkipEvent event) {
        // Update all afterlife players when time changes significantly
        Bukkit.getLogger().info("[AfterlifeTimeListener] Time skip detected! Updating afterlife effects...");
        AfterlifeManager.updateAllAfterlifePlayersDayNightEffect();
    }
    
    // Method to start the periodic update task
    public static void startPeriodicUpdate(AfterLifePlugin plugin) {
        Bukkit.getLogger().info("[AfterlifeTimeListener] Starting periodic update task (every 30 seconds)");
        new BukkitRunnable() {
            @Override
            public void run() {
                // Check every 30 seconds (600 ticks) for time changes
                Bukkit.getLogger().info("[AfterlifeTimeListener] Running periodic update check...");
                AfterlifeManager.updateAllAfterlifePlayersDayNightEffect();
            }
        }.runTaskTimer(plugin, 600L, 600L); // Start after 30 seconds, repeat every 30 seconds
    }
} 