package me.yodeling_goat.afterlifeplugin.stats.listeners;

import me.yodeling_goat.afterlifeplugin.stats.StatsManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.block.BlockBreakEvent;
import java.util.HashMap;
import java.util.UUID;

public class PlayerStatsListener implements Listener {
    
    private final HashMap<UUID, Long> lastClickTime = new HashMap<>();
    private static final long CLICK_COOLDOWN = 500; // 500ms cooldown
    
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent event) {
        // Check if stats system is enabled
        if (!org.bukkit.Bukkit.getPluginManager().getPlugin("AfterLifePlugin").getConfig().getBoolean("stats.enabled", true)) {
            return;
        }
        
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }
        
        Player clickedPlayer = (Player) event.getRightClicked();
        Player player = event.getPlayer();
        
        // Check cooldown to prevent double-triggering
        long currentTime = System.currentTimeMillis();
        Long lastClick = lastClickTime.get(player.getUniqueId());
        if (lastClick != null && currentTime - lastClick < CLICK_COOLDOWN) {
            return;
        }
        lastClickTime.put(player.getUniqueId(), currentTime);
        
        // Don't show stats for yourself
        if (clickedPlayer.equals(player)) {
            return;
        }
        
        // Show stats board
        showStatsBoard(player, clickedPlayer);
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        StatsManager.getInstance().addDeath(player);
        
        // Check if player was killed by another player
        if (player.getKiller() != null) {
            StatsManager.getInstance().addKill(player.getKiller());
        }
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Check if an animal was killed by a player
        if (event.getEntity() instanceof Animals && event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            StatsManager.getInstance().addAnimalKill(killer);
        }

        // Check if a hostile mob was killed by a player
        if (event.getEntity() instanceof Monster && event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            StatsManager.getInstance().addHostileMobKill(killer);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Check if stats system is enabled
        if (!org.bukkit.Bukkit.getPluginManager().getPlugin("AfterLifePlugin").getConfig().getBoolean("stats.enabled", true)) {
            return;
        }

        Player player = event.getPlayer();
        StatsManager.getInstance().addBlockMined(player);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            StatsManager.getInstance().addItemCrafted(player);
        }
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        int xpGained = event.getAmount();
        if (xpGained > 0) {
            StatsManager.getInstance().addXpCollected(event.getPlayer(), xpGained);
        }
    }

    private String applyLimeGreenPattern(String text) {
        StringBuilder result = new StringBuilder();
        boolean useYellow = true;

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                if (useYellow) {
                    result.append(ChatColor.YELLOW).append(c);
                } else {
                    result.append(ChatColor.GREEN).append(c);
                }
                useYellow = !useYellow; // Alternate colors
            } else {
                result.append(c); // Keep non-letter characters as-is
            }
        }

        return result.toString();
    }

    private void showStatsBoard(Player viewer, Player target) {
        StatsManager.PlayerStats stats = StatsManager.getInstance().getPlayerStats(target);
        
        // Create a clean, modern stats board
        viewer.sendMessage("");
        viewer.sendMessage(ChatColor.DARK_GRAY + "┌─ " + ChatColor.GOLD + target.getName() + "'s Statistics" + ChatColor.DARK_GRAY + " ─┐");
        viewer.sendMessage(ChatColor.DARK_GRAY + "│");
        viewer.sendMessage(ChatColor.DARK_GRAY + "│ " + ChatColor.GREEN + "● Kills: " + ChatColor.WHITE + stats.getKills());
        viewer.sendMessage(ChatColor.DARK_GRAY + "│ " + ChatColor.RED + "● Deaths: " + ChatColor.WHITE + stats.getDeaths());
        viewer.sendMessage(ChatColor.DARK_GRAY + "│ " + ChatColor.AQUA + "● K/D Ratio: " + ChatColor.WHITE + String.format("%.2f", stats.getKDRatio()));
        viewer.sendMessage(ChatColor.DARK_GRAY + "│ " + ChatColor.LIGHT_PURPLE + "● Animals Killed: " + ChatColor.WHITE + stats.getAnimalsKilled());
        viewer.sendMessage(ChatColor.DARK_GRAY + "│ " + ChatColor.DARK_RED + "● Hostile Mobs Killed: " + ChatColor.WHITE + stats.getHostileMobsKilled());
        viewer.sendMessage(ChatColor.DARK_GRAY + "│ " + ChatColor.GOLD + "● Blocks Mined: " + ChatColor.WHITE + stats.getBlocksMined());
        viewer.sendMessage(ChatColor.DARK_GRAY + "│ " + ChatColor.GOLD + "● Items Crafted: " + ChatColor.WHITE + stats.getItemsCrafted());
        viewer.sendMessage(ChatColor.DARK_GRAY + "│ " + ChatColor.YELLOW + "● " + applyLimeGreenPattern("XP Collected: ") + ChatColor.WHITE + stats.getXpCollected());
        viewer.sendMessage(ChatColor.DARK_GRAY + "│");
        viewer.sendMessage(ChatColor.DARK_GRAY + "└─────────────────────────────────┘");
        viewer.sendMessage("");
        
        // Play a subtle sound effect
        viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.5f);
    }
} 