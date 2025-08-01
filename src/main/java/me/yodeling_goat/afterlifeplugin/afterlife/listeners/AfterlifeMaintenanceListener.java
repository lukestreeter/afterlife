package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class AfterlifeMaintenanceListener implements Listener {
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (AfterlifeManager.isInAfterlife(player)) {
            // Keep hunger at max and health at max to minimize health bar visibility
            if (player.getFoodLevel() < 20) {
                player.setFoodLevel(20);
                player.setSaturation(20.0f);
                player.setExhaustion(0.0f);
            }
            
            // Ensure health is always at maximum
            if (player.getHealth() < player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
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
    public void onEntityDamage(EntityDamageEvent event) {
        // Prevent any damage to afterlife players to keep health bar hidden
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (AfterlifeManager.isInAfterlife(player)) {
                event.setCancelled(true);
                // Ensure health stays at maximum
                player.setHealth(player.getMaxHealth());
            }
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