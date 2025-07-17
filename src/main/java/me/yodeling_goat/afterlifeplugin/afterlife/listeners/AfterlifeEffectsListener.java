package me.yodeling_goat.afterlifeplugin.afterlife.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import me.yodeling_goat.afterlifeplugin.afterlife.events.PlayerEnterAfterlifeEvent;
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;

public class AfterlifeEffectsListener implements Listener {
    @EventHandler
    public void onPlayerEnterAfterlife(PlayerEnterAfterlifeEvent event) {
        Player player = event.getPlayer();
        AfterlifeManager.applyAllAfterlifeEffects(player);
    }
} 