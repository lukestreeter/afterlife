package com.yourname.wardenmute;

import org.bukkit.entity.Player;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WardenMutePlugin extends JavaPlugin implements Listener {
    
    private Set<UUID> deadPlayers = new HashSet<>();
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("WardenMute Plugin enabled - Dead players are invisible to Wardens!");
    }
    
    @Override
    public void onDisable() {
        deadPlayers.clear();
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        deadPlayers.add(player.getUniqueId());
        
        // Immediately clear any mob targeting this player
        player.getWorld().getEntitiesByClass(Mob.class).forEach(mob -> {
            if (mob.getTarget() == player) {
                mob.setTarget(null);
            }
        });
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        deadPlayers.remove(player.getUniqueId());
    }
    
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        // Prevent any mob from targeting dead players
        if (event.getEntity() instanceof Mob && event.getTarget() instanceof Player) {
            Player target = (Player) event.getTarget();
            if (deadPlayers.contains(target.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityTargetLiving(EntityTargetLivingEntityEvent event) {
        // Additional protection for targeting events
        if (event.getEntity() instanceof Mob && event.getTarget() instanceof Player) {
            Player target = (Player) event.getTarget();
            if (deadPlayers.contains(target.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
} 