package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.ChatColor;
import java.util.Collections;
import me.yodeling_goat.afterlifeplugin.afterlife.util.MobMorphManager;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class MobSpectateListener implements Listener {


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity target = event.getRightClicked();
        
        // Only allow morphing if player is in the afterlife
        if (!AfterlifeManager.isInAfterlife(player)) {
            return;
        }
        
        if (target instanceof LivingEntity && !(target instanceof Player)) {
            LivingEntity mob = (LivingEntity) target;
            
            // Use the new morphing system for full control
            MobMorphManager.morphIntoMob(player, mob);
            event.setCancelled(true);
        }
    }


    

} 