package me.yodeling_goat.afterlifeplugin.grave.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.yodeling_goat.afterlifeplugin.afterlife.events.PlayerEnterAfterlifeEvent;
import me.yodeling_goat.afterlifeplugin.grave.GravestoneManager;


public class PlayerEnteredAfterlifeListener implements Listener {
    @EventHandler
    public void onAfterLife(PlayerEnterAfterlifeEvent event) {
        Player player = event.getPlayer();
        if (player.getLastDamageCause() != null) {
            String deathCause = player.getLastDamageCause().getCause().toString();
            GravestoneManager.createGravestone(player, deathCause);
        }
    }
}
