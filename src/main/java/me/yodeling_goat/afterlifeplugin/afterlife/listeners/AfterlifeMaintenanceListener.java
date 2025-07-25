package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class AfterlifeMaintenanceListener implements Listener {
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // If player was in afterlife, clean up their effects but keep them in the afterlife state
        // This allows them to return to afterlife when they rejoin
        if (AfterlifeManager.isInAfterlife(player)) {
            AfterlifeManager.removeAfterlifeEffects(player);
        }
    }
} 