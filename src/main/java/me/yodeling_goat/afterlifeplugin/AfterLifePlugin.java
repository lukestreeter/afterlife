package me.yodeling_goat.afterlifeplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.ChatColor;

// Karma system
import me.yodeling_goat.afterlifeplugin.karma.KarmaManager;
// Afterlife system
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeEffectsListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeRestrictionListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.PlayerDeathListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.EntityDeathListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.MobSpectateListener;
import me.yodeling_goat.afterlifeplugin.afterlife.util.MobMorphManager;
// Grave system
import me.yodeling_goat.afterlifeplugin.grave.GraveManager;

public class AfterLifePlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("AfterLifePlugin is starting up...");
        KarmaManager karmaManager = new KarmaManager();
        GraveManager graveManager = new GraveManager();
        MobMorphManager morphManager = new MobMorphManager(this);
        Bukkit.getPluginManager().registerEvents(karmaManager, this);
        Bukkit.getPluginManager().registerEvents(graveManager, this);
        Bukkit.getPluginManager().registerEvents(new AfterlifeEffectsListener(), this);
        Bukkit.getPluginManager().registerEvents(new AfterlifeRestrictionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new MobSpectateListener(), this);
        Bukkit.getPluginManager().registerEvents(morphManager, this);
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
