package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onFinalDamge(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        if (isFatal(event, player)) {
            event.setCancelled(true);
            AfterlifeManager.sendToAfterlife(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!AfterlifeManager.isInAfterlife(player)) {
            AfterlifeManager.sendToAfterlife(player);
        }
    }

    private boolean isFatal(EntityDamageEvent event, Player player) {
        double damage = event.getFinalDamage();
        double health = player.getHealth();
        return damage > health;
    }
} 