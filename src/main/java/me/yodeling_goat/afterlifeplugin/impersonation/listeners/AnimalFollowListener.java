package me.yodeling_goat.afterlifeplugin.impersonation.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Animals;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import me.yodeling_goat.afterlifeplugin.impersonation.ImpersonationManager;

public class AnimalFollowListener implements Listener {
    
    // Store last known player locations for animals to stick around
    private static final java.util.Map<String, org.bukkit.Location> lastPlayerLocations = new java.util.HashMap<>();
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is impersonating and has the animal lure
        if (ImpersonationManager.isPlayerImpersonating(player)) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (mainHand.hasItemMeta() && mainHand.getItemMeta().hasDisplayName() && 
                mainHand.getItemMeta().getDisplayName().equals("§6Animal Lure")) {
                
                // Get the player's unique identifier from the food item
                String playerId = getPlayerIdFromFood(mainHand);
                if (playerId != null && playerId.equals(player.getName())) {
                    
                    // Update last known location
                    lastPlayerLocations.put(playerId, player.getLocation());
                    
                    // Find the specific animal that belongs to this player
                    Entity targetAnimal = findPlayerAnimal(player, playerId);
                    if (targetAnimal != null && !targetAnimal.isDead()) {
                        
                        // Make the animal follow the player using improved pathfinding
                        makeAnimalFollow(player, targetAnimal);
                    }
                }
            }
        }
    }
    
    // Periodic task to make animals stick around even when player isn't moving
    public static void startStickAroundTask(org.bukkit.plugin.Plugin plugin) {
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                for (java.util.Map.Entry<String, org.bukkit.Location> entry : lastPlayerLocations.entrySet()) {
                    String playerId = entry.getKey();
                    org.bukkit.Location lastLocation = entry.getValue();
                    
                    // Find the animal for this player
                    for (org.bukkit.World world : org.bukkit.Bukkit.getWorlds()) {
                        for (Entity entity : world.getEntities()) {
                            if (entity.hasMetadata("animal_disguise_player") && 
                                entity.getMetadata("animal_disguise_player").get(0).asString().equals(playerId)) {
                                
                                // Make animal stick around the last known location
                                AnimalFollowListener instance = new AnimalFollowListener();
                                instance.makeAnimalStickAround(entity, lastLocation);
                                break;
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Run every second
    }
    
    private String getPlayerIdFromFood(ItemStack food) {
        if (food.hasItemMeta()) {
            ItemMeta meta = food.getItemMeta();
            if (meta.hasLore() && meta.getLore().size() >= 3) {
                String idLine = meta.getLore().get(2); // Third line contains the ID
                if (idLine.startsWith("§7ID: ")) {
                    return idLine.substring(6); // Remove "§7ID: " prefix
                }
            }
        }
        return null;
    }
    
    private Entity findPlayerAnimal(Player player, String playerId) {
        for (Entity entity : player.getWorld().getEntities()) {
            if (entity.hasMetadata("animal_disguise_player") && 
                entity.getMetadata("animal_disguise_player").get(0).asString().equals(playerId)) {
                return entity;
            }
        }
        return null;
    }
    
    private void makeAnimalFollow(Player player, Entity animal) {
        // Use improved pathfinding to make the animal follow
        if (animal instanceof org.bukkit.entity.LivingEntity) {
            org.bukkit.entity.LivingEntity livingAnimal = (org.bukkit.entity.LivingEntity) animal;
            
            // Calculate distance to player
            double distance = animal.getLocation().distance(player.getLocation());
            
            // Only make the animal follow if it's within a reasonable range
            if (distance > 3.0 && distance < 20.0) {
                // Use improved pathfinding with collision detection
                Location animalLoc = animal.getLocation();
                Location playerLoc = player.getLocation();
                
                // Calculate direction to player
                double dx = playerLoc.getX() - animalLoc.getX();
                double dz = playerLoc.getZ() - animalLoc.getZ();
                double distance2D = Math.sqrt(dx * dx + dz * dz);
                
                if (distance2D > 0) {
                    // Normalize direction
                    dx /= distance2D;
                    dz /= distance2D;
                    
                    // Check for obstacles and find a clear path
                    Location newLoc = findClearPath(animalLoc, dx, dz, animal.getWorld());
                    
                    if (newLoc != null) {
                        // Make the animal face the player
                        newLoc.setYaw((float) Math.toDegrees(Math.atan2(-dx, dz)));
                        
                        // Move the animal smoothly
                        animal.teleport(newLoc);
                    }
                }
            }
        }
    }
    
    private void makeAnimalStickAround(Entity animal, Location targetLocation) {
        if (animal instanceof org.bukkit.entity.LivingEntity) {
            org.bukkit.entity.LivingEntity livingAnimal = (org.bukkit.entity.LivingEntity) animal;
            
            // Calculate distance to target location
            double distance = animal.getLocation().distance(targetLocation);
            
            // If animal is too far from the last known location, bring it closer
            if (distance > 8.0) {
                Location animalLoc = animal.getLocation();
                
                // Calculate direction to target
                double dx = targetLocation.getX() - animalLoc.getX();
                double dz = targetLocation.getZ() - animalLoc.getZ();
                double distance2D = Math.sqrt(dx * dx + dz * dz);
                
                if (distance2D > 0) {
                    // Normalize direction
                    dx /= distance2D;
                    dz /= distance2D;
                    
                    // Find a clear path back to the target area
                    Location newLoc = findClearPath(animalLoc, dx, dz, animal.getWorld());
                    
                    if (newLoc != null) {
                        // Make the animal face the target
                        newLoc.setYaw((float) Math.toDegrees(Math.atan2(-dx, dz)));
                        
                        // Move the animal back to the area
                        animal.teleport(newLoc);
                    }
                }
            }
        }
    }
    
    private Location findClearPath(Location currentLoc, double dx, double dz, org.bukkit.World world) {
        // Try to find a clear path with collision detection
        double moveDistance = 0.3;
        
        // Try different movement distances to find a clear path
        for (double distance : new double[]{moveDistance, moveDistance * 0.5, moveDistance * 0.25}) {
            Location testLoc = currentLoc.clone().add(dx * distance, 0, dz * distance);
            
            // Check if the new location is clear
            if (isLocationClear(testLoc, world)) {
                return testLoc;
            }
            
            // Try with slight elevation changes for stairs
            Location testLocUp = testLoc.clone().add(0, 0.5, 0);
            if (isLocationClear(testLocUp, world)) {
                return testLocUp;
            }
            
            Location testLocDown = testLoc.clone().add(0, -0.5, 0);
            if (isLocationClear(testLocDown, world)) {
                return testLocDown;
            }
        }
        
        // If no clear path found, try to move in a different direction
        return currentLoc.clone().add(dx * 0.1, 0, dz * 0.1);
    }
    
    private boolean isLocationClear(Location location, org.bukkit.World world) {
        // Check if the location and surrounding area is clear of solid blocks
        org.bukkit.Material blockType = location.getBlock().getType();
        org.bukkit.Material blockAbove = location.clone().add(0, 1, 0).getBlock().getType();
        
        // Allow movement through air, water, and some passable blocks
        return blockType == org.bukkit.Material.AIR || 
               blockType == org.bukkit.Material.WATER || 
               blockType == org.bukkit.Material.GRASS ||
               blockType == org.bukkit.Material.TALL_GRASS ||
               blockType == org.bukkit.Material.SNOW ||
               blockType == org.bukkit.Material.CAVE_AIR ||
               (blockAbove == org.bukkit.Material.AIR || blockAbove == org.bukkit.Material.CAVE_AIR);
    }
} 