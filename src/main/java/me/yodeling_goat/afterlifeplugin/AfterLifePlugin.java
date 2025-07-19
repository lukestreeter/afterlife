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
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.PlayerDeathListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.EntityDeathListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeTimeListener;

// Grave system
import me.yodeling_goat.afterlifeplugin.grave.GraveManager;

// Impersonation system
import me.yodeling_goat.afterlifeplugin.impersonation.ImpersonationManager;
import me.yodeling_goat.afterlifeplugin.impersonation.listeners.ImpersonationListener;
import me.yodeling_goat.afterlifeplugin.impersonation.listeners.EntitySpawnListener;
import me.yodeling_goat.afterlifeplugin.impersonation.commands.ImpersonationCommand;
import me.yodeling_goat.afterlifeplugin.impersonation.tasks.ImpersonationUpdateTask;

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
        
        // Register afterlife listeners
        Bukkit.getPluginManager().registerEvents(new AfterlifeEffectsListener(), this);
        Bukkit.getPluginManager().registerEvents(new AfterlifeRestrictionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new AfterlifeTimeListener(), this);
        
        // Register impersonation listeners
        Bukkit.getPluginManager().registerEvents(new ImpersonationListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntitySpawnListener(), this);
        
        // Start the periodic time update task
        getLogger().info("Starting AfterlifeTimeListener periodic updates...");
        AfterlifeTimeListener.startPeriodicUpdate(this);
        
        // Start the periodic impersonation update task
        getLogger().info("Starting ImpersonationUpdateTask periodic updates...");
        ImpersonationUpdateTask.startPeriodicUpdate(this);
        

        
        // Register this plugin as a listener for player join events
        Bukkit.getPluginManager().registerEvents(this, this);
        
        // Register impersonation commands
        getCommand("impersonate").setExecutor(new ImpersonationCommand());
        getCommand("unimpersonate").setExecutor(new ImpersonationCommand());
        getCommand("debugimpersonate").setExecutor(new ImpersonationCommand());
        
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
