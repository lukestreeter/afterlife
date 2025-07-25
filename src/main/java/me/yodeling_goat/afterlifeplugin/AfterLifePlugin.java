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

// Grave system
import me.yodeling_goat.afterlifeplugin.grave.listeners.PlayerEnteredAfterlifeListener;

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

        // Register grave listeners
        Bukkit.getPluginManager().registerEvents(new PlayerEnteredAfterlifeListener(), this);
        
        // Register this plugin as a listener for player join events
        Bukkit.getPluginManager().registerEvents(this, this);
        
        getLogger().info("AfterLifePlugin has been enabled!");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        KarmaManager.initializeKarmaDisplay(player);
        AfterlifeManager.initializeAfterlifeState(player);
    }
    
    @Override
    public void onDisable() {
        getLogger().info("AfterLifePlugin is shutting down...");
        
        // Save all stats before shutting down
        StatsManager.getInstance().saveStats();
        
        getLogger().info("AfterLifePlugin has been disabled!");
    }
}
