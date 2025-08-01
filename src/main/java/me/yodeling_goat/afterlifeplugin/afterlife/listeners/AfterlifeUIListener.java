package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.afterlife.events.PlayerEnterAfterlifeEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class AfterlifeUIListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), () -> {
                hideHealthAndHungerBar(player);
            }, 5L);
        }
    }
    
    @EventHandler
    public void onPlayerEnterAfterlife(PlayerEnterAfterlifeEvent event) {
        Player player = event.getPlayer();
        hideHealthAndHungerBar(player);
    }
    
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), () -> {
                hideHealthAndHungerBar(player);
            }, 3L);
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (AfterlifeManager.isInAfterlife(player)) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), () -> {
                hideHealthAndHungerBar(player);
            }, 3L);
        }
    }
    
    /**
     * Hides the health and hunger bars for a player in the afterlife
     */
    public static void hideHealthAndHungerBar(Player player) {
        // Store original max health if not already stored
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttribute != null) {
            if (!player.getPersistentDataContainer().has(
                new org.bukkit.NamespacedKey(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), "original_max_health"), 
                org.bukkit.persistence.PersistentDataType.DOUBLE)) {
                player.getPersistentDataContainer().set(
                    new org.bukkit.NamespacedKey(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), "original_max_health"),
                    org.bukkit.persistence.PersistentDataType.DOUBLE,
                    maxHealthAttribute.getBaseValue()
                );
            }
            
            // Set max health to minimum possible to hide hearts
            maxHealthAttribute.setBaseValue(0.1);
        }
        
        // Set health to prevent death but hide hearts
        player.setHealth(0.1);
        
        // Remove any absorption hearts
        player.setAbsorptionAmount(0);
        
        // Disable hunger completely
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        player.setExhaustion(0.0f);
        
        // Add saturation effect to prevent hunger loss  
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false, false));
        
        // Add wither effect to make hearts black (indicates death/afterlife state)
        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 0, false, false, false));
        
        // Make player invulnerable to prevent death with 0 hearts
        player.setInvulnerable(true);
        
        // Schedule periodic maintenance to ensure effects stay active
        Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), () -> {
            if (AfterlifeManager.isInAfterlife(player) && player.isOnline()) {
                // Maintain zero absorption
                if (player.getAbsorptionAmount() > 0) {
                    player.setAbsorptionAmount(0);
                }
                
                // Maintain food level
                if (player.getFoodLevel() < 20) {
                    player.setFoodLevel(20);
                    player.setSaturation(20.0f);
                }
                
                // Ensure saturation effect is still active
                if (!player.hasPotionEffect(PotionEffectType.SATURATION)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false, false));
                }
                
                // Ensure wither effect is still active (for black hearts)
                if (!player.hasPotionEffect(PotionEffectType.WITHER)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 0, false, false, false));
                }
                
                // Ensure invulnerability
                if (!player.isInvulnerable()) {
                    player.setInvulnerable(true);
                }
            }
        }, 20L, 20L); // Check every second
    }
    
    /**
     * Restores the health and hunger bars for a player leaving the afterlife
     */
    public static void showHealthAndHungerBar(Player player) {
        // Remove potion effects
        player.removePotionEffect(PotionEffectType.SATURATION);
        player.removePotionEffect(PotionEffectType.WITHER);
        
        // Clear absorption amount
        player.setAbsorptionAmount(0);
        
        // Remove invulnerability
        player.setInvulnerable(false);
        
        // Restore original max health
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttribute != null) {
            org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), "original_max_health");
            if (player.getPersistentDataContainer().has(key, org.bukkit.persistence.PersistentDataType.DOUBLE)) {
                double originalMaxHealth = player.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.DOUBLE);
                maxHealthAttribute.setBaseValue(originalMaxHealth);
                player.setHealth(originalMaxHealth);
                
                // Remove the stored value
                player.getPersistentDataContainer().remove(key);
            } else {
                // Fallback to default
                maxHealthAttribute.setBaseValue(20.0);
                player.setHealth(20.0);
            }
        }
        
        // Reset food to normal levels (not full)
        player.setFoodLevel(18);
        player.setSaturation(5.0f);
        player.setExhaustion(0.0f);
    }
} 