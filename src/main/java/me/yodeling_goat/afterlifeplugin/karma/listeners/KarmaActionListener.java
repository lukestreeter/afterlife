package me.yodeling_goat.afterlifeplugin.karma.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import me.yodeling_goat.afterlifeplugin.karma.events.KarmaChangeRequestEvent;

public class KarmaActionListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        
        if (event.getEntity() instanceof Villager) {
            // Negative karma for harming villagers
            fireKarmaChange(player, -5, "Harming a villager");
        } else if (event.getEntity() instanceof Tameable) {
            Tameable tameable = (Tameable) event.getEntity();
            if (tameable.isTamed()) {
                // Negative karma for harming tamed animals
                fireKarmaChange(player, -3, "Harming a tamed animal");
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        Player player = event.getEntity().getKiller();
        
        if (event.getEntity() instanceof Villager) {
            // Major negative karma for killing villagers
            fireKarmaChange(player, -10, "Killing a villager");
        } else if (event.getEntity() instanceof Tameable) {
            Tameable tameable = (Tameable) event.getEntity();
            if (tameable.isTamed()) {
                // Negative karma for killing tamed animals
                fireKarmaChange(player, -5, "Killing a tamed animal");
            }
        }
    }

    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        // Positive karma for shearing sheep (sustainable farming)
        fireKarmaChange(player, 1, "Sustainable farming");
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        
        if (event.getRightClicked() instanceof Villager) {
            // Small positive karma for interacting with villagers
            fireKarmaChange(player, 1, "Interacting with villagers");
        }
    }

    private void fireKarmaChange(Player player, int karmaDelta, String reason) {
        KarmaChangeRequestEvent event = new KarmaChangeRequestEvent(player, karmaDelta, reason);
        Bukkit.getPluginManager().callEvent(event);
    }
} 