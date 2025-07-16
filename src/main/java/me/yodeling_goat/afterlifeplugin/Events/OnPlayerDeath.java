package me.yodeling_goat.afterlifeplugin.Events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.HashSet;
import java.util.Set;

public class OnPlayerDeath implements Listener {
	
	// Track players who are in the afterlife
	private static final Set<Player> afterlifePlayers = new HashSet<>();

	@EventHandler
	public void onFinalDamge(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		Player player = (Player) entity;
		if (isFatal(event, player)) {
			event.setCancelled(true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 50));
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 50));
			sendToAfterlife(player);
		}
	}
	
	// Backup handler for any death that doesn't trigger EntityDamageEvent
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		// Only process if player is not already in afterlife
		if (!afterlifePlayers.contains(player)) {
			// Prevent drops and death message
			event.getDrops().clear();
			event.setKeepInventory(true);
			event.setDeathMessage(null);
			
			// Add visual effects
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 50));
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 50));
			
			// Send to afterlife
			sendToAfterlife(player);
		}
	}

	private boolean isFatal(EntityDamageEvent event, Player player) {
		player.sendMessage("checking if this damage will hurt you - why so serious");
		// determine how much damage dealt in event
		// determine damage reduction of armor
		double damage = event.getFinalDamage();
		double health = player.getHealth();
		// determine if player sustaining damage would be killed
		return damage > health;
	}

	private void sendToAfterlife(Player player) {
        World world = player.getWorld();
		// Teleport high in the sky (Y=200) at the same X,Z coordinates
		Location loc = new Location(world, player.getLocation().getX(), 200, player.getLocation().getZ());
		player.teleport(loc);
		
		// Make player fly and set flight mode
		player.setAllowFlight(true);
		player.setFlying(true);
		
		// Make player appear ghost-like with potion effects
		// Note: Bukkit doesn't have direct transparency support, but we can use potion effects
		// to make them appear more ghost-like
		player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
		
		// Restore full health
		player.setHealth(player.getMaxHealth());
		
		// Clear inventory
		player.getInventory().clear();
		
		// Add player to afterlife tracking
		afterlifePlayers.add(player);
		
		// determine which afterlife: purgatory or paradise
		// make player immortal, give them their full life back
		// empty out all they have in their inventory
	}
	
	// Prevent afterlife players from breaking blocks
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (afterlifePlayers.contains(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	// Prevent afterlife players from placing blocks
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (afterlifePlayers.contains(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	// Prevent afterlife players from interacting with anything
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (afterlifePlayers.contains(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	// Prevent afterlife players from picking up items
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (afterlifePlayers.contains(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	// Prevent afterlife players from being damaged
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (afterlifePlayers.contains(player)) {
				event.setCancelled(true);
			}
		}
	}
	
	// Prevent afterlife players from damaging others
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (afterlifePlayers.contains(player)) {
				event.setCancelled(true);
			}
		}
	}
	
	// Method to remove player from afterlife (for when they respawn or leave)
	public static void removeFromAfterlife(Player player) {
		afterlifePlayers.remove(player);
	}
}
