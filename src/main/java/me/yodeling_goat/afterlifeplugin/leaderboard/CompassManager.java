package me.yodeling_goat.afterlifeplugin.leaderboard;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import me.yodeling_goat.afterlifeplugin.AfterLifePlugin;

import java.util.Arrays;
import java.util.List;
import me.yodeling_goat.afterlifeplugin.leaderboard.CompassDataManager;

public class CompassManager {
    private static final NamespacedKey LEADERBOARD_COMPASS_KEY = new NamespacedKey(AfterLifePlugin.getInstance(), "leaderboard_compass");
    
    public static void giveLeaderboardCompass(Player player) {
        // Check if player has already received a compass
        if (CompassDataManager.getInstance().hasPlayerReceivedCompass(player)) {
            return;
        }
        
        ItemStack compass = createLeaderboardCompass();
        
        // Check if player already has a leaderboard compass in inventory
        if (player.getInventory().containsAtLeast(compass, 1)) {
            return;
        }
        
        // Give the compass to the player
        player.getInventory().addItem(compass);
        CompassDataManager.getInstance().markPlayerAsReceivedCompass(player);
        player.sendMessage("§aYou received a Leaderboard Compass! Right-click to view stats.");
    }
    
    public static ItemStack createLeaderboardCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6§lLeaderboard Compass");
            meta.setLore(Arrays.asList(
                "§7Right-click to view",
                "§7server statistics and",
                "§7leaderboards!"
            ));
            
            // Add custom data to identify this as a leaderboard compass
            meta.getPersistentDataContainer().set(LEADERBOARD_COMPASS_KEY, PersistentDataType.BYTE, (byte) 1);
            
            compass.setItemMeta(meta);
        }
        
        return compass;
    }
    
    public static boolean isLeaderboardCompass(ItemStack item) {
        if (item == null || item.getType() != Material.COMPASS) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        return meta.getPersistentDataContainer().has(LEADERBOARD_COMPASS_KEY, PersistentDataType.BYTE);
    }
} 