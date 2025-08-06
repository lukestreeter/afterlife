package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import me.yodeling_goat.afterlifeplugin.afterlife.events.PlayerEnterAfterlifeEvent;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.Bukkit;

public class AfterlifeEffectsListener implements Listener {
    @EventHandler
    public void onPlayerEnterAfterlife(PlayerEnterAfterlifeEvent event) {
        Player player = event.getPlayer();
        AfterlifeManager.applyAllAfterlifeEffects(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), () -> {
                AfterlifeManager.applyPermanentAfterlifeEffects(player);
            }, 5L);
        }
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), () -> {
                AfterlifeManager.applyPermanentAfterlifeEffects(player);
            }, 3L);
        }
    }
} 