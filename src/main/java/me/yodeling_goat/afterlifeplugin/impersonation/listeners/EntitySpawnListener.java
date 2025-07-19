package me.yodeling_goat.afterlifeplugin.impersonation.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class EntitySpawnListener implements Listener {
    
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        
        // Check if this is an animal disguise entity by looking for the tag
        if (entity.getScoreboardTags().contains("animal_disguise")) {
            Bukkit.getLogger().info("[EntitySpawnListener] Found animal disguise entity: " + entity.getType());
            
            // Find the player name from the tags
            String playerName = null;
            for (String tag : entity.getScoreboardTags()) {
                if (!tag.equals("animal_disguise")) {
                    playerName = tag;
                    break;
                }
            }
            
            if (playerName != null) {
                // Add metadata to the entity to link it to the player
                entity.setMetadata("animal_disguise_player", 
                    new FixedMetadataValue(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), playerName));
                Bukkit.getLogger().info("[EntitySpawnListener] Linked entity to player: " + playerName);
                
                // Set up the animal to follow the player using Minecraft's AI
                setupAnimalFollowing(entity, playerName);
            }
        }
    }
    
    private void setupAnimalFollowing(Entity animal, String playerName) {
        // Schedule a task to set up the following behavior after the entity is fully spawned
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(playerName);
                if (player != null && player.isOnline() && !animal.isDead()) {
                    // Use Minecraft's built-in AI to make the animal follow the player
                    if (animal instanceof org.bukkit.entity.LivingEntity) {
                        org.bukkit.entity.LivingEntity livingAnimal = (org.bukkit.entity.LivingEntity) animal;
                        
                        // For tamed animals, set the owner to make them follow
                        if (animal instanceof org.bukkit.entity.Wolf) {
                            org.bukkit.entity.Wolf wolf = (org.bukkit.entity.Wolf) animal;
                            wolf.setTamed(true);
                            wolf.setOwner(player);
                        } else if (animal instanceof org.bukkit.entity.Cat) {
                            org.bukkit.entity.Cat cat = (org.bukkit.entity.Cat) animal;
                            cat.setTamed(true);
                            cat.setOwner(player);
                        } else if (animal instanceof org.bukkit.entity.Horse) {
                            org.bukkit.entity.Horse horse = (org.bukkit.entity.Horse) animal;
                            horse.setTamed(true);
                            horse.setOwner(player);
                        }
                        
                        Bukkit.getLogger().info("[EntitySpawnListener] Set up following AI for " + animal.getType() + " to follow " + playerName);
                    }
                }
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), 5L); // Run after 5 ticks
    }
} 