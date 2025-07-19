package me.yodeling_goat.afterlifeplugin.impersonation.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.Bukkit;

public class EntitySpawnListener implements Listener {
    
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        
        // Check if this is an animal disguise entity by looking for the tag
        if (entity.getScoreboardTags().contains("animal_disguise")) {
            // Find the player name from the tags
            String playerName = null;
            for (String tag : entity.getScoreboardTags()) {
                if (!tag.equals("animal_disguise")) {
                    playerName = tag;
                    break;
                }
            }
            
            if (playerName != null) {
                // Add metadata to the entity to link it to the player
                entity.setMetadata("animal_disguise_player", 
                    new FixedMetadataValue(Bukkit.getPluginManager().getPlugin("AfterLifePlugin"), playerName));
            }
        }
    }
} 