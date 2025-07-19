package me.yodeling_goat.afterlifeplugin.afterlife;

import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.yodeling_goat.afterlifeplugin.afterlife.events.PlayerEnterAfterlifeEvent;
import org.bukkit.Location;

public class AfterlifeManager {
    private static final Set<Player> afterlifePlayers = new HashSet<>();

    public static void sendToAfterlife(Player player) {
        afterlifePlayers.add(player);
        Bukkit.getPluginManager().callEvent(new PlayerEnterAfterlifeEvent(player));
    }

    public static void removeFromAfterlife(Player player) {
        afterlifePlayers.remove(player);
        removeAfterlifeEffects(player);
    }

    public static boolean isInAfterlife(Player player) {
        return afterlifePlayers.contains(player);
    }

    public static Set<Player> getAfterlifePlayers() {
        return new HashSet<>(afterlifePlayers);
    }

    public static void initializeAfterlifeState(Player player) {
        if (isInAfterlife(player)) {
            applyPermanentAfterlifeEffects(player);
        }
    }

    public static void applyPermanentAfterlifeEffects(Player player) {
        // Reapply flight
        player.setAllowFlight(true);
        player.setFlying(true);
        
        // Apply semi-transparent effect
        // Make player invisible but keep their outline
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false)); // Increased amplifier to 1 for stronger glow
        
        // Create a team with more vibrant color
        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        org.bukkit.scoreboard.Team team = scoreboard.getTeam("translucent");
        if (team == null) {
            team = scoreboard.registerNewTeam("translucent");
        }
        team.setColor(org.bukkit.ChatColor.WHITE); // Changed to WHITE for stronger visibility
        team.addPlayer(player);
        
        // Reapply permanent potion effects
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        
        // Clear inventory (since afterlife players shouldn't have items)
        player.getInventory().clear();
        
        // Set health to max
        player.setHealth(player.getMaxHealth());
    }

    public static void applyTemporaryAfterlifeEffects(Player player) {
        // Teleport to high altitude
        Location loc = new Location(player.getWorld(), player.getLocation().getX(), 200, player.getLocation().getZ());
        player.teleport(loc);
        
        // Apply temporary confusion and blindness effects
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 50));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 50));
    }

    public static void applyAllAfterlifeEffects(Player player) {
        // Apply both permanent and temporary effects (for new afterlife entry)
        applyPermanentAfterlifeEffects(player);
        applyTemporaryAfterlifeEffects(player);
    }
    
    public static void removeAfterlifeEffects(Player player) {
        // Remove invisibility and glowing effects
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.GLOWING);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        
        // Remove player from translucent team
        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        org.bukkit.scoreboard.Team team = scoreboard.getTeam("translucent");
        if (team != null) {
            team.removePlayer(player);
        }
        
        // Disable flight
        player.setAllowFlight(false);
        player.setFlying(false);
    }
} 