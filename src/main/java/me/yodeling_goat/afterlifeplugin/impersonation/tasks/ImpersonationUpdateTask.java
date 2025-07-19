package me.yodeling_goat.afterlifeplugin.impersonation.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import me.yodeling_goat.afterlifeplugin.impersonation.ImpersonationManager;

public class ImpersonationUpdateTask extends BukkitRunnable {
    
    @Override
    public void run() {
        // Use Minecraft's built-in AI for natural animal movement
        // Update all impersonation entities every 20 ticks (1 time per second) for efficiency
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (ImpersonationManager.isPlayerImpersonating(player)) {
                boolean foundEntity = false;
                // Find the animal entity associated with this player
                for (Entity entity : player.getWorld().getEntities()) {
                    if (entity.hasMetadata("animal_disguise_player") && 
                        entity.getMetadata("animal_disguise_player").get(0).asString().equals(player.getName())) {
                        if (!entity.isDead()) {
                            // Use Minecraft's native AI - much more efficient than teleportation
                            if (entity instanceof org.bukkit.entity.LivingEntity) {
                                org.bukkit.entity.LivingEntity livingEntity = (org.bukkit.entity.LivingEntity) entity;
                                
                                // For animals that can be tamed, ensure they follow the player
                                if (entity instanceof org.bukkit.entity.Wolf) {
                                    org.bukkit.entity.Wolf wolf = (org.bukkit.entity.Wolf) entity;
                                    if (!wolf.isTamed()) {
                                        wolf.setTamed(true);
                                        wolf.setOwner(player);
                                    }
                                } else if (entity instanceof org.bukkit.entity.Cat) {
                                    org.bukkit.entity.Cat cat = (org.bukkit.entity.Cat) entity;
                                    if (!cat.isTamed()) {
                                        cat.setTamed(true);
                                        cat.setOwner(player);
                                    }
                                } else if (entity instanceof org.bukkit.entity.Horse) {
                                    org.bukkit.entity.Horse horse = (org.bukkit.entity.Horse) entity;
                                    if (!horse.isTamed()) {
                                        horse.setTamed(true);
                                        horse.setOwner(player);
                                    }
                                }
                            }
                            foundEntity = true;
                        }
                        break;
                    }
                }
                
                if (!foundEntity) {
                    Bukkit.getLogger().warning("[ImpersonationUpdateTask] Could not find animal entity for player: " + player.getName());
                }
            }
        }
    }
    
    public static void startPeriodicUpdate(org.bukkit.plugin.Plugin plugin) {
        new ImpersonationUpdateTask().runTaskTimer(plugin, 20L, 20L); // Run every 20 ticks (1 second) for efficiency
        plugin.getLogger().info("[ImpersonationUpdateTask] Started periodic animal AI updates");
    }
} 