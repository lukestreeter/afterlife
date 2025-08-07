package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.afterlife.handlers.InventoryHandler;
import me.yodeling_goat.afterlifeplugin.AfterLifePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Location;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Save the player's inventory before sending to afterlife
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();
        
        // Get particle configuration from config
        int particleCount = AfterLifePlugin.getInstance().getConfig().getInt("grave.death_particles.count", 50);
        double spreadX = AfterLifePlugin.getInstance().getConfig().getDouble("grave.death_particles.spread_x", 1.0);
        double spreadY = AfterLifePlugin.getInstance().getConfig().getDouble("grave.death_particles.spread_y", 1.0);
        double spreadZ = AfterLifePlugin.getInstance().getConfig().getDouble("grave.death_particles.spread_z", 1.0);
        double speed = AfterLifePlugin.getInstance().getConfig().getDouble("grave.death_particles.speed", 0.5);
        
        // Show configurable NETHER_WART_BLOCK particles at death location
        // Move particles one block higher
        Location particleLocation = deathLocation.clone().add(0, 1, 0);
        
        // Spawn particles over 0.1 seconds (2 ticks) - instant burst
        for (int i = 0; i < 2; i++) {
            final int wave = i;
            org.bukkit.Bukkit.getScheduler().runTaskLater(AfterLifePlugin.getInstance(), () -> {
                player.getWorld().spawnParticle(Particle.BLOCK_CRACK, particleLocation, particleCount / 2, spreadX, spreadY, spreadZ, speed, Material.NETHER_WART_BLOCK.createBlockData());
            }, wave * 1L);
        }
        
        InventoryHandler.getInstance().saveInventory(player);
        event.getDrops().clear(); // Prevent drops
        
        if (!AfterlifeManager.isInAfterlife(player)) {
            AfterlifeManager.sendToAfterlife(player);
        }
    }
} 