package me.yodeling_goat.afterlifeplugin.impersonation.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import me.yodeling_goat.afterlifeplugin.impersonation.ImpersonationManager;

public class ImpersonationUpdateTask extends BukkitRunnable {
    
    @Override
    public void run() {
        // Update all impersonation locations every 5 ticks (4 times per second)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (ImpersonationManager.isPlayerImpersonating(player)) {
                // Find the animal entity associated with this player
                for (Entity entity : player.getWorld().getEntities()) {
                    if (entity.hasMetadata("animal_disguise_player") && 
                        entity.getMetadata("animal_disguise_player").get(0).asString().equals(player.getName())) {
                        if (!entity.isDead()) {
                            // Teleport the animal to the player's location
                            entity.teleport(player.getLocation());
                        }
                        break;
                    }
                }
            }
        }
    }
    
    public static void startPeriodicUpdate(org.bukkit.plugin.Plugin plugin) {
        new ImpersonationUpdateTask().runTaskTimer(plugin, 5L, 5L);
        plugin.getLogger().info("[ImpersonationUpdateTask] Started periodic animal entity updates");
    }
} 