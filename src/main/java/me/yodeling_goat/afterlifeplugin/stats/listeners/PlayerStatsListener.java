package me.yodeling_goat.afterlifeplugin.stats.listeners;

import me.yodeling_goat.afterlifeplugin.stats.StatsManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Animals;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class PlayerStatsListener implements Listener {
    
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
    }
    
    private void showStatsBoard(Player viewer, Player target) {
        StatsManager.PlayerStats stats = StatsManager.getInstance().getPlayerStats(target);
        
        // Create a beautiful stats board
        viewer.sendMessage(ChatColor.GOLD + "╔══════════════════════════════════════╗");
        viewer.sendMessage(ChatColor.GOLD + "║" + ChatColor.YELLOW + "           " + target.getName() + "'s Stats" + ChatColor.GOLD + "           ║");
        viewer.sendMessage(ChatColor.GOLD + "╠══════════════════════════════════════╣");
        viewer.sendMessage(ChatColor.GOLD + "║" + ChatColor.GREEN + " Kills: " + ChatColor.WHITE + stats.getKills() + ChatColor.GOLD + "                              ║");
        viewer.sendMessage(ChatColor.GOLD + "║" + ChatColor.RED + " Deaths: " + ChatColor.WHITE + stats.getDeaths() + ChatColor.GOLD + "                            ║");
        viewer.sendMessage(ChatColor.GOLD + "║" + ChatColor.AQUA + " K/D Ratio: " + ChatColor.WHITE + String.format("%.2f", stats.getKDRatio()) + ChatColor.GOLD + "                        ║");
        viewer.sendMessage(ChatColor.GOLD + "║" + ChatColor.LIGHT_PURPLE + " Animals Killed: " + ChatColor.WHITE + stats.getAnimalsKilled() + ChatColor.GOLD + "                    ║");
        viewer.sendMessage(ChatColor.GOLD + "╚══════════════════════════════════════╝");
        
        // Play a sound effect
        viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
    }
} 