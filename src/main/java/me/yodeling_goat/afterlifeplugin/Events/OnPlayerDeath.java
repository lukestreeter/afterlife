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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.Bukkit;
import java.util.HashMap;
import me.yodeling_goat.afterlifeplugin.AfterLifePlugin;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.EntityType;

public class OnPlayerDeath implements Listener {
	
	// Track players who are in the afterlife
	private static final Set<Player> afterlifePlayers = new HashSet<>();

	// Simple static Karma tracking (UUID -> Integer)
	private static final HashMap<String, Integer> playerKarma = new HashMap<>();
	private static final HashMap<Player, BossBar> playerBossBars = new HashMap<>();

	// Get or initialize a player's Karma
	public static int getKarma(Player player) {
		return playerKarma.getOrDefault(player.getName(), 50); // Default 50
	}

	public static void setKarma(Player player, int value) {
		playerKarma.put(player.getName(), value);
		showKarmaBar(player);
	}

	// Show/update the Karma BossBar
	public static void showKarmaBar(Player player) {
		int karma = getKarma(player);
		BarColor color;
		if (karma >= 1 && karma < 20) {
			color = BarColor.RED;
		} else if (karma >= 20 && karma < 40) {
			color = BarColor.PINK; // Closest to orange
		} else if (karma >= 40 && karma < 60) {
			color = BarColor.YELLOW;
		} else if (karma >= 60 && karma < 80) {
			color = BarColor.GREEN;
		} else if (karma >= 80 && karma <= 100) {
			color = BarColor.BLUE; // Closest to lawn green
		} else {
			color = BarColor.WHITE;
		}
		String title = "Karma: " + karma + "/100";
		BossBar bar = playerBossBars.get(player);
		if (bar == null) {
			bar = Bukkit.createBossBar(title, color, BarStyle.SOLID);
			playerBossBars.put(player, bar);
			bar.addPlayer(player);
		} else {
			bar.setTitle(title);
			bar.setColor(color);
		}
		bar.setProgress(Math.max(0.01, Math.min(1.0, karma / 100.0)));
	}

	@EventHandler
	public void onFinalDamge(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		Player player = (Player) entity;
		if (isFatal(event, player)) {
			event.setCancelled(true);
			// Place chest at death location and transfer inventory
			placeChestAtLocation(player.getLocation(), player);
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
			
			// Place chest at death location and transfer inventory
			placeChestAtLocation(player.getLocation(), player);
			
			// Show Karma bar on death
			showKarmaBar(player);

			// Send to afterlife
			sendToAfterlife(player);
		}
	}

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) return;
        int karmaChange = 0;
        EntityType type = entity.getType();
        switch (type) {
            case PLAYER:
                karmaChange = -5;
                break;
            case COW:
            case PIG:
            case SHEEP:
            case CHICKEN:
            case HORSE:
            case WOLF:
            case RABBIT:
            case CAT:
            case OCELOT:
            case PARROT:
            case LLAMA:
            case DONKEY:
            case MULE:
            case FOX:
            case TURTLE:
            case PANDA:
            case POLAR_BEAR:
                karmaChange = -2;
                break;
            case ENDERMAN:
                karmaChange = 4;
                break;
            case ZOMBIE:
                karmaChange = 1;
                break;
            case ZOMBIE_VILLAGER:
                karmaChange = 1;
                break;
            case SPIDER:
                karmaChange = 1;
                break;
            case SKELETON:
                karmaChange = 2;
                break;
            case WITHER:
                karmaChange = 15;
                break;
            case ENDER_DRAGON:
                karmaChange = 15;
                break;
            case PHANTOM:
                karmaChange = 2;
                break;
            case BLAZE:
                karmaChange = 3;
                break;
            case WITHER_SKELETON:
                karmaChange = 5;
                break;
            case PIG_ZOMBIE:
                karmaChange = -2;
                break;
            case MAGMA_CUBE:
                karmaChange = 1;
                break;
            // Hostile mobs (default +1)
            case CREEPER:
            case DROWNED:
            case HUSK:
            case STRAY:
            case VEX:
            case VINDICATOR:
            case EVOKER:
            case PILLAGER:
            case ILLUSIONER:
            case RAVAGER:
            case SHULKER:
            case SILVERFISH:
            case SLIME:
            case WITCH:
            case GUARDIAN:
            case ELDER_GUARDIAN:
            case GHAST:
                karmaChange = 1;
                break;
            // Passive mobs (default -2)
            case BAT:
            case SQUID:
            case COD:
            case SALMON:
            case PUFFERFISH:
            case TROPICAL_FISH:
            case DOLPHIN:
            case BEE:
                karmaChange = -2;
                break;
            default:
                // If not listed, no Karma change
                break;
        }
        if (karmaChange != 0) {
            String entityName = type.name().replace('_', ' ').toLowerCase();
            entityName = entityName.substring(0, 1).toUpperCase() + entityName.substring(1);
            String sign = karmaChange > 0 ? "gained" : "lost";
            String color = karmaChange > 0 ? "§a" : "§c";
            String msg = String.format("%sHey! You've just %s %s%d Karma by killing a %s!", color, sign, karmaChange > 0 ? "+" : "", karmaChange, entityName);
            killer.sendMessage(msg);
        }
        int newKarma = Math.max(1, Math.min(100, getKarma(killer) + karmaChange));
        setKarma(killer, newKarma);
    }

	private boolean isFatal(EntityDamageEvent event, Player player) {
		player.sendMessage("Careful! You've taken damage and will be temporarily restricted from playing if you die!");
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

    // Place a chest at the given location and transfer player's inventory to it
    private void placeChestAtLocation(Location location, Player player) {
        // Get the block at the player's location
        Block block = location.getBlock();
        // Set the block to a chest
        block.setType(Material.CHEST);
        
        // Get the chest state and its inventory
        Chest chest = (Chest) block.getState();
        org.bukkit.inventory.Inventory chestInventory = chest.getInventory();
        
        // Transfer all items from player's inventory to the chest
        ItemStack[] playerItems = player.getInventory().getContents();
        for (ItemStack item : playerItems) {
            if (item != null) {
                chestInventory.addItem(item);
            }
        }
        // Transfer armor items
        ItemStack[] armorItems = player.getInventory().getArmorContents();
        for (ItemStack item : armorItems) {
            if (item != null) {
                chestInventory.addItem(item);
            }
        }
    }
}
