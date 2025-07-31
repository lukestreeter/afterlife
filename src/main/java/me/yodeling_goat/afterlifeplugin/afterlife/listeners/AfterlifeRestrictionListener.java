package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Mob;
import me.yodeling_goat.afterlifeplugin.afterlife.events.PlayerEnterAfterlifeEvent;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class AfterlifeRestrictionListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (AfterlifeManager.isInAfterlife(event.getPlayer())) {
            // Allow admins to break blocks even in afterlife
            if (!event.getPlayer().hasPermission("afterlife.admin")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (AfterlifeManager.isInAfterlife(event.getPlayer())) {
            // Allow admins to place blocks even in afterlife
            if (!event.getPlayer().hasPermission("afterlife.admin")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (AfterlifeManager.isInAfterlife(event.getPlayer())) {
            // Allow admins to interact even in afterlife
            if (!event.getPlayer().hasPermission("afterlife.admin")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (AfterlifeManager.isInAfterlife(event.getPlayer())) {
            // Allow admins to pickup items even in afterlife
            if (!event.getPlayer().hasPermission("afterlife.admin")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (AfterlifeManager.isInAfterlife(player)) {
                // Allow admins to take damage even in afterlife
                if (!player.hasPermission("afterlife.admin")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (AfterlifeManager.isInAfterlife(player)) {
                // Allow admins to deal damage even in afterlife
                if (!player.hasPermission("afterlife.admin")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (AfterlifeManager.isInAfterlife(player)) {
                // Allow admins to combust even in afterlife
                if (!player.hasPermission("afterlife.admin")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (AfterlifeManager.isInAfterlife(player)) {
                // Allow admins to be targeted even in afterlife
                if (!player.hasPermission("afterlife.admin")) {
                    event.setCancelled(true);
                }
            }
        }
    }
} 