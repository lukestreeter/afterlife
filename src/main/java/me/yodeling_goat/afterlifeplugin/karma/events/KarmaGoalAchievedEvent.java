package me.yodeling_goat.afterlifeplugin.karma.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.List;
import me.yodeling_goat.afterlifeplugin.karma.KarmaGoalManager.RewardItem;

public class KarmaGoalAchievedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String goalName;
    private final int karmaThreshold;
    private final String rewardType;
    private final List<RewardItem> rewardItems;
    private final String message;

    public KarmaGoalAchievedEvent(Player player, String goalName, int karmaThreshold, 
                                 String rewardType, List<RewardItem> rewardItems, String message) {
        this.player = player;
        this.goalName = goalName;
        this.karmaThreshold = karmaThreshold;
        this.rewardType = rewardType;
        this.rewardItems = rewardItems;
        this.message = message;
    }

    public Player getPlayer() { return player; }
    public String getGoalName() { return goalName; }
    public int getKarmaThreshold() { return karmaThreshold; }
    public String getRewardType() { return rewardType; }
    public List<RewardItem> getRewardItems() { return rewardItems; }
    public String getMessage() { return message; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
} 