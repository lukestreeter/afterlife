package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class AfterlifeMaintenanceListener implements Listener {
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (AfterlifeManager.isInAfterlife(player)) {
            // Keep hunger at max
            if (player.getFoodLevel() < 20) {
                player.setFoodLevel(20);
                player.setSaturation(20.0f);
            }
        }
    }
    
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        
        if (AfterlifeManager.isInAfterlife(player)) {
            // Allow normal flight toggle (so they can drop and fly naturally)
            // Don't interfere with their flight state
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Check if both entities are players
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        
        // Prevent PvP between afterlife players
        if (AfterlifeManager.isInAfterlife(attacker) && AfterlifeManager.isInAfterlife(victim)) {
            event.setCancelled(true);
            attacker.sendMessage("§cYou cannot attack other players in the afterlife!");
            return;
        }
        
        // Prevent afterlife players from attacking living players
        if (AfterlifeManager.isInAfterlife(attacker) && !AfterlifeManager.isInAfterlife(victim)) {
            event.setCancelled(true);
            attacker.sendMessage("§cYou cannot attack living players from the afterlife!");
            return;
        }
        
        // Prevent living players from attacking afterlife players
        if (!AfterlifeManager.isInAfterlife(attacker) && AfterlifeManager.isInAfterlife(victim)) {
            event.setCancelled(true);
            attacker.sendMessage("§cYou cannot attack players in the afterlife!");
            return;
        }
    }
    
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