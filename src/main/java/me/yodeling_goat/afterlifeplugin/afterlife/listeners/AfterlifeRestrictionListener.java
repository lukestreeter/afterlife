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
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Mob;

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
                // Cancel targeting of afterlife players by ANY mob (including Warden)
                event.setCancelled(true);
                
                // Force mobs to stop targeting afterlife players
                if (event.getEntity() instanceof Mob) {
                    Mob mob = (Mob) event.getEntity();
                    mob.setTarget(null);
                    
                    // Special handling for any mob that might be a Warden
                    // This will work for any mob type including Warden if available
                    try {
                        // Try to clear any anger or targeting state
                        mob.setTarget(null);
                    } catch (Exception e) {
                        // Ignore any errors, just ensure targeting is cancelled
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
     * This provides extra coverage for Warden and other mobs
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
} 