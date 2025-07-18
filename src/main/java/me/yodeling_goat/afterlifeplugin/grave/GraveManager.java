package me.yodeling_goat.afterlifeplugin.grave;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import me.yodeling_goat.afterlifeplugin.afterlife.events.PlayerEnterAfterlifeEvent;

public class GraveManager implements Listener {
    public static void placeGrave(Player player, Location location) {
        Block block = location.getBlock();
        block.setType(Material.CHEST);
        Chest chest = (Chest) block.getState();
        org.bukkit.inventory.Inventory chestInventory = chest.getInventory();
        ItemStack[] playerItems = player.getInventory().getContents();
        for (ItemStack item : playerItems) {
            if (item != null) {
                chestInventory.addItem(item);
            }
        }
        ItemStack[] armorItems = player.getInventory().getArmorContents();
        for (ItemStack item : armorItems) {
            if (item != null) {
                chestInventory.addItem(item);
            }
        }
    }

    @EventHandler
    public void onPlayerEnterAfterlife(PlayerEnterAfterlifeEvent event) {
        Player player = event.getPlayer();
        placeGrave(player, player.getLocation());
    }
} 