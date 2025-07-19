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
    
    private static final String EXIT_MORPH_NAME = ChatColor.RED + "Exit Morph";
    private static final String EXIT_MORPH_LORE = ChatColor.YELLOW + "Left Click to exit morph mode!";
    
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
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        
        // Position player at a distance from the mob for the following system
        Location mobLocation = target.getLocation().clone();
        Location playerLocation = mobLocation.clone().add(2, 1, 0); // 2 blocks away, 1 block up
        player.teleport(playerLocation);
        
        // Start movement task
        startMovementTask(player, target);
        
        // Give exit item
        giveExitMorphItem(player);
        
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
        player.setAllowFlight(false);
        player.setFlying(false);
        player.teleport(originalLocations.get(playerId));
        
        // Restore inventory
        ItemStack[] originalInventory = originalInventories.get(playerId);
        if (originalInventory != null) {
            player.getInventory().setContents(originalInventory);
        }
        
        // Clean up data
        morphedPlayers.remove(playerId);
        originalPlayers.remove(playerId);
        originalLocations.remove(playerId);
        originalGameModes.remove(playerId);
        originalInventories.remove(playerId);
        movementTasks.remove(playerId);
        lastMovementTime.remove(playerId);
        
        player.sendMessage(ChatColor.AQUA + "You have exited morph mode!");
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
        ItemStack item = event.getItem();
        
        // Handle exit morph item - only if player is holding the exit head
        if (item != null && item.getType().name().contains("HEAD") && item.hasItemMeta()) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta != null && EXIT_MORPH_NAME.equals(meta.getDisplayName()) &&
                meta.hasLore() && meta.getLore().contains(EXIT_MORPH_LORE)) {
                
                if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    item.setAmount(item.getAmount() - 1);
                    exitMorph(player);
                    event.setCancelled(true);
                    return; // Exit early to prevent attack logic
                }
            }
        }
        
        // Handle morph attacks - only if not holding the exit head
        if (isMorphed(player) && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            // Don't attack if we're holding the exit head
            if (item != null && item.getType().name().contains("HEAD")) {
                return;
            }
            
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
                            targetEntity.damage(4.0, livingMob); // Attack damage
                            player.sendMessage(ChatColor.RED + "You attacked " + targetEntity.getName() + "!");
                        }
                    });
                
                // If no entities found, just show a message
                if (livingMob.getNearbyEntities(3, 3, 3).stream()
                    .noneMatch(entity -> entity instanceof LivingEntity && !(entity instanceof Player))) {
                    player.sendMessage(ChatColor.YELLOW + "No targets nearby to attack!");
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
        // Protect morphed entities from taking damage
        Entity damagedEntity = event.getEntity();
        for (Entity morphedEntity : morphedPlayers.values()) {
            if (morphedEntity.equals(damagedEntity)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    
    private static void giveExitMorphItem(Player player) {
        ItemStack head = new ItemStack(org.bukkit.Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(EXIT_MORPH_NAME);
        meta.setLore(Collections.singletonList(EXIT_MORPH_LORE));
        meta.setOwningPlayer(player);
        head.setItemMeta(meta);
        player.getInventory().addItem(head);
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
            player.sendMessage(ChatColor.YELLOW + "Exit Head: " + ChatColor.WHITE + "Alternative exit");
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
} 