package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.afterlife.CountdownManager;
import me.yodeling_goat.afterlifeplugin.afterlife.handlers.InventoryHandler;
import me.yodeling_goat.afterlifeplugin.AfterLifePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Save the player's inventory before sending to afterlife
        Player player = event.getEntity();
        InventoryHandler.getInstance().saveInventory(player);
        event.getDrops().clear(); // Prevent drops
        
        if (!AfterlifeManager.isInAfterlife(player)) {
            // Prevent the death screen by respawning immediately
            org.bukkit.Bukkit.getScheduler().runTaskLater(
                me.yodeling_goat.afterlifeplugin.AfterLifePlugin.getInstance(),
                () -> {
                    if (player.isOnline()) {
                        // Respawn the player to prevent death screen
                        player.spigot().respawn();
                        
                        // Teleport to sky and apply afterlife effects
                        org.bukkit.Location skyLocation = new org.bukkit.Location(
                            player.getWorld(), 
                            player.getLocation().getX(), 
                            200, 
                            player.getLocation().getZ()
                        );
                        player.teleport(skyLocation);
                        
                        // Send to afterlife
                        AfterlifeManager.sendToAfterlife(player);
                        
                        // Start countdown immediately
                        CountdownManager.startDeathCountdown(player);
                    }
                },
                1L // Minimal delay to ensure death event is processed
            );
        }
    }
} 