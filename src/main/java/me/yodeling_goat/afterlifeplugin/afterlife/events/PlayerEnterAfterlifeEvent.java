package me.yodeling_goat.afterlifeplugin.afterlife.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEnterAfterlifeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    public PlayerEnterAfterlifeEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() { return player; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
} 