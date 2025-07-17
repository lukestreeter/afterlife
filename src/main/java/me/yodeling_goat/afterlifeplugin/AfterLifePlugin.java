package me.yodeling_goat.afterlifeplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

// Karma system
import me.yodeling_goat.afterlifeplugin.karma.KarmaManager;
import me.yodeling_goat.afterlifeplugin.karma.listeners.KarmaKillListener;

// Afterlife system
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeEffectsListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeRestrictionListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.PlayerDeathListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.EntityDeathListener;

// Grave system
import me.yodeling_goat.afterlifeplugin.grave.GraveManager;

public class AfterLifePlugin extends JavaPlugin implements Listener {
    
    @Override
    public void onEnable() {
        getLogger().info("AfterLifePlugin is starting up...");
        
        // Register managers that implement Listener
        KarmaManager karmaManager = new KarmaManager();
        GraveManager graveManager = new GraveManager();
        
        // Register managers as listeners
        Bukkit.getPluginManager().registerEvents(karmaManager, this);
        Bukkit.getPluginManager().registerEvents(graveManager, this);
        
        // Register karma listeners
        Bukkit.getPluginManager().registerEvents(new KarmaKillListener(), this);
        
        // Register afterlife listeners
        Bukkit.getPluginManager().registerEvents(new AfterlifeEffectsListener(), this);
        Bukkit.getPluginManager().registerEvents(new AfterlifeRestrictionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);
        
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
        getLogger().info("AfterLifePlugin has been disabled!");
    }
}
