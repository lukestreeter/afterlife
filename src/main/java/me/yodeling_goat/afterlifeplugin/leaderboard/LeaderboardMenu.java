package me.yodeling_goat.afterlifeplugin.leaderboard;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import me.yodeling_goat.afterlifeplugin.stats.StatsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaderboardMenu {
    private static final String MENU_TITLE = "§8§lMAIN MENU";
    private static final int MENU_SIZE = 54; // 6 rows
    
    public static void openLeaderboardMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, MENU_SIZE, MENU_TITLE);
        
        // Fill with black stained glass panes
        ItemStack background = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", new ArrayList<>());
        for (int i = 0; i < MENU_SIZE; i++) {
            menu.setItem(i, background);
        }
        
        // Add stat category items
        menu.setItem(10, createStatItem(Material.DIAMOND_SWORD, "§a§lKills", "§7Player vs Player kills", LeaderboardManager.StatType.KILLS));
        menu.setItem(11, createStatItem(Material.SKELETON_SKULL, "§c§lDeaths", "§7Player deaths", LeaderboardManager.StatType.DEATHS));
        menu.setItem(12, createStatItem(Material.BOOK, "§e§lK/D Ratio", "§7Kill to Death ratio", LeaderboardManager.StatType.KDR));
        menu.setItem(13, createStatItem(Material.COW_SPAWN_EGG, "§6§lAnimals Killed", "§7Peaceful mob kills", LeaderboardManager.StatType.ANIMALS_KILLED));
        menu.setItem(14, createStatItem(Material.ZOMBIE_HEAD, "§4§lHostile Mobs", "§7Hostile mob kills", LeaderboardManager.StatType.HOSTILE_MOBS_KILLED));
        menu.setItem(15, createStatItem(Material.DIAMOND_PICKAXE, "§7§lBlocks Mined", "§7Total blocks mined", LeaderboardManager.StatType.BLOCKS_MINED));
        menu.setItem(16, createStatItem(Material.OBSIDIAN, "§9§lWarden Kills", "§7Warden boss kills", LeaderboardManager.StatType.WARDEN_KILLED));
        
        // Second row of stats
        menu.setItem(19, createStatItem(Material.DRAGON_HEAD, "§5§lEnder Dragon", "§7Ender Dragon kills", LeaderboardManager.StatType.ENDER_DRAGON_KILLED));
        menu.setItem(20, createStatItem(Material.WITHER_SKELETON_SKULL, "§8§lWither Kills", "§7Wither boss kills", LeaderboardManager.StatType.WITHER_KILLED));
        menu.setItem(21, createStatItem(Material.CRAFTING_TABLE, "§a§lItems Crafted", "§7Total items crafted", LeaderboardManager.StatType.ITEMS_CRAFTED));
        menu.setItem(22, createStatItem(Material.EXPERIENCE_BOTTLE, "§e§lXP Collected", "§7Total XP collected", LeaderboardManager.StatType.XP_COLLECTED));
        
        // Add return button
        menu.setItem(49, createItem(Material.BARRIER, "§c§lReturn to the menu", Arrays.asList("§7Click to close this menu")));
        
        player.openInventory(menu);
    }
    
    public static void openStatLeaderboard(Player player, LeaderboardManager.StatType statType) {
        String title = "§8§l" + statType.getDisplayName() + " Leaderboard";
        Inventory menu = Bukkit.createInventory(null, 54, title);
        
        // Fill with black stained glass panes
        ItemStack background = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", new ArrayList<>());
        for (int i = 0; i < 54; i++) {
            menu.setItem(i, background);
        }
        
        // Get top 10 players for this stat
        List<LeaderboardManager.LeaderboardEntry> topPlayers = LeaderboardManager.getInstance().getTopPlayers(statType, 10);
        
        // Display top players in slots 10-43 (4 rows, 9 columns each)
        int slot = 10;
        for (int i = 0; i < topPlayers.size() && slot < 43; i++) {
            LeaderboardManager.LeaderboardEntry entry = topPlayers.get(i);
            menu.setItem(slot, createPlayerHead(entry, i + 1, statType));
            slot++;
            
            // Skip to next row every 9 slots
            if (slot % 9 == 0) {
                slot += 1; // Move to next row
            }
        }
        
        // Add back button
        menu.setItem(49, createItem(Material.ARROW, "§e§lBack to Main Menu", Arrays.asList("§7Return to the main lobby")));
        
        player.openInventory(menu);
    }
    
    private static ItemStack createStatItem(Material material, String name, String description, LeaderboardManager.StatType statType) {
        List<String> lore = new ArrayList<>();
        lore.add(description);
        lore.add("");
        lore.add("§eClick to view leaderboard!");
        
        return createItem(material, name, lore);
    }
    
    private static ItemStack createPlayerHead(LeaderboardManager.LeaderboardEntry entry, int rank, LeaderboardManager.StatType statType) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        if (meta != null) {
            boolean headSet = false;
            
            // Try to set the player's head texture
            try {
                // First try to get the player if they're online
                Player onlinePlayer = Bukkit.getPlayer(entry.getPlayerUuid());
                if (onlinePlayer != null) {
                    meta.setOwningPlayer(onlinePlayer);
                    headSet = true;
                } else {
                    // If player is offline, try to set by name (this will work for players who have joined before)
                    meta.setOwner(entry.getPlayerName());
                    headSet = true;
                }
            } catch (Exception e) {
                // If setting the head fails, we'll use a fallback
                headSet = false;
            }
            
            meta.setDisplayName(getRankColor(rank) + "§l#" + rank + " " + entry.getPlayerName());
            
            List<String> lore = new ArrayList<>();
            lore.add("§7" + statType.getDisplayName() + ": §e" + formatValue(entry.getValue(), statType));
            lore.add("");
            lore.add("§8Player: §f" + entry.getPlayerName());
            lore.add("§8Rank: §f#" + rank);
            
            // Add additional stats if available
            try {
                StatsManager.PlayerStats playerStats = StatsManager.getInstance().getPlayerStats(entry.getPlayerUuid());
                if (playerStats != null) {
                    lore.add("");
                    lore.add("§7Other Stats:");
                    lore.add("§8Kills: §f" + playerStats.getKills());
                    lore.add("§8Deaths: §f" + playerStats.getDeaths());
                    if (playerStats.getKDRatio() > 0) {
                        lore.add("§8K/D Ratio: §f" + String.format("%.2f", playerStats.getKDRatio()));
                    }
                }
            } catch (Exception e) {
                // Ignore if we can't get additional stats
            }
            
            lore.add("");
            
            // Add rank-specific messages and visual indicators
            if (rank == 1) {
                lore.add("§6§l🥇 First Place!");
                lore.add("§6§lCROWNED CHAMPION!");
                lore.add("§6§l✨ LEGENDARY PLAYER ✨");
            } else if (rank == 2) {
                lore.add("§7§l🥈 Second Place!");
                lore.add("§7§lSILVER MEDALIST!");
                lore.add("§7§l⭐ ELITE PLAYER ⭐");
            } else if (rank == 3) {
                lore.add("§c§l🥉 Third Place!");
                lore.add("§c§lBRONZE MEDALIST!");
                lore.add("§c§l🌟 SKILLED PLAYER 🌟");
            } else if (rank <= 10) {
                lore.add("§a§lTOP 10 PLAYER!");
                lore.add("§a§l🏆 ACHIEVER 🏆");
            }
            
            meta.setLore(lore);
            head.setItemMeta(meta);
            
            // If we couldn't set the player head, return a fallback
            if (!headSet) {
                return createFallbackHead(entry.getPlayerName(), rank);
            }
        }
        
        return head;
    }
    
    private static ItemStack createFallbackHead(String playerName, int rank) {
        // Create a fallback head with a different material if player head fails
        Material fallbackMaterial = Material.SKELETON_SKULL;
        switch (rank) {
            case 1: fallbackMaterial = Material.DIAMOND_BLOCK; break;
            case 2: fallbackMaterial = Material.IRON_BLOCK; break;
            case 3: fallbackMaterial = Material.GOLD_BLOCK; break;
            default: fallbackMaterial = Material.STONE; break;
        }
        
        ItemStack fallback = new ItemStack(fallbackMaterial);
        ItemMeta meta = fallback.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(getRankColor(rank) + "§l#" + rank + " " + playerName);
            List<String> lore = new ArrayList<>();
            lore.add("§7Player head unavailable");
            lore.add("§8Player: §f" + playerName);
            meta.setLore(lore);
            fallback.setItemMeta(meta);
        }
        
        return fallback;
    }
    
    private static String getRankColor(int rank) {
        switch (rank) {
            case 1: return "§6"; // Gold
            case 2: return "§7"; // Silver
            case 3: return "§c"; // Bronze
            default: return "§f"; // White
        }
    }
    
    private static String formatValue(int value, LeaderboardManager.StatType statType) {
        if (statType == LeaderboardManager.StatType.KDR) {
            return String.format("%.2f", value / 100.0);
        } else if (statType == LeaderboardManager.StatType.XP_COLLECTED) {
            return String.format("%,d", value);
        } else {
            return String.format("%,d", value);
        }
    }
    
    private static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
} 