package me.yodeling_goat.afterlifeplugin.karma;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import java.util.HashMap;
import me.yodeling_goat.afterlifeplugin.karma.events.KarmaChangeEvent;
import me.yodeling_goat.afterlifeplugin.karma.events.KarmaChangeRequestEvent;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class KarmaManager implements Listener {
    private static final HashMap<String, Integer> playerKarma = new HashMap<>();
    private static final HashMap<Player, BossBar> playerBossBars = new HashMap<>();

    public static int getKarma(Player player) {
        return playerKarma.getOrDefault(player.getName(), 50); // Default 50
    }

    public static void setKarma(Player player, int value) {
        int oldKarma = getKarma(player);
        playerKarma.put(player.getName(), value);
        Bukkit.getPluginManager().callEvent(new KarmaChangeEvent(player, oldKarma, value));
    }

    @EventHandler
    public void onKarmaChangeRequest(KarmaChangeRequestEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        
        // Don't allow karma changes for players in the afterlife
        if (AfterlifeManager.isInAfterlife(player)) {
            event.setCancelled(true);
            return;
        }
        
        int karmaDelta = event.getKarmaDelta();
        int currentKarma = getKarma(player);
        int newKarma = Math.max(1, Math.min(100, currentKarma + karmaDelta));
        
        setKarma(player, newKarma);
        
        // Send message to player about karma change
        if (karmaDelta != 0) {
            sendKarmaChangeMessage(player, karmaDelta, event.getReason());
        }
    }

    private void sendKarmaChangeMessage(Player player, int karmaDelta, String reason) {
        String message = karmaDelta > 0 ? 
            "§a+" + karmaDelta + " Karma: " + reason :
            "§c" + karmaDelta + " Karma: " + reason;
        player.sendMessage(message);
    }

    @EventHandler
    public void onKarmaChange(KarmaChangeEvent event) {
        updatePlayerHandicaps(event.getPlayer(), event.getNewKarma());
        updateKarmaDisplay(event.getPlayer(), event.getNewKarma());
        // Add more handlers here as needed
    }

    // --- Effect Handlers ---
    public static void updatePlayerHandicaps(Player player, int karma) {
        // Remove all relevant effects first
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.POISON);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.HUNGER);
        player.removePotionEffect(PotionEffectType.ABSORPTION);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        player.removePotionEffect(PotionEffectType.FAST_DIGGING);
        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
        
        // Only apply positive effects for good karma (50+)
        // No negative effects for bad karma (1-49)
        if (karma > 50 && karma <= 60) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 4, true, false));
        } else if (karma > 60 && karma <= 70) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 4, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2, true, false));
        } else if (karma > 70 && karma <= 80) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 4, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 4, true, false));
        } else if (karma > 80 && karma <= 90) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 4, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 4, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 5, true, false));
        } else if (karma > 90 && karma <= 95) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 4, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 4, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 5, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 3, true, false));
        } else if (karma > 95 && karma <= 100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 4, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 4, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 5, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 3, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 9, true, false));
        }
        // For karma 1-50: No effects (neutral)
    }

    public static void updateKarmaDisplay(Player player, int karma) {
        BarColor color;
        if (karma >= 1 && karma < 20) {
            color = BarColor.RED;
        } else if (karma >= 20 && karma < 40) {
            color = BarColor.PINK; // Closest to orange
        } else if (karma >= 40 && karma < 60) {
            color = BarColor.YELLOW;
        } else if (karma >= 60 && karma < 80) {
            color = BarColor.GREEN;
        } else if (karma >= 80 && karma <= 100) {
            color = BarColor.BLUE; // Closest to lawn green
        } else {
            color = BarColor.WHITE;
        }
        String title = "Karma: " + karma + "/100";
        BossBar bar = playerBossBars.get(player);
        if (bar == null) {
            bar = Bukkit.createBossBar(title, color, BarStyle.SOLID);
            playerBossBars.put(player, bar);
        } else {
            bar.setTitle(title);
            bar.setColor(color);
        }
        bar.setProgress(Math.max(0.01, Math.min(1.0, karma / 100.0)));
        bar.addPlayer(player); // Ensure player is added to the bar
    }

    public static void initializeKarmaDisplay(Player player) {
        // Don't show karma display for players in the afterlife
        if (AfterlifeManager.isInAfterlife(player)) {
            return;
        }
        
        int karma = getKarma(player);
        updateKarmaDisplay(player, karma);
    }

    public static void removeKarmaDisplay(Player player) {
        BossBar bar = playerBossBars.get(player);
        if (bar != null) {
            bar.removePlayer(player);
            playerBossBars.remove(player);
        }
    }
} 