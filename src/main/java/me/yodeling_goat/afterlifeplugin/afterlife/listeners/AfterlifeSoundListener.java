package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.entity.Player;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

/**
 * Suppresses loud actions from afterlife players to prevent Warden detection
 * while allowing normal movement and basic interactions
 */
public class AfterlifeSoundListener implements Listener {
    
    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (AfterlifeManager.isInAfterlife(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
        }
    }
    
    // Cancel ALL loud actions that would definitely attract Warden
    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
        }
    }
} 