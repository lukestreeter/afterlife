package me.yodeling_goat.afterlifeplugin.impersonation.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import me.yodeling_goat.afterlifeplugin.impersonation.ImpersonationManager;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class ImpersonationListener implements Listener {
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (ImpersonationManager.isPlayerImpersonating(player)) {
            ImpersonationManager.handlePlayerQuit(player);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (ImpersonationManager.isPlayerImpersonating(player)) {
            ImpersonationManager.handlePlayerDeath(player);
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        
        // Only allow impersonation if player is in afterlife and right-clicks an animal
        if (AfterlifeManager.isInAfterlife(player) && entity instanceof org.bukkit.entity.Animals) {
            // Check if this is an impersonation attempt (right-click with empty hand)
            if (player.getInventory().getItemInMainHand().getType() == org.bukkit.Material.AIR) {
                event.setCancelled(true);
                ImpersonationManager.impersonateEntity(player, entity);
            }
        }
    }
} 