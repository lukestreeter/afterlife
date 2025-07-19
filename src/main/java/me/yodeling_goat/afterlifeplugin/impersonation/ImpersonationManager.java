package me.yodeling_goat.afterlifeplugin.impersonation;

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Animals;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.impersonation.events.PlayerImpersonateEvent;
import me.yodeling_goat.afterlifeplugin.impersonation.events.PlayerUnimpersonateEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImpersonationManager {
    private static final Map<UUID, ImpersonationData> impersonatingPlayers = new HashMap<>();

    public static class ImpersonationData {
        private final org.bukkit.entity.EntityType entityType;
        private final Player player;
        private final Location originalLocation;
        private final boolean wasInAfterlife;

        public ImpersonationData(org.bukkit.entity.EntityType entityType, Player player, Location originalLocation, boolean wasInAfterlife) {
            this.entityType = entityType;
            this.player = player;
            this.originalLocation = originalLocation;
            this.wasInAfterlife = wasInAfterlife;
        }

        public org.bukkit.entity.EntityType getEntityType() { return entityType; }
        public Player getPlayer() { return player; }
        public Location getOriginalLocation() { return originalLocation; }
        public boolean wasInAfterlife() { return wasInAfterlife; }
    }

    public static boolean impersonateEntity(Player player, Entity target) {
        // Check if player is in afterlife
        if (!AfterlifeManager.isInAfterlife(player)) {
            player.sendMessage("§cYou can only impersonate animals while in the afterlife!");
            return false;
        }

        // Check if target is a valid animal
        if (!(target instanceof Animals)) {
            player.sendMessage("§cYou can only impersonate animals!");
            return false;
        }

        // Check if player is already impersonating something
        if (isPlayerImpersonating(player)) {
            player.sendMessage("§cYou are already impersonating an animal! Use /unimpersonate first.");
            return false;
        }

        // Store original location and afterlife state
        Location originalLocation = player.getLocation();
        boolean wasInAfterlife = AfterlifeManager.isInAfterlife(player);

        // Create impersonation data
        ImpersonationData data = new ImpersonationData(target.getType(), player, originalLocation, wasInAfterlife);
        impersonatingPlayers.put(player.getUniqueId(), data);

        // Apply impersonation effects
        applyImpersonationEffects(player, target);

        // Remove the target entity
        target.remove();

        // Call custom event
        PlayerImpersonateEvent event = new PlayerImpersonateEvent(player, target.getType());
        Bukkit.getPluginManager().callEvent(event);

        player.sendMessage("§aYou are now impersonating a " + target.getType().name().toLowerCase() + "!");
        return true;
    }

    public static boolean unimpersonateEntity(Player player) {
        ImpersonationData data = impersonatingPlayers.get(player.getUniqueId());
        if (data == null) {
            player.sendMessage("§cYou are not currently impersonating any animal!");
            return false;
        }

        // Remove impersonation effects
        removeImpersonationEffects(player, data);

        // Restore player to original location
        player.teleport(data.getOriginalLocation());

        // Restore afterlife state if they were in it
        if (data.wasInAfterlife()) {
            AfterlifeManager.applyAllAfterlifeEffects(player);
        }

        // Clean up data
        impersonatingPlayers.remove(player.getUniqueId());

        // Call custom event
        PlayerUnimpersonateEvent event = new PlayerUnimpersonateEvent(player, data.getEntityType());
        Bukkit.getPluginManager().callEvent(event);

        player.sendMessage("§aYou have stopped impersonating the " + data.getEntityType().name().toLowerCase() + "!");
        return true;
    }

    public static boolean isPlayerImpersonating(Player player) {
        return impersonatingPlayers.containsKey(player.getUniqueId());
    }

    public static org.bukkit.entity.EntityType getImpersonatedType(Player player) {
        ImpersonationData data = impersonatingPlayers.get(player.getUniqueId());
        return data != null ? data.getEntityType() : null;
    }

    private static void applyImpersonationEffects(Player player, Entity target) {
        // Make the player completely invisible
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        
        // Add night vision so they can see clearly
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        
        // Hide player from all other players completely
        for (org.bukkit.entity.Player otherPlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (!otherPlayer.equals(player)) {
                otherPlayer.hidePlayer(org.bukkit.Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), player);
            }
        }
        
        // Execute the summon command directly to create the animal disguise
        String command = buildAnimalSummonCommand(target.getType(), player.getName(), player.getLocation());
        Bukkit.getLogger().info("[ImpersonationManager] Executing command: " + command);
        org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), command);
        
        // Store the player name in metadata for tracking
        player.setMetadata("animal_disguise", new org.bukkit.metadata.FixedMetadataValue(
            org.bukkit.Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), player.getName()));
        
        // Give the player the appropriate food item to make the animal follow
        giveAnimalFood(player, target.getType());
    }
    
    private static String buildAnimalSummonCommand(org.bukkit.entity.EntityType entityType, String playerName, org.bukkit.Location location) {
        // Create a summon command with specific properties for the animal disguise
        String baseCommand = "summon " + entityType.name().toLowerCase() + " " + location.getX() + " " + location.getY() + " " + location.getZ();
        
        // Add custom properties based on entity type - enable AI for natural movement
        switch (entityType) {
            case CHICKEN:
                return baseCommand + " {Invulnerable:1b,Silent:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case COW:
                return baseCommand + " {Invulnerable:1b,Silent:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case PIG:
                return baseCommand + " {Invulnerable:1b,Silent:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case SHEEP:
                return baseCommand + " {Invulnerable:1b,Silent:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case HORSE:
                return baseCommand + " {Invulnerable:1b,Silent:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case RABBIT:
                return baseCommand + " {Invulnerable:1b,Silent:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case FOX:
                return baseCommand + " {Invulnerable:1b,Silent:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case WOLF:
                return baseCommand + " {Invulnerable:1b,Silent:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case CAT:
                return baseCommand + " {Invulnerable:1b,Silent:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case PARROT:
                return baseCommand + " {Invulnerable:1b,Silent:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            default:
                return baseCommand + " {Invulnerable:1b,Silent:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
        }
    }

    private static void removeImpersonationEffects(Player player, ImpersonationData data) {
        // Remove invisibility and other potion effects
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        
        // Show player to all other players again
        for (org.bukkit.entity.Player otherPlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (!otherPlayer.equals(player)) {
                otherPlayer.showPlayer(org.bukkit.Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), player);
            }
        }
        
        // Remove the spawned animal entity
        if (player.hasMetadata("animal_disguise")) {
            // Find and remove the spawned animal entity
            for (Entity entity : player.getWorld().getEntities()) {
                if (entity.hasMetadata("animal_disguise_player") && 
                    entity.getMetadata("animal_disguise_player").get(0).asString().equals(player.getName())) {
                    entity.remove();
                    break;
                }
            }
            player.removeMetadata("animal_disguise", org.bukkit.Bukkit.getPluginManager().getPlugin("AfterLifePlugin"));
        }
        
        // Remove the animal lure food item
        org.bukkit.inventory.ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.hasItemMeta() && mainHand.getItemMeta().hasDisplayName() && 
            mainHand.getItemMeta().getDisplayName().equals("§6Animal Lure")) {
            player.getInventory().setItemInMainHand(null);
        }
    }



    public static void handlePlayerQuit(Player player) {
        ImpersonationData data = impersonatingPlayers.get(player.getUniqueId());
        if (data != null) {
            // Remove the spawned animal entity
            if (player.hasMetadata("animal_disguise")) {
                // Find and remove the spawned animal entity
                for (Entity entity : player.getWorld().getEntities()) {
                    if (entity.hasMetadata("animal_disguise_player") && 
                        entity.getMetadata("animal_disguise_player").get(0).asString().equals(player.getName())) {
                        entity.remove();
                        break;
                    }
                }
                player.removeMetadata("animal_disguise", org.bukkit.Bukkit.getPluginManager().getPlugin("AfterLifePlugin"));
            }
            
            // Remove the animal lure food item
            org.bukkit.inventory.ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (mainHand.hasItemMeta() && mainHand.getItemMeta().hasDisplayName() && 
                mainHand.getItemMeta().getDisplayName().equals("§6Animal Lure")) {
                player.getInventory().setItemInMainHand(null);
            }
            
            // Clean up data
            impersonatingPlayers.remove(player.getUniqueId());
        }
    }

    public static void handlePlayerDeath(Player player) {
        ImpersonationData data = impersonatingPlayers.get(player.getUniqueId());
        if (data != null) {
            // Remove impersonation effects
            removeImpersonationEffects(player, data);
            
            // Clean up data
            impersonatingPlayers.remove(player.getUniqueId());
            
            player.sendMessage("§cYou have died and lost your animal disguise!");
        }
    }
    
    private static void giveAnimalFood(Player player, org.bukkit.entity.EntityType entityType) {
        org.bukkit.Material foodItem = null;
        
        // Determine the appropriate food item for each animal type
        switch (entityType) {
            case PIG:
                foodItem = org.bukkit.Material.CARROT;
                break;
            case COW:
            case SHEEP:
                foodItem = org.bukkit.Material.WHEAT;
                break;
            case CHICKEN:
                foodItem = org.bukkit.Material.WHEAT_SEEDS;
                break;
            case RABBIT:
                foodItem = org.bukkit.Material.CARROT;
                break;
            case HORSE:
            case DONKEY:
            case MULE:
                foodItem = org.bukkit.Material.GOLDEN_APPLE;
                break;
            case WOLF:
                foodItem = org.bukkit.Material.BONE;
                break;
            case CAT:
                foodItem = org.bukkit.Material.COD;
                break;
            case FOX:
                foodItem = org.bukkit.Material.SWEET_BERRIES;
                break;
            case PARROT:
                foodItem = org.bukkit.Material.COOKIE;
                break;
            default:
                foodItem = org.bukkit.Material.WHEAT; // Default food
                break;
        }
        
        if (foodItem != null) {
            // Create a custom food item with a unique identifier for this specific animal
            org.bukkit.inventory.ItemStack food = new org.bukkit.inventory.ItemStack(foodItem);
            org.bukkit.inventory.meta.ItemMeta meta = food.getItemMeta();
            meta.setDisplayName("§6Animal Lure");
            meta.setLore(java.util.Arrays.asList("§7Makes your animal follow you", "§7Only you can see this", "§7ID: " + player.getName()));
            
            // Add custom data to make this food unique to this player
            meta.setLocalizedName("animal_lure_" + player.getName());
            food.setItemMeta(meta);
            
            // Give the food to the player's main hand
            player.getInventory().setItemInMainHand(food);
            
            Bukkit.getLogger().info("[ImpersonationManager] Gave " + foodItem.name() + " to " + player.getName() + " for " + entityType.name());
        }
    }
} 