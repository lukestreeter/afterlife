package me.yodeling_goat.afterlifeplugin.karma.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class KarmaChangeRequestEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final int karmaDelta;
    private final String reason;
    private boolean cancelled = false;

    public KarmaChangeRequestEvent(Player player, int karmaDelta, String reason) {
        this.player = player;
        this.karmaDelta = karmaDelta;
        this.reason = reason;
    }

    public Player getPlayer() { return player; }
    public int getKarmaDelta() { return karmaDelta; }
    public String getReason() { return reason; }

    @Override
    public boolean isCancelled() { return cancelled; }
    @Override
    public void setCancelled(boolean cancel) { this.cancelled = cancel; }
    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
} 