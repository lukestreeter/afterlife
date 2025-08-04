package me.yodeling_goat.afterlifeplugin.afterlife.util;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.EntityDamageEvent;

import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

import java.util.HashMap;
import java.util.UUID;
import java.util.Collections;

public class MobMorphManager implements Listener {
    private static final HashMap<UUID, Entity> morphedPlayers = new HashMap<>();
    private static final HashMap<UUID, Player> originalPlayers = new HashMap<>();
    private static final HashMap<UUID, Location> originalLocations = new HashMap<>();
    private static final HashMap<UUID, GameMode> originalGameModes = new HashMap<>();
    private static final HashMap<UUID, ItemStack[]> originalInventories = new HashMap<>();
    private static final HashMap<UUID, BukkitRunnable> movementTasks = new HashMap<>();
    private static final HashMap<UUID, Long> lastMovementTime = new HashMap<>();
    private static final HashMap<UUID, Long> lastAttackTime = new HashMap<>();
    

    
    private static Plugin plugin;
    
    public MobMorphManager(Plugin plugin) {
        MobMorphManager.plugin = plugin;
    }
    
    public static void morphIntoMob(Player player, LivingEntity target) {
        UUID playerId = player.getUniqueId();
        
        // Store original player data
        originalPlayers.put(playerId, player);
        originalLocations.put(playerId, player.getLocation().clone());
        originalGameModes.put(playerId, player.getGameMode());
        originalInventories.put(playerId, player.getInventory().getContents().clone());
        
        // Clear player's inventory to prevent head-related issues
        player.getInventory().clear();
        
        // Store the original mob (don't create a copy)
        morphedPlayers.put(playerId, target);
        
        // Make mob invulnerable but keep AI for movement
        if (target instanceof Mob) {
            Mob mob = (Mob) target;
            mob.setInvulnerable(true);
            // Don't disable AI - we need it for movement!
        }
        
        // Hide the original player and put them in adventure mode for WASD control
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));
        player.removePotionEffect(PotionEffectType.GLOWING); // Remove glowing effect when morphed
        player.setGameMode(GameMode.ADVENTURE); // Use adventure mode to prevent teleporting
        player.setAllowFlight(true);
        player.setFlying(true);
        
        // Position player at a distance from the mob for the following system
        Location mobLocation = target.getLocation().clone();
        Location playerLocation = mobLocation.clone().add(2, 1, 0); // 2 blocks away, 1 block up
        player.teleport(playerLocation);
        
        // Start movement task
        startMovementTask(player, target);
        
        // Show morph UI
        showMorphUI(player);
        
        player.sendMessage(ChatColor.GREEN + "You have morphed into " + 
                          ChatColor.YELLOW + target.getName() + 
                          ChatColor.GREEN + "! The mob will follow you as you move.");
    }
    
    public static void exitMorph(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (!morphedPlayers.containsKey(playerId)) {
            return;
        }
        
        // Stop movement task
        stopMovementTask(player);
        
        // Restore the original mob's vulnerability
        Entity morphedEntity = morphedPlayers.get(playerId);
        if (morphedEntity instanceof Mob) {
            Mob mob = (Mob) morphedEntity;
            mob.setInvulnerable(false);
        }
        
        // Restore player
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false)); // Restore glowing effect
        player.setGameMode(originalGameModes.get(playerId));
        
        // Restore afterlife effects if player is in afterlife
        if (AfterlifeManager.isInAfterlife(player)) {
            AfterlifeManager.applyPermanentAfterlifeEffects(player);
        }
        
        // Restore flight state based on whether player is in afterlife
        if (AfterlifeManager.isInAfterlife(player)) {
            player.setAllowFlight(true);
            player.setFlying(true);
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
        
        player.teleport(originalLocations.get(playerId));
        
        // Restore inventory
        ItemStack[] originalInventory = originalInventories.get(playerId);
        if (originalInventory != null) {
            player.getInventory().setContents(originalInventory);
        }
        
        // Remove any remaining head items that might cause issues
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType().name().contains("HEAD")) {
                player.getInventory().remove(item);
            }
        }
        
        // Clean up data
        morphedPlayers.remove(playerId);
        originalPlayers.remove(playerId);
        originalLocations.remove(playerId);
        originalGameModes.remove(playerId);
        originalInventories.remove(playerId);
        movementTasks.remove(playerId);
        lastMovementTime.remove(playerId);
        lastAttackTime.remove(playerId);
    }
    
    public static boolean isMorphed(Player player) {
        return morphedPlayers.containsKey(player.getUniqueId());
    }
    
    public static Entity getMorphedEntity(Player player) {
        return morphedPlayers.get(player.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        // Exit morph when player sneaks (holds shift)
        if (isMorphed(player) && event.isSneaking()) {
            exitMorph(player);
        }
    }
    
    // Add a command-based attack system
    public static void performAttack(Player player) {
        if (!isMorphed(player)) {
            player.sendMessage(ChatColor.RED + "You are not morphed!");
            return;
        }
        
        // Check cooldown (5 seconds = 5000 milliseconds)
        long currentTime = System.currentTimeMillis();
        long lastAttack = lastAttackTime.getOrDefault(player.getUniqueId(), 0L);
        long timeSinceLastAttack = currentTime - lastAttack;
        if (timeSinceLastAttack < 5000) {
            long remainingTime = 5000 - timeSinceLastAttack;
            int remainingSeconds = (int) Math.ceil(remainingTime / 1000.0);
            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.YELLOW + remainingSeconds + ChatColor.RED + " seconds to use this again!");
            return;
        }
        
        // Update last attack time
        lastAttackTime.put(player.getUniqueId(), currentTime);
        
        Entity morphedEntity = getMorphedEntity(player);
        if (morphedEntity instanceof LivingEntity) {
            LivingEntity livingMob = (LivingEntity) morphedEntity;
            
            // Find nearby entities to attack
            livingMob.getNearbyEntities(3, 3, 3).stream()
                .filter(entity -> entity instanceof LivingEntity && !(entity instanceof Player))
                .findFirst()
                .ifPresent(target -> {
                    if (target instanceof LivingEntity) {
                        LivingEntity targetEntity = (LivingEntity) target;
                        targetEntity.damage(4.0, livingMob);
                        player.sendMessage(ChatColor.RED + "You attacked " + targetEntity.getName() + "!");
                    }
                });
            
            // If no entities found
            if (livingMob.getNearbyEntities(3, 3, 3).stream()
                .noneMatch(entity -> entity instanceof LivingEntity && !(entity instanceof Player))) {
                player.sendMessage(ChatColor.YELLOW + "No targets nearby to attack!");
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Ignore head-related interactions to prevent accidental exits
        ItemStack item = event.getItem();
        if (item != null && item.getType().name().contains("HEAD")) {
            return;
        }
        
        // Handle morph attacks
        if (isMorphed(player) && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            
            // Check cooldown (5 seconds = 5000 milliseconds)
            long currentTime = System.currentTimeMillis();
            long lastAttack = lastAttackTime.getOrDefault(player.getUniqueId(), 0L);
            long timeSinceLastAttack = currentTime - lastAttack;
            if (timeSinceLastAttack < 5000) {
                long remainingTime = 5000 - timeSinceLastAttack;
                int remainingSeconds = (int) Math.ceil(remainingTime / 1000.0);
                player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.YELLOW + remainingSeconds + ChatColor.RED + " seconds to use this again!");
                return;
            }
            
            // Update last attack time
            lastAttackTime.put(player.getUniqueId(), currentTime);
            
            Entity morphedEntity = getMorphedEntity(player);
            if (morphedEntity instanceof LivingEntity) {
                LivingEntity livingMob = (LivingEntity) morphedEntity;
                
                // Find the entity the player is looking at (within 100 blocks)
                Entity target = getTargetEntity(player, 100.0);
                
                if (target != null && target != player) { // Prevent targeting the controlling player
                    // Make the morphed mob attack the target
                    if (morphedEntity instanceof Mob) {
                        Mob mob = (Mob) morphedEntity;
                        if (target instanceof LivingEntity) {
                            mob.setTarget((LivingEntity) target);
                        }
                    }
                    
                    // Shoot laser projectile at target
                    shootLaserProjectile(morphedEntity, target, player);
                } else if (target == player) {
                    // If player is targeting themselves, show a message
                    player.sendMessage(ChatColor.YELLOW + "You can't shoot yourself!");
                } else {
                    // If no entity target, try to shoot at blocks
                    Location blockTarget = getBlockTarget(player, 100.0);
                    if (blockTarget != null) {
                        shootLaserAtBlock(morphedEntity, blockTarget, player);
                    } else {
                        // If no specific target, attack nearby entities like before
                        livingMob.getNearbyEntities(5, 5, 5).stream()
                            .filter(entity -> entity != player) // Exclude the controlling player
                            .findFirst()
                            .ifPresent(nearbyTarget -> {
                                // Shoot laser projectile at nearby target
                                shootLaserProjectile(morphedEntity, nearbyTarget, player);
                            });
                        
                        if (livingMob.getNearbyEntities(5, 5, 5).stream()
                            .filter(entity -> entity != player)
                            .findFirst().isEmpty()) {
                            player.sendMessage(ChatColor.YELLOW + "No targets nearby to attack!");
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity target = event.getRightClicked();
        
        // Handle block breaking/placing as morphed mob
        if (isMorphed(player) && target instanceof LivingEntity) {
            Entity morphedEntity = getMorphedEntity(player);
            if (morphedEntity != null) {
                // Interact with the target entity
                if (target instanceof LivingEntity) {
                    LivingEntity targetEntity = (LivingEntity) target;
                    targetEntity.damage(2.0, (LivingEntity) morphedEntity);
                    player.sendMessage(ChatColor.RED + "You attacked " + targetEntity.getName() + "!");
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (isMorphed(player)) {
            // Allow block breaking as morphed mob
            player.sendMessage(ChatColor.GREEN + "You broke a block as " + 
                              getMorphedEntity(player).getName() + "!");
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (isMorphed(player)) {
            // Allow block placing as morphed mob
            player.sendMessage(ChatColor.GREEN + "You placed a block as " + 
                              getMorphedEntity(player).getName() + "!");
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isMorphed(player)) return;
        
        Entity morphedEntity = getMorphedEntity(player);
        if (morphedEntity == null || morphedEntity.isDead()) {
            exitMorph(player);
            return;
        }
        
        // Get movement direction from player
        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (to != null && !from.equals(to)) {
            // Calculate distance between player and mob
            Location mobLocation = morphedEntity.getLocation();
            double distance = to.distance(mobLocation);
            
            // If mob is too far away, make it follow the player with dynamic speed
            if (distance > 3.0) {
                // Calculate direction from mob to player
                Vector direction = to.toVector().subtract(mobLocation.toVector()).normalize();
                
                // Dynamic speed based on distance - the further away, the faster it moves
                double speedMultiplier = Math.min(1.2, 0.4 + (distance - 3.0) * 0.2); // Scales from 0.4 to 1.2
                
                // Apply velocity to make mob follow player
                Vector velocity = direction.multiply(speedMultiplier);
                morphedEntity.setVelocity(velocity);
                
                // Update mob rotation to face the player
                morphedEntity.setRotation(to.getYaw(), to.getPitch());
            }
            // If mob is too close, make it back away slightly
            else if (distance < 1.5) {
                // Calculate direction away from player
                Vector direction = mobLocation.toVector().subtract(to.toVector()).normalize();
                
                // Apply small velocity to back away
                Vector velocity = direction.multiply(0.3);
                morphedEntity.setVelocity(velocity);
            }
            // If mob is at good distance, just update rotation to face player
            else {
                // Just update rotation to face the player
                morphedEntity.setRotation(to.getYaw(), to.getPitch());
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // Protect morphed entities and controlling players from ALL damage
        Entity damagedEntity = event.getEntity();
        
        // Check if the damaged entity is a morphed entity
        for (Entity morphedEntity : morphedPlayers.values()) {
            if (morphedEntity.equals(damagedEntity)) {
                // Cancel ALL damage to morphed entities
                event.setCancelled(true);
                return;
            }
        }
        
        // Check if the damaged entity is a player controlling a morphed entity
        if (damagedEntity instanceof Player) {
            Player player = (Player) damagedEntity;
            if (isMorphed(player)) {
                // Cancel ALL damage to controlling player
                event.setCancelled(true);
                return;
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Protect morphed entities and controlling players from ALL damage
        Entity damagedEntity = event.getEntity();
        Entity damager = event.getDamager();
        
        // Check if the damaged entity is a morphed entity
        for (Entity morphedEntity : morphedPlayers.values()) {
            if (morphedEntity.equals(damagedEntity)) {
                // Cancel ALL damage to morphed entities
                event.setCancelled(true);
                return;
            }
        }
        
        // Check if the damaged entity is a player controlling a morphed entity
        if (damagedEntity instanceof Player) {
            Player player = (Player) damagedEntity;
            if (isMorphed(player)) {
                // Cancel ALL damage to controlling player
                event.setCancelled(true);
                return;
            }
        }
    }
    


    

    
    private static void showMorphUI(Player player) {
        Entity morphedEntity = getMorphedEntity(player);
        if (morphedEntity != null) {
            player.sendMessage(ChatColor.GOLD + "=== Morph Controls ===");
            player.sendMessage(ChatColor.YELLOW + "WASD: " + ChatColor.WHITE + "Move around (mob will follow)");
            player.sendMessage(ChatColor.YELLOW + "Mouse: " + ChatColor.WHITE + "Look around");
            player.sendMessage(ChatColor.YELLOW + "Left Click: " + ChatColor.WHITE + "Attack nearby entities");
            player.sendMessage(ChatColor.YELLOW + "Right Click: " + ChatColor.WHITE + "Interact with entities");
            player.sendMessage(ChatColor.YELLOW + "Shift: " + ChatColor.WHITE + "Exit morph (hold shift)");
            player.sendMessage(ChatColor.GREEN + "You are now: " + ChatColor.AQUA + morphedEntity.getName());
            player.sendMessage(ChatColor.GRAY + "Tip: The mob will follow you as you move around!");
        }
    }

    private static void startMovementTask(Player player, LivingEntity morphedEntity) {
        UUID playerId = player.getUniqueId();
        
        // Cancel existing task if any
        stopMovementTask(player);
        
        // For WASD movement, we don't need a continuous task
        // The PlayerMoveEvent will handle movement
    }
    
    private static void stopMovementTask(Player player) {
        UUID playerId = player.getUniqueId();
        BukkitRunnable task = movementTasks.get(playerId);
        if (task != null) {
            task.cancel();
            movementTasks.remove(playerId);
        }
    }

    // Helper method to get the entity the player is looking at
    private Entity getTargetEntity(Player player, double maxDistance) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        
        for (double distance = 0; distance <= maxDistance; distance += 0.5) {
            Location checkLocation = eyeLocation.clone().add(direction.clone().multiply(distance));
            
            // Check for entities at this location
            for (Entity entity : player.getWorld().getNearbyEntities(checkLocation, 0.5, 0.5, 0.5)) {
                if (entity != getMorphedEntity(player) && entity != player) {
                    return entity;
                }
            }
        }
        return null;
    }

    private static Location getBlockTarget(Player player, double maxDistance) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        for (double distance = 0; distance <= maxDistance; distance += 0.5) {
            Location checkLocation = eyeLocation.clone().add(direction.clone().multiply(distance));
            
            // Check for blocks at this location
            if (player.getWorld().getBlockAt(checkLocation).getType() != org.bukkit.Material.AIR) {
                return checkLocation;
            }
        }
        return null;
    }

    private static void shootLaserProjectile(Entity shooter, Entity target, Player player) {
        Location shooterLocation = shooter.getLocation().add(0, 1, 0); // Shoot from head level
        Location targetLocation = target.getLocation().add(0, 1, 0);

        Vector direction = targetLocation.toVector().subtract(shooterLocation.toVector()).normalize();
        double distance = shooterLocation.distance(targetLocation);

        // Calculate projectile speed based on distance
        double projectileSpeed = Math.min(1.5, 0.5 + (distance - 1.0) * 0.1);

        // Create a fireball projectile for laser effect
        org.bukkit.entity.Fireball fireball = shooter.getWorld().spawn(shooterLocation, org.bukkit.entity.Fireball.class);
        
        // Set fireball properties - BIGGER EXPLOSIONS with sheep protection
        fireball.setDirection(direction);
        fireball.setVelocity(direction.multiply(projectileSpeed));
        fireball.setYield(4.0f); // BIG explosion radius (was 0.0f)
        fireball.setIsIncendiary(true); // Allow fire spread for dramatic effect
        
        // Make it look like a laser by setting it on fire briefly
        fireball.setFireTicks(20); // 1 second of fire
        
        // Store the morphed entity to protect it from this fireball
        Entity morphedEntity = getMorphedEntity(player);
        
        // Add a task to damage the target when the projectile hits
        BukkitRunnable hitTask = new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 20 * 2; // Reduced to 2 seconds max flight time

            @Override
            public void run() {
                if (ticks >= maxTicks || fireball.isDead()) {
                    this.cancel();
                    return;
                }
                
                // Check if projectile hit the target
                if (fireball.getLocation().distance(targetLocation) < 1.0) {
                    // Handle different entity types
                    if (target instanceof LivingEntity) {
                        LivingEntity livingTarget = (LivingEntity) target;
                        // Don't damage the morphed entity or the controlling player
                        if (target != morphedEntity && target != player) {
                            livingTarget.damage(12.0, shooter); // Increased damage (was 8.0)
                        }
                    } else if (target instanceof org.bukkit.entity.Item) {
                        // Destroy items
                        target.remove();
                    } else if (target instanceof org.bukkit.entity.Projectile) {
                        // Destroy other projectiles
                        target.remove();
                    } else if (target instanceof org.bukkit.entity.Minecart) {
                        // Damage minecarts
                        target.remove();
                    } else if (target instanceof org.bukkit.entity.Boat) {
                        // Damage boats
                        target.remove();
                    }
                    
                    fireball.remove();
                    this.cancel();
                }
                
                ticks++;
            }
        };
        hitTask.runTaskTimer(plugin, 0, 1);
    }
    
    private static void shootLaserAtBlock(Entity shooter, Location blockLocation, Player player) {
        Location shooterLocation = shooter.getLocation().add(0, 1, 0); // Shoot from head level
        
        Vector direction = blockLocation.toVector().subtract(shooterLocation.toVector()).normalize();
        double distance = shooterLocation.distance(blockLocation);
        
        // Calculate projectile speed based on distance
        double projectileSpeed = Math.min(1.5, 0.5 + (distance - 1.0) * 0.1);
        
        // Create a fireball projectile for laser effect
        org.bukkit.entity.Fireball fireball = shooter.getWorld().spawn(shooterLocation, org.bukkit.entity.Fireball.class);
        
        // Set fireball properties - BIGGER EXPLOSIONS with sheep protection
        fireball.setDirection(direction);
        fireball.setVelocity(direction.multiply(projectileSpeed));
        fireball.setYield(4.0f); // BIG explosion radius (was 0.0f)
        fireball.setIsIncendiary(true); // Allow fire spread for dramatic effect
        
        // Make it look like a laser by setting it on fire briefly
        fireball.setFireTicks(20); // 1 second of fire
        
        // Store the morphed entity to protect it from this fireball
        Entity morphedEntity = getMorphedEntity(player);
        
        // Add a task to break the block when the projectile hits
        BukkitRunnable hitTask = new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 20 * 2; // Reduced to 2 seconds max flight time

            @Override
            public void run() {
                if (ticks >= maxTicks || fireball.isDead()) {
                    this.cancel();
                    return;
                }
                
                // Check if projectile hit the block
                if (fireball.getLocation().distance(blockLocation) < 1.0) {
                    // Break multiple blocks in a radius around the impact point
                    Location impactLocation = fireball.getLocation();
                    int radius = 3; // 3 block radius for destruction
                    
                    for (int x = -radius; x <= radius; x++) {
                        for (int y = -radius; y <= radius; y++) {
                            for (int z = -radius; z <= radius; z++) {
                                Location blockLoc = impactLocation.clone().add(x, y, z);
                                double distance = impactLocation.distance(blockLoc);
                                
                                // Only break blocks within the radius
                                if (distance <= radius) {
                                    org.bukkit.block.Block block = blockLoc.getBlock();
                                    // Don't break bedrock or other unbreakable blocks
                                    if (block.getType() != org.bukkit.Material.BEDROCK && 
                                        block.getType() != org.bukkit.Material.BARRIER &&
                                        block.getType() != org.bukkit.Material.COMMAND_BLOCK) {
                                        block.setType(org.bukkit.Material.AIR);
                                    }
                                }
                            }
                        }
                    }
                    
                    fireball.remove();
                    this.cancel();
                }
                
                ticks++;
            }
        };
        hitTask.runTaskTimer(plugin, 0, 1);
    }
} 