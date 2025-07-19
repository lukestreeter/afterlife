package me.yodeling_goat.afterlifeplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

// Karma system
import me.yodeling_goat.afterlifeplugin.karma.KarmaManager;
import me.yodeling_goat.afterlifeplugin.karma.KarmaGoalManager;
import me.yodeling_goat.afterlifeplugin.karma.commands.KarmaCommand;
import me.yodeling_goat.afterlifeplugin.karma.listeners.KarmaActionListener;

// Afterlife system
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeEffectsListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeRestrictionListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.PlayerDeathListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.EntityDeathListener;

// Grave system
import me.yodeling_goat.afterlifeplugin.grave.listeners.PlayerEnteredAfterlifeListener;

public class AfterLifePlugin extends JavaPlugin implements Listener {
    
    @Override
    public void onEnable() {
        getLogger().info("AfterLifePlugin is starting up...");
        
        // Save default config
        saveDefaultConfig();
        
        // Register managers that implement Listener
        KarmaManager karmaManager = new KarmaManager();
        KarmaGoalManager karmaGoalManager = new KarmaGoalManager(this);
        
        // Register managers as listeners
        Bukkit.getPluginManager().registerEvents(karmaManager, this);
        Bukkit.getPluginManager().registerEvents(karmaGoalManager, this);
        
        // Register karma listeners
        Bukkit.getPluginManager().registerEvents(new KarmaActionListener(), this);
        
        // Register commands
        getCommand("karma").setExecutor(new KarmaCommand());
        
        // Register afterlife listeners
        Bukkit.getPluginManager().registerEvents(new AfterlifeEffectsListener(), this);
        Bukkit.getPluginManager().registerEvents(new AfterlifeRestrictionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);

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
    
    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        Player player = event.getPlayer();
        KarmaManager.removeKarmaDisplay(player);
    }
    
    @Override
    public void onDisable() {
        getLogger().info("AfterLifePlugin is shutting down...");
        getLogger().info("AfterLifePlugin has been disabled!");
    }
}
