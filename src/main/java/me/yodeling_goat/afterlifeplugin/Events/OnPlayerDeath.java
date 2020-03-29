package me.yodeling_goat.afterlifeplugin.Events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OnPlayerDeath implements Listener {

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

	private boolean isFatal(EntityDamageEvent event, Player player) {
		player.sendMessage("checking if this damage will hurt you");
		// determine how much damage dealt in event
		// determine damage reduction of armor
		double damage = event.getFinalDamage();
		double health = player.getHealth();
		// determine if player sustaining damage would be killed
		return damage > health;
	}

	private void sendToAfterlife(Player player) {
        World world = player.getWorld();
		Location loc = new Location(world,206.464,240.50000,-252.539);
		player.teleport(loc);
		// determine which afterlife: purgatory or paradise
		// make player immortal, give them their full life back
		// empty out all they have in their inventory
	}
}
