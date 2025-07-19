package me.yodeling_goat.afterlifeplugin.karma.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KarmaGoalAchievedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String goalName;
    private final int karmaThreshold;
    private final String rewardType;
    private final String rewardData;
    private final String message;

    public KarmaGoalAchievedEvent(Player player, String goalName, int karmaThreshold, 
                                 String rewardType, String rewardData, String message) {
        this.player = player;
        this.goalName = goalName;
        this.karmaThreshold = karmaThreshold;
        this.rewardType = rewardType;
        this.rewardData = rewardData;
        this.message = message;
    }

    public Player getPlayer() { return player; }
    public String getGoalName() { return goalName; }
    public int getKarmaThreshold() { return karmaThreshold; }
    public String getRewardType() { return rewardType; }
    public String getRewardData() { return rewardData; }
    public String getMessage() { return message; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
} 