package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.afterlife.handlers.InventoryHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Save the player's inventory before sending to afterlife
        Player player = event.getEntity();
        InventoryHandler.getInstance().saveInventory(player);
        event.getDrops().clear(); // Prevent drops
        if (!AfterlifeManager.isInAfterlife(player)) {
            AfterlifeManager.sendToAfterlife(player);
        }
    }
} 