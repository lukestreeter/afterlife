package me.yodeling_goat.afterlifeplugin.afterlife;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;
import me.yodeling_goat.afterlifeplugin.AfterLifePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CountdownManager {
    private static final Map<UUID, Integer> activeCountdowns = new HashMap<>();
    private static final int UPDATE_INTERVAL = 20; // Update every second (20 ticks)
    
    public static void startDeathCountdown(Player player) {
        UUID playerId = player.getUniqueId();
        
        // Cancel any existing countdown for this player
        stopCountdown(player);
        
        // Initialize countdown
        activeCountdowns.put(playerId, 10);
        
        // Start the animated countdown
        new BukkitRunnable() {
            private int countdown = 10; // Start from 10
            private int animationTick = 0;
            private boolean displayed10 = false;
            private boolean displayed5 = false;
            private boolean displayed3 = false;
            private boolean displayed2 = false;
            private boolean displayed1 = false;
            
            @Override
            public void run() {
                // Check if player is still online and in afterlife
                if (!player.isOnline() || !AfterlifeManager.isInAfterlife(player)) {
                    stopCountdown(player);
                    this.cancel();
                    return;
                }
                
                // Display 10 immediately on first tick
                if (animationTick == 0 && !displayed10) {
                    displayCountdown(player, countdown, animationTick);
                    displayed10 = true;
                }
                
                // Update countdown every second
                if (animationTick % UPDATE_INTERVAL == 0 && animationTick > 0) {
                    countdown--;
                    activeCountdowns.put(playerId, countdown);
                }
                
                // Display specific numbers only once: 10, 5, 3, 2, 1
                if (countdown == 5 && !displayed5) {
                    displayCountdown(player, countdown, animationTick);
                    displayed5 = true;
                } else if (countdown == 3 && !displayed3) {
                    displayCountdown(player, countdown, animationTick);
                    displayed3 = true;
                } else if (countdown == 2 && !displayed2) {
                    displayCountdown(player, countdown, animationTick);
                    displayed2 = true;
                } else if (countdown == 1 && !displayed1) {
                    displayCountdown(player, countdown, animationTick);
                    displayed1 = true;
                }
                
                animationTick++;
                
                // End countdown when it reaches 0
                if (countdown <= 0) {
                    stopCountdown(player);
                    this.cancel();
                    
                    // Send completion message
                    player.sendTitle(
                        ChatColor.GREEN + "§lJUDGEMENT COMPLETE",
                        ChatColor.GREEN + "Your soul is now bound to the afterlife!",
                        10, 60, 20
                    );
                    
                    // Send completion message in chat
                    player.sendMessage(ChatColor.GREEN + "§lJudgement complete! You remain in the afterlife.");
                }
            }
        }.runTaskTimer(AfterLifePlugin.getInstance(), 0L, 1L);
    }
    
    private static void displayCountdown(Player player, int countdown, int animationTick) {
        // Choose color based on countdown number
        ChatColor color;
        if (countdown >= 4) {
            // 10, 9, 8, 7, 6, 5, 4 in red
            color = ChatColor.RED;
        } else if (countdown == 3) {
            // 3 in red
            color = ChatColor.RED;
        } else if (countdown == 2) {
            // 2 in dark orange
            color = ChatColor.GOLD;
        } else if (countdown == 1) {
            // 1 in lime green
            color = ChatColor.GREEN;
        } else {
            // 0 in green (completion)
            color = ChatColor.GREEN;
        }
        
        // Create the number for title (just the number)
        String titleNumber = color + "§l" + countdown;
        
        // Add pulsing animation effect for numbers 4 and above
        if (countdown >= 4 && animationTick % 20 < 10) {
            titleNumber = ChatColor.DARK_RED + "§l" + countdown;
        }
        
        // Create the message for chat
        String chatMessage = color + "§l" + countdown + ChatColor.YELLOW + " seconds left until judgement complete";
        
        // Send number as title
        player.sendTitle(titleNumber, "", 0, 21, 0);
        
        // Send message in chat
        player.sendMessage(chatMessage);
    }
    
    public static void stopCountdown(Player player) {
        activeCountdowns.remove(player.getUniqueId());
        
        // Clear any existing titles
        player.sendTitle("", "", 0, 0, 0);
    }
    
    public static int getRemainingTime(Player player) {
        return activeCountdowns.getOrDefault(player.getUniqueId(), 0);
    }
    
    public static boolean hasActiveCountdown(Player player) {
        return activeCountdowns.containsKey(player.getUniqueId());
    }
    
    public static void stopAllCountdowns() {
        activeCountdowns.clear();
    }
} 