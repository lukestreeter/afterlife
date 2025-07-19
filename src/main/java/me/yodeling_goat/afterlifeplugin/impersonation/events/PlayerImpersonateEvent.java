package me.yodeling_goat.afterlifeplugin.impersonation.events;

import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerImpersonateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final EntityType entityType;

    public PlayerImpersonateEvent(Player player, EntityType entityType) {
        this.player = player;
        this.entityType = entityType;
    }

    public Player getPlayer() {
        return player;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
} 