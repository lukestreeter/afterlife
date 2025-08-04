package me.yodeling_goat.afterlifeplugin.leaderboard.listeners;

import me.yodeling_goat.afterlifeplugin.leaderboard.CompassManager;
import me.yodeling_goat.afterlifeplugin.leaderboard.LeaderboardManager;
import me.yodeling_goat.afterlifeplugin.leaderboard.LeaderboardMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LeaderboardListener implements Listener {
    
    @EventHandler
    public void onCompassRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // Check if player right-clicked with a leaderboard compass
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (item != null && CompassManager.isLeaderboardCompass(item)) {
                event.setCancelled(true);
                LeaderboardMenu.openLeaderboardMenu(player);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clickedItem = event.getCurrentItem();
        
        // Handle main menu clicks
        if (title.equals("§8§lMAIN MENU")) {
            event.setCancelled(true);
            
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }
            
            // Handle stat category clicks
            if (clickedItem.getType() == Material.DIAMOND_SWORD) {
                LeaderboardMenu.openStatLeaderboard(player, LeaderboardManager.StatType.KILLS);
            } else if (clickedItem.getType() == Material.SKELETON_SKULL) {
                LeaderboardMenu.openStatLeaderboard(player, LeaderboardManager.StatType.DEATHS);
            } else if (clickedItem.getType() == Material.BOOK) {
                LeaderboardMenu.openStatLeaderboard(player, LeaderboardManager.StatType.KDR);
            } else if (clickedItem.getType() == Material.COW_SPAWN_EGG) {
                LeaderboardMenu.openStatLeaderboard(player, LeaderboardManager.StatType.ANIMALS_KILLED);
            } else if (clickedItem.getType() == Material.ZOMBIE_HEAD) {
                LeaderboardMenu.openStatLeaderboard(player, LeaderboardManager.StatType.HOSTILE_MOBS_KILLED);
            } else if (clickedItem.getType() == Material.DIAMOND_PICKAXE) {
                LeaderboardMenu.openStatLeaderboard(player, LeaderboardManager.StatType.BLOCKS_MINED);
            } else if (clickedItem.getType() == Material.OBSIDIAN) {
                LeaderboardMenu.openStatLeaderboard(player, LeaderboardManager.StatType.WARDEN_KILLED);
            } else if (clickedItem.getType() == Material.DRAGON_HEAD) {
                LeaderboardMenu.openStatLeaderboard(player, LeaderboardManager.StatType.ENDER_DRAGON_KILLED);
            } else if (clickedItem.getType() == Material.WITHER_SKELETON_SKULL) {
                LeaderboardMenu.openStatLeaderboard(player, LeaderboardManager.StatType.WITHER_KILLED);
            } else if (clickedItem.getType() == Material.CRAFTING_TABLE) {
                LeaderboardMenu.openStatLeaderboard(player, LeaderboardManager.StatType.ITEMS_CRAFTED);
            } else if (clickedItem.getType() == Material.EXPERIENCE_BOTTLE) {
                LeaderboardMenu.openStatLeaderboard(player, LeaderboardManager.StatType.XP_COLLECTED);
            } else if (clickedItem.getType() == Material.BARRIER) {
                player.closeInventory();
            }
        }
        // Handle stat leaderboard menu clicks
        else if (title.contains("Leaderboard")) {
            event.setCancelled(true);
            
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }
            
            // Handle back button
            if (clickedItem.getType() == Material.ARROW) {
                LeaderboardMenu.openLeaderboardMenu(player);
            }
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        String title = event.getView().getTitle();
        
        // Prevent dragging items in leaderboard menus
        if (title.equals("§8§lMAIN MENU") || title.contains("Leaderboard")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        // Prevent moving items from leaderboard menus
        String sourceTitle = "";
        String destinationTitle = "";
        
        if (!event.getSource().getViewers().isEmpty()) {
            sourceTitle = event.getSource().getViewers().get(0).getOpenInventory().getTitle();
        }
        if (!event.getDestination().getViewers().isEmpty()) {
            destinationTitle = event.getDestination().getViewers().get(0).getOpenInventory().getTitle();
        }
        
        if (sourceTitle.equals("§8§lMAIN MENU") || sourceTitle.contains("Leaderboard") ||
            destinationTitle.equals("§8§lMAIN MENU") || destinationTitle.contains("Leaderboard")) {
            event.setCancelled(true);
        }
    }
} 