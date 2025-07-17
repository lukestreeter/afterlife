package me.yodeling_goat.afterlifeplugin.karma.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KarmaChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final int oldKarma;
    private final int newKarma;

    public KarmaChangeEvent(Player player, int oldKarma, int newKarma) {
        this.player = player;
        this.oldKarma = oldKarma;
        this.newKarma = newKarma;
    }

    public Player getPlayer() { return player; }
    public int getOldKarma() { return oldKarma; }
    public int getNewKarma() { return newKarma; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
} 