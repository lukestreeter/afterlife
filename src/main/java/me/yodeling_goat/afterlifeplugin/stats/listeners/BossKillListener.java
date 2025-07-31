package me.yodeling_goat.afterlifeplugin.stats.listeners;

import me.yodeling_goat.afterlifeplugin.stats.StatsManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class BossKillListener implements Listener {
    
    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        // Check if the killer is a player
        if (event.getEntity().getKiller() == null) {
            return;
        }
        
        Player killer = event.getEntity().getKiller();
        EntityType entityType = event.getEntity().getType();
        
        // Check for boss mobs and track kills
        String entityName = entityType.name();
        
        if (entityName.equals("WARDEN")) {
            StatsManager.getInstance().addWardenKill(killer);
            broadcastBossKill(killer, "WARDEN", ChatColor.DARK_BLUE, ChatColor.BLUE);
        } else if (entityName.equals("ENDER_DRAGON")) {
            StatsManager.getInstance().addEnderDragonKill(killer);
            broadcastBossKill(killer, "ENDER DRAGON", ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE);
        } else if (entityName.equals("WITHER_SKELETON")) {
            // Check if it's actually a Wither boss, not just a wither skeleton
            if (event.getEntity().getCustomName() != null && 
                event.getEntity().getCustomName().contains("Wither")) {
                StatsManager.getInstance().addWitherKill(killer);
                broadcastBossKill(killer, "WITHER", ChatColor.BLACK, ChatColor.RED);
            }
        }
    }
    
    private void broadcastBossKill(Player killer, String bossName, ChatColor primaryColor, ChatColor secondaryColor) {
        // Create a scary and cool boss kill message
        String message = primaryColor + "☠️ " + secondaryColor + "BOSS SLAIN! " + primaryColor + "☠️";
        String killerMessage = secondaryColor + killer.getName() + primaryColor + " has defeated the " + secondaryColor + bossName + primaryColor + "!";
        String statsMessage = secondaryColor + "Check your stats with " + primaryColor + "/stats" + secondaryColor + " to see your boss kill count!";
        
        // Broadcast to all players
        killer.getServer().broadcastMessage("");
        killer.getServer().broadcastMessage(message);
        killer.getServer().broadcastMessage(killerMessage);
        killer.getServer().broadcastMessage(statsMessage);
        killer.getServer().broadcastMessage("");
        
        // Play scary sound effects for all players
        killer.getServer().getOnlinePlayers().forEach(player -> {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_WITHER_DEATH, 1.0f, 0.5f);
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDER_DRAGON_DEATH, 0.8f, 1.0f);
        });
        
        // Send special message to the killer
        killer.sendMessage(primaryColor + "🎯 " + secondaryColor + "You have slain " + bossName + "!");
        killer.sendMessage(primaryColor + "💀 " + secondaryColor + "Your boss kill count has been updated!");
    }
} 