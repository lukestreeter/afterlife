package me.yodeling_goat.afterlifeplugin.grave.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.yodeling_goat.afterlifeplugin.afterlife.events.PlayerEnterAfterlifeEvent;
import me.yodeling_goat.afterlifeplugin.grave.GravestoneManager;
import me.yodeling_goat.afterlifeplugin.AfterLifePlugin;
import org.bukkit.Bukkit;


public class PlayerEnteredAfterlifeListener implements Listener {
    @EventHandler
    public void onAfterLife(PlayerEnterAfterlifeEvent event) {
        // Check if grave system is enabled in config
        if (!AfterLifePlugin.getInstance().getConfig().getBoolean("grave.enabled", true)) {
            return;
        }
        
        Player player = event.getPlayer();
        if (player.getLastDamageCause() != null) {
            String deathCause = player.getLastDamageCause().getCause().toString();
            
            // Delay grave creation by 5 minutes (6000 ticks = 5 minutes)
            Bukkit.getScheduler().runTaskLater(AfterLifePlugin.getInstance(), () -> {
                GravestoneManager.createGravestone(player, deathCause);
            }, 6000L);
            
            // Notify player about the delay
            player.sendMessage("§7[AfterLife] §fYour grave will appear in 5 minutes to prevent immediate looting.");
        }
    }
}
