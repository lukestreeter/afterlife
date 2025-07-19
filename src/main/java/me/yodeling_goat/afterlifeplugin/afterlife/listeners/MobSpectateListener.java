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
    private static final String EXIT_MORPH_NAME = ChatColor.RED + "Exit Morph";
    private static final String EXIT_MORPH_LORE = ChatColor.YELLOW + "Left Click to exit morph mode!";

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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item != null && item.getType() == Material.PLAYER_HEAD && item.hasItemMeta()) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta != null && EXIT_MORPH_NAME.equals(meta.getDisplayName()) &&
                meta.hasLore() && meta.getLore().contains(EXIT_MORPH_LORE)) {
                
                if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    // Remove the head from inventory
                    item.setAmount(item.getAmount() - 1);
                    // Exit morph mode using the new system
                    MobMorphManager.exitMorph(player);
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        // Handle hotbar slot changes for morphed players
        if (MobMorphManager.isMorphed(player)) {
            int newSlot = event.getNewSlot();
            if (newSlot == 0) { // Slot 1 - Exit morph
                MobMorphManager.exitMorph(player);
            }
        }
    }
} 