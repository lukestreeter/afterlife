package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.Bukkit;
import me.yodeling_goat.afterlifeplugin.karma.events.KarmaChangeRequestEvent;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class EntityDeathListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) return;
        
        // Don't allow karma changes for players in the afterlife
        if (AfterlifeManager.isInAfterlife(killer)) {
            return;
        }
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
        Bukkit.getPluginManager().callEvent(new KarmaChangeRequestEvent(killer, karmaChange, "Killed " + type.name()));
    }
} 