package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Mob;
import org.bukkit.entity.EntityType;

import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class AfterlifeRestrictionListener implements Listener {
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (AfterlifeManager.isInAfterlife(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (AfterlifeManager.isInAfterlife(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (AfterlifeManager.isInAfterlife(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (AfterlifeManager.isInAfterlife(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (AfterlifeManager.isInAfterlife(player)) {
                event.setCancelled(true);
            }
        }
        
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (AfterlifeManager.isInAfterlife(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (AfterlifeManager.isInAfterlife(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (AfterlifeManager.isInAfterlife(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (AfterlifeManager.isInAfterlife(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        // Comprehensive protection against ALL mobs targeting afterlife players
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (AfterlifeManager.isInAfterlife(player)) {
                // Cancel targeting of afterlife players by ANY mob (including Warden, Armadillo, etc.)
                event.setCancelled(true);
                
                // Force mobs to stop targeting afterlife players
                if (event.getEntity() instanceof Mob) {
                    Mob mob = (Mob) event.getEntity();
                    mob.setTarget(null);
                    
                    // Special handling for entities that might have special detection
                    EntityType entityType = mob.getType();
                    if (isEntityTypeWithSpecialDetection(entityType)) {
                        // Force the entity to lose interest in the player
                        forceEntityToLoseInterest(mob, player);
                    }
                }
            }
        }
        
        // Also prevent afterlife players from being targeted as attackers
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (AfterlifeManager.isInAfterlife(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * Additional protection specifically for living entity targeting
     * This provides extra coverage for Warden, Armadillo, and other mobs
     */
    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (AfterlifeManager.isInAfterlife(player)) {
                // Cancel targeting of afterlife players by ANY living entity
                event.setCancelled(true);
                
                // Force the entity to stop targeting afterlife players
                if (event.getEntity() instanceof Mob) {
                    Mob mob = (Mob) event.getEntity();
                    mob.setTarget(null);
                    
                    // Additional protection for entities with special detection
                    EntityType entityType = mob.getType();
                    if (isEntityTypeWithSpecialDetection(entityType)) {
                        forceEntityToLoseInterest(mob, player);
                    }
                }
            }
        }
        
        // Also prevent afterlife players from being targeted as attackers
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (AfterlifeManager.isInAfterlife(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * Check if an entity type has special detection mechanisms
     * that might bypass normal targeting restrictions
     */
        private boolean isEntityTypeWithSpecialDetection(EntityType entityType) {
        // Use proper entity types for 1.21.8 API
        return entityType == EntityType.WARDEN;
    }
    
    /**
     * Force an entity to completely lose interest in a player
     * This provides additional protection for entities with special detection
     */
    private void forceEntityToLoseInterest(Mob mob, Player player) {
        try {
            // Clear the target
            mob.setTarget(null);
            
            // Try to make the entity forget about the player
            // This works for most mob types including newer ones
            
            // For entities that might have persistent memory of players
            // we can try to teleport them slightly away to break line of sight
            if (mob.getLocation().distance(player.getLocation()) < 16) {
                // Only if they're close enough to potentially detect the player
                // Move the entity slightly away to break detection
                mob.teleport(mob.getLocation().add(0, 0.1, 0));
            }
            
        } catch (Exception e) {
            // Ignore any errors - the main targeting cancellation should still work
        }
    }
} 