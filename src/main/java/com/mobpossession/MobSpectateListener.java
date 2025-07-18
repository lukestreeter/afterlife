package com.mobpossession;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.ChatColor;

public class MobSpectateListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity target = event.getRightClicked();
        if (target instanceof LivingEntity && !(target instanceof Player)) {
            LivingEntity mob = (LivingEntity) target;
            if (player.getGameMode() != GameMode.SPECTATOR) {
                player.setGameMode(GameMode.SPECTATOR);
            }
            player.setSpectatorTarget(mob);
            player.sendMessage(ChatColor.GREEN + "You are now viewing from the mob's perspective!");
            event.setCancelled(true);
        }
    }
} 