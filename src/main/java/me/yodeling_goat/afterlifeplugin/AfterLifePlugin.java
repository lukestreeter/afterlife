package me.yodeling_goat.afterlifeplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

// Karma system
import me.yodeling_goat.afterlifeplugin.karma.KarmaManager;

// Afterlife system
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeEffectsListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeRestrictionListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeMaintenanceListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.PlayerDeathListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.EntityDeathListener;

// Stats system
import me.yodeling_goat.afterlifeplugin.stats.StatsManager;
import me.yodeling_goat.afterlifeplugin.stats.listeners.PlayerStatsListener;
import me.yodeling_goat.afterlifeplugin.stats.listeners.BossKillListener;

// Grave system
import me.yodeling_goat.afterlifeplugin.grave.listeners.PlayerEnteredAfterlifeListener;

// Leaderboard system
import me.yodeling_goat.afterlifeplugin.leaderboard.listeners.LeaderboardListener;
import me.yodeling_goat.afterlifeplugin.leaderboard.CompassManager;

public class AfterLifePlugin extends JavaPlugin implements Listener {
    
    private static AfterLifePlugin instance;
    
    public static AfterLifePlugin getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        instance = this;
        // Save default config if it doesn't exist
        saveDefaultConfig();
        getLogger().info("AfterLifePlugin is starting up...");
        
        // Initialize afterlife manager
        AfterlifeManager.initialize();
        
        // Register managers that implement Listener
        KarmaManager karmaManager = new KarmaManager();
        
        // Register managers as listeners
        Bukkit.getPluginManager().registerEvents(karmaManager, this);
        
        // Register afterlife listeners
        Bukkit.getPluginManager().registerEvents(new AfterlifeEffectsListener(), this);
        Bukkit.getPluginManager().registerEvents(new AfterlifeRestrictionListener(), this);
        Bukkit.getPluginManager().registerEvents(new AfterlifeMaintenanceListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);

        // Register stats listeners
        Bukkit.getPluginManager().registerEvents(new PlayerStatsListener(), this);
        Bukkit.getPluginManager().registerEvents(new BossKillListener(), this);

        // Register grave listeners
        Bukkit.getPluginManager().registerEvents(new PlayerEnteredAfterlifeListener(), this);

        // Register leaderboard listeners
        Bukkit.getPluginManager().registerEvents(new LeaderboardListener(), this);
        
        // Register this plugin as a listener for player join events
        Bukkit.getPluginManager().registerEvents(this, this);
        
        // Schedule periodic cleanup of offline afterlife players (every 5 minutes)
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            AfterlifeManager.cleanupOfflinePlayers();
        }, 6000L, 6000L); // 6000 ticks = 5 minutes
        
        getLogger().info("AfterLifePlugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("AfterLifePlugin is shutting down...");
        
        // Save afterlife state before shutting down
        AfterlifeManager.saveAfterlifeState();
        
        // Save all stats before shutting down
        StatsManager.getInstance().saveStats();
        
        getLogger().info("AfterLifePlugin has been disabled!");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        KarmaManager.initializeKarmaDisplay(player);
        AfterlifeManager.initializeAfterlifeState(player);
        
        // Give leaderboard compass to new players
        CompassManager.giveLeaderboardCompass(player);
    }
}
