package me.yodeling_goat.afterlifeplugin.afterlife.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.HashMap;
import java.util.UUID;

public class SpectatorHotbar {
    private static final HashMap<UUID, Entity> spectatingPlayers = new HashMap<>();
    private static final HashMap<UUID, ViewMode> playerViewModes = new HashMap<>();
    
    public enum ViewMode {
        FIRST_PERSON,
        THIRD_PERSON,
        FREE_CAMERA
    }
    
    public static void startSpectating(Player player, LivingEntity target) {
        spectatingPlayers.put(player.getUniqueId(), target);
        playerViewModes.put(player.getUniqueId(), ViewMode.FIRST_PERSON);
        
        if (player.getGameMode() != GameMode.SPECTATOR) {
            player.setGameMode(GameMode.SPECTATOR);
        }
        player.setSpectatorTarget(target);
        
        showSpectatorUI(player);
        player.sendMessage(ChatColor.GREEN + "You are now spectating " + 
                          ChatColor.YELLOW + target.getName() + 
                          ChatColor.GREEN + "! Use the action bar to navigate.");
    }
    
    public static void stopSpectating(Player player) {
        spectatingPlayers.remove(player.getUniqueId());
        playerViewModes.remove(player.getUniqueId());
        
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.setSpectatorTarget(null);
        }
        player.setGameMode(GameMode.SURVIVAL);
        
        player.sendMessage(ChatColor.AQUA + "You have stopped spectating!");
    }
    
    public static void cycleViewMode(Player player) {
        UUID playerId = player.getUniqueId();
        if (!playerViewModes.containsKey(playerId)) return;
        
        ViewMode current = playerViewModes.get(playerId);
        ViewMode next;
        
        switch (current) {
            case FIRST_PERSON:
                next = ViewMode.THIRD_PERSON;
                break;
            case THIRD_PERSON:
                next = ViewMode.FREE_CAMERA;
                break;
            case FREE_CAMERA:
                next = ViewMode.FIRST_PERSON;
                break;
            default:
                next = ViewMode.FIRST_PERSON;
        }
        
        playerViewModes.put(playerId, next);
        showSpectatorUI(player);
        
        String modeName = next.name().replace("_", " ").toLowerCase();
        player.sendMessage(ChatColor.YELLOW + "Switched to " + modeName + " view!");
    }
    
    public static void showSpectatorUI(Player player) {
        try {
            ViewMode mode = playerViewModes.getOrDefault(player.getUniqueId(), ViewMode.FIRST_PERSON);
            Entity target = spectatingPlayers.get(player.getUniqueId());
            
            String targetName = target != null ? target.getName() : "Unknown";
            String modeName = mode.name().replace("_", " ").toLowerCase();
            
            // Hypixel-style action bar with multiple options
            String hotbar = ChatColor.GRAY + "[" + ChatColor.GOLD + "1" + ChatColor.GRAY + "] " + 
                           ChatColor.WHITE + "Exit " + 
                           ChatColor.GRAY + "| " + 
                           ChatColor.GOLD + "2" + ChatColor.GRAY + "] " + 
                           ChatColor.WHITE + "View Mode: " + ChatColor.YELLOW + modeName + 
                           ChatColor.GRAY + " | " + 
                           ChatColor.GOLD + "3" + ChatColor.GRAY + "] " + 
                           ChatColor.WHITE + "Target: " + ChatColor.AQUA + targetName;
            
            TextComponent component = new TextComponent(hotbar);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            
        } catch (Exception e) {
            // Fallback to simple message
            player.sendMessage(ChatColor.GRAY + "[1] Exit | [2] Change View | [3] Info");
        }
    }
    
    public static boolean isSpectating(Player player) {
        return spectatingPlayers.containsKey(player.getUniqueId());
    }
    
    public static Entity getSpectatingTarget(Player player) {
        return spectatingPlayers.get(player.getUniqueId());
    }
    
    public static void handleHotbarClick(Player player, int slot) {
        switch (slot) {
            case 1: // Exit
                stopSpectating(player);
                break;
            case 2: // Change view mode
                cycleViewMode(player);
                break;
            case 3: // Show info
                showTargetInfo(player);
                break;
        }
    }
    
    private static void showTargetInfo(Player player) {
        Entity target = getSpectatingTarget(player);
        if (target != null) {
            player.sendMessage(ChatColor.GOLD + "=== Target Info ===");
            player.sendMessage(ChatColor.YELLOW + "Name: " + ChatColor.WHITE + target.getName());
            player.sendMessage(ChatColor.YELLOW + "Type: " + ChatColor.WHITE + target.getType().name());
            player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + 
                             target.getLocation().getBlockX() + ", " + 
                             target.getLocation().getBlockY() + ", " + 
                             target.getLocation().getBlockZ());
            
            if (target instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) target;
                player.sendMessage(ChatColor.YELLOW + "Health: " + ChatColor.GREEN + 
                                 living.getHealth() + "/" + living.getMaxHealth());
            }
        }
    }
} 