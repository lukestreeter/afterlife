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
        // Make the player invisible
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        
        // Add night vision so they can see clearly
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        
        // Execute the summon command directly to create the animal disguise
        String command = buildAnimalSummonCommand(target.getType(), player.getName(), player.getLocation());
        Bukkit.getLogger().info("[ImpersonationManager] Executing command: " + command);
        org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), command);
        
        // Store the player name in metadata for tracking
        player.setMetadata("animal_disguise", new org.bukkit.metadata.FixedMetadataValue(
            org.bukkit.Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), player.getName()));
        
        // Add the player to a special team for visual effects
        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        org.bukkit.scoreboard.Team impersonatingTeam = scoreboard.getTeam("impersonating");
        if (impersonatingTeam == null) {
            impersonatingTeam = scoreboard.registerNewTeam("impersonating");
            impersonatingTeam.setColor(org.bukkit.ChatColor.GOLD);
        }
        impersonatingTeam.addPlayer(player);
    }
    
    private static String buildAnimalSummonCommand(org.bukkit.entity.EntityType entityType, String playerName, org.bukkit.Location location) {
        // Create a summon command with specific properties for the animal disguise
        String baseCommand = "summon " + entityType.name().toLowerCase() + " " + location.getX() + " " + location.getY() + " " + location.getZ();
        
        // Add custom properties based on entity type
        switch (entityType) {
            case CHICKEN:
                return baseCommand + " {CustomName:\"\\u00a76Chicken Disguise\",CustomNameVisible:1b,Invulnerable:1b,NoAI:1b,Silent:1b,Glowing:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case COW:
                return baseCommand + " {CustomName:\"\\u00a76Cow Disguise\",CustomNameVisible:1b,Invulnerable:1b,NoAI:1b,Silent:1b,Glowing:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case PIG:
                return baseCommand + " {CustomName:\"\\u00a76Pig Disguise\",CustomNameVisible:1b,Invulnerable:1b,NoAI:1b,Silent:1b,Glowing:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case SHEEP:
                return baseCommand + " {CustomName:\"\\u00a76Sheep Disguise\",CustomNameVisible:1b,Invulnerable:1b,NoAI:1b,Silent:1b,Glowing:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case HORSE:
                return baseCommand + " {CustomName:\"\\u00a76Horse Disguise\",CustomNameVisible:1b,Invulnerable:1b,NoAI:1b,Silent:1b,Glowing:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case RABBIT:
                return baseCommand + " {CustomName:\"\\u00a76Rabbit Disguise\",CustomNameVisible:1b,Invulnerable:1b,NoAI:1b,Silent:1b,Glowing:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case FOX:
                return baseCommand + " {CustomName:\"\\u00a76Fox Disguise\",CustomNameVisible:1b,Invulnerable:1b,NoAI:1b,Silent:1b,Glowing:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case WOLF:
                return baseCommand + " {CustomName:\"\\u00a76Wolf Disguise\",CustomNameVisible:1b,Invulnerable:1b,NoAI:1b,Silent:1b,Glowing:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case CAT:
                return baseCommand + " {CustomName:\"\\u00a76Cat Disguise\",CustomNameVisible:1b,Invulnerable:1b,NoAI:1b,Silent:1b,Glowing:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            case PARROT:
                return baseCommand + " {CustomName:\"\\u00a76Parrot Disguise\",CustomNameVisible:1b,Invulnerable:1b,NoAI:1b,Silent:1b,Glowing:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
            default:
                return baseCommand + " {CustomName:\"\\u00a76" + entityType.name().toLowerCase() + " Disguise\",CustomNameVisible:1b,Invulnerable:1b,NoAI:1b,Silent:1b,Glowing:1b,Tags:[\"animal_disguise\",\"" + playerName + "\"]}";
        }
    }

    private static void removeImpersonationEffects(Player player, ImpersonationData data) {
        // Remove invisibility and other potion effects
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        
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
        
        // Remove from team
        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        org.bukkit.scoreboard.Team impersonatingTeam = scoreboard.getTeam("impersonating");
        if (impersonatingTeam != null) {
            impersonatingTeam.removePlayer(player);
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
            
            // Remove from team
            org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            org.bukkit.scoreboard.Team impersonatingTeam = scoreboard.getTeam("impersonating");
            if (impersonatingTeam != null) {
                impersonatingTeam.removePlayer(player);
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
} 