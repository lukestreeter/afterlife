package me.yodeling_goat.afterlifeplugin.afterlife;

import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.yodeling_goat.afterlifeplugin.afterlife.events.PlayerEnterAfterlifeEvent;
import org.bukkit.Location;
import me.yodeling_goat.afterlifeplugin.karma.KarmaManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AfterlifeManager {
    private static final Set<UUID> afterlifePlayers = new HashSet<>();
    private static File afterlifeFile;
    private static FileConfiguration afterlifeConfig;
    
    public static void initialize() {
        loadAfterlifeState();
    }
    
    private static void loadAfterlifeState() {
        afterlifeFile = new File(org.bukkit.Bukkit.getPluginManager().getPlugin("AfterLifePlugin").getDataFolder(), "afterlife.yml");
        if (!afterlifeFile.exists()) {
            try {
                afterlifeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        afterlifeConfig = YamlConfiguration.loadConfiguration(afterlifeFile);
        
        // Load saved afterlife players
        if (afterlifeConfig.contains("afterlife_players")) {
            for (String uuidString : afterlifeConfig.getStringList("afterlife_players")) {
                try {
                    afterlifePlayers.add(UUID.fromString(uuidString));
                } catch (IllegalArgumentException e) {
                    // Skip invalid UUIDs
                }
            }
        }
    }
    
    public static void saveAfterlifeState() {
        // Convert UUIDs to strings for saving
        java.util.List<String> uuidStrings = new java.util.ArrayList<>();
        for (UUID uuid : afterlifePlayers) {
            uuidStrings.add(uuid.toString());
        }
        afterlifeConfig.set("afterlife_players", uuidStrings);
        
        try {
            afterlifeConfig.save(afterlifeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendToAfterlife(Player player) {
        afterlifePlayers.add(player.getUniqueId());
        saveAfterlifeState();
        
        // Reset fireball count for new afterlife session
        me.yodeling_goat.afterlifeplugin.afterlife.util.MobMorphManager.resetFireballsUsed(player);
        
        Bukkit.getPluginManager().callEvent(new PlayerEnterAfterlifeEvent(player));
    }

    public static void removeFromAfterlife(Player player) {
        afterlifePlayers.remove(player.getUniqueId());
        saveAfterlifeState();
        removeAfterlifeEffects(player);
    }

    public static boolean isInAfterlife(Player player) {
        return afterlifePlayers.contains(player.getUniqueId());
    }

    public static Set<Player> getAfterlifePlayers() {
        Set<Player> players = new HashSet<>();
        for (UUID uuid : afterlifePlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }
    
    public static void clearAllAfterlifePlayers() {
        for (UUID uuid : new HashSet<>(afterlifePlayers)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                removeAfterlifeEffects(player);
            }
        }
        afterlifePlayers.clear();
        saveAfterlifeState();
    }

    public static void initializeAfterlifeState(Player player) {
        if (isInAfterlife(player)) {
            applyPermanentAfterlifeEffects(player);
        } else {
            // If player was in afterlife but server restarted, clear any lingering effects
            removeAfterlifeEffects(player);
        }
    }
    
    public static void cleanupOfflinePlayers() {
        // Remove players who are no longer online from the afterlife set
        afterlifePlayers.removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
        saveAfterlifeState();
    }

    public static void applyPermanentAfterlifeEffects(Player player) {
        // Reapply flight - ensure they can always fly
        player.setAllowFlight(true);
        player.setFlying(true);
        
        // Disable hunger for afterlife players
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        
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

        // remove karma display
        KarmaManager.removeKarmaDisplay(player);
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
        
        // Restore normal hunger (let it regenerate naturally)
        // Don't set specific values, let the game handle it
    }
} 