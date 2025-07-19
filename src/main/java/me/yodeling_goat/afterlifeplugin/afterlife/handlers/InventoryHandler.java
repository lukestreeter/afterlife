package me.yodeling_goat.afterlifeplugin.afterlife.handlers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryHandler {

    private static InventoryHandler instance = new InventoryHandler();
    private HashMap<Player, ItemStack[]> inventory;
    private HashMap<Player, ItemStack[]> armor;

    private InventoryHandler() {
        inventory = new HashMap<Player, ItemStack[]>();
        armor = new HashMap<Player, ItemStack[]>();
    }

    public static InventoryHandler getInstance() {
        return instance;
    }

    /**
     * Saves the player's complete inventory and armor contents
     * Always saves everything without any permission checks
     */
    public void saveInventory(Player player) {
        ItemStack[] tempInventory = new ItemStack[player.getInventory().getSize()];
        ItemStack[] tempArmor = new ItemStack[player.getInventory().getArmorContents().length];
        
        tempInventory = player.getInventory().getContents();
        tempArmor = player.getInventory().getArmorContents();
        
        inventory.put(player, tempInventory);
        armor.put(player, tempArmor);
    }

    /**
     * Retrieves the saved inventory for a player
     * @return ItemStack array of the saved inventory, or null if not found
     */
    public ItemStack[] getInventory(Player player) {
        return inventory.get(player);
    }

    /**
     * Retrieves the saved armor for a player
     * @return ItemStack array of the saved armor, or null if not found
     */
    public ItemStack[] getArmor(Player player) {
        return armor.get(player);
    }

    /**
     * Removes the saved inventory and armor for a player
     * Call this when the player retrieves their items or respawns
     */
    public void removeInventory(Player player) {
        inventory.remove(player);
        armor.remove(player);
    }

    /**
     * Checks if a player has a saved inventory
     * @return true if inventory is saved, false otherwise
     */
    public boolean hasInventorySaved(Player player) {
        return inventory.containsKey(player);
    }

    /**
     * Checks if a player has saved armor
     * @return true if armor is saved, false otherwise
     */
    public boolean hasArmorSaved(Player player) {
        return armor.containsKey(player);
    }

    /**
     * Checks if a player has any saved items (inventory or armor)
     * @return true if player has any saved items, false otherwise
     */
    public boolean hasAnyItemsSaved(Player player) {
        return hasInventorySaved(player) || hasArmorSaved(player);
    }
} 