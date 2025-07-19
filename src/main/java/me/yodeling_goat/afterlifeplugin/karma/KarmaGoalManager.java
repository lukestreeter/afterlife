package me.yodeling_goat.afterlifeplugin.karma;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.yodeling_goat.afterlifeplugin.karma.events.KarmaChangeEvent;
import me.yodeling_goat.afterlifeplugin.karma.events.KarmaGoalAchievedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KarmaGoalManager implements Listener {
    private final JavaPlugin plugin;
    private final Map<String, KarmaGoal> karmaGoals = new HashMap<>();
    private final Map<Player, Set<String>> achievedGoals = new HashMap<>();

    public KarmaGoalManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadKarmaGoals();
    }

    private void loadKarmaGoals() {
        ConfigurationSection goalsSection = plugin.getConfig().getConfigurationSection("karma-goals");
        if (goalsSection == null) {
            plugin.getLogger().warning("No karma goals found in config.yml");
            return;
        }

        for (String goalName : goalsSection.getKeys(false)) {
            ConfigurationSection goalSection = goalsSection.getConfigurationSection(goalName);
            if (goalSection != null) {
                int threshold = goalSection.getInt("threshold", 0);
                String rewardType = goalSection.getString("reward", "item");
                String rewardData = goalSection.getString("reward_data", "");
                String message = goalSection.getString("message", "§a§lKARMA GOAL ACHIEVED!");

                karmaGoals.put(goalName, new KarmaGoal(goalName, threshold, rewardType, rewardData, message));
            }
        }
        plugin.getLogger().info("Loaded " + karmaGoals.size() + " karma goals");
    }

    @EventHandler
    public void onKarmaChange(KarmaChangeEvent event) {
        Player player = event.getPlayer();
        int newKarma = event.getNewKarma();
        
        // Check for new goal achievements
        checkForGoalAchievements(player, newKarma);
    }

    private void checkForGoalAchievements(Player player, int karma) {
        for (KarmaGoal goal : karmaGoals.values()) {
            if (karma >= goal.getThreshold() && !hasAchievedGoal(player, goal.getName())) {
                // Player has achieved this goal
                achieveGoal(player, goal);
            }
        }
    }

    private boolean hasAchievedGoal(Player player, String goalName) {
        Set<String> playerGoals = achievedGoals.get(player);
        return playerGoals != null && playerGoals.contains(goalName);
    }

    private void achieveGoal(Player player, KarmaGoal goal) {
        // Mark goal as achieved
        achievedGoals.computeIfAbsent(player, k -> new java.util.HashSet<>()).add(goal.getName());

        // Fire the event
        KarmaGoalAchievedEvent event = new KarmaGoalAchievedEvent(
            player, goal.getName(), goal.getThreshold(), 
            goal.getRewardType(), goal.getRewardData(), goal.getMessage()
        );
        Bukkit.getPluginManager().callEvent(event);

        // Give reward
        giveReward(player, goal);

        // Send message
        player.sendMessage(goal.getMessage());
        
        // Broadcast to all players
        Bukkit.broadcastMessage("§6§l" + player.getName() + " §r§ahas achieved the karma goal: §a" + goal.getName() + "§r!");
    }

    private void giveReward(Player player, KarmaGoal goal) {
        if ("item".equals(goal.getRewardType())) {
            try {
                Material material = Material.valueOf(goal.getRewardData());
                ItemStack item = new ItemStack(material);
                
                // Try to add to inventory, drop if full
                if (player.getInventory().addItem(item).isEmpty()) {
                    player.sendMessage("§aReward added to your inventory!");
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    player.sendMessage("§aReward dropped at your feet (inventory full)!");
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material for karma goal reward: " + goal.getRewardData());
            }
        }
        // Add more reward types here as needed (experience, money, etc.)
    }

    public void resetPlayerGoals(Player player) {
        achievedGoals.remove(player);
    }

    /**
     * Get the next blessing goal for a player based on their current karma
     * @param player The player to check
     * @return The next KarmaGoal, or null if no more goals available
     */
    public KarmaGoal getNextBlessingGoal(Player player) {
        int currentKarma = KarmaManager.getKarma(player);
        KarmaGoal nextGoal = null;
        
        for (KarmaGoal goal : karmaGoals.values()) {
            if (goal.getThreshold() > currentKarma) {
                if (nextGoal == null || goal.getThreshold() < nextGoal.getThreshold()) {
                    nextGoal = goal;
                }
            }
        }
        
        return nextGoal;
    }

    /**
     * Get the current blessing goal that the player has achieved
     * @param player The player to check
     * @return The current KarmaGoal, or null if no goal achieved
     */
    public KarmaGoal getCurrentBlessingGoal(Player player) {
        int currentKarma = KarmaManager.getKarma(player);
        KarmaGoal currentGoal = null;
        
        for (KarmaGoal goal : karmaGoals.values()) {
            if (goal.getThreshold() <= currentKarma) {
                if (currentGoal == null || goal.getThreshold() > currentGoal.getThreshold()) {
                    currentGoal = goal;
                }
            }
        }
        
        return currentGoal;
    }

    /**
     * Get all karma goals sorted by threshold
     * @return Map of karma goals sorted by threshold
     */
    public Map<String, KarmaGoal> getKarmaGoals() {
        return new HashMap<>(karmaGoals);
    }

    public static class KarmaGoal {
        private final String name;
        private final int threshold;
        private final String rewardType;
        private final String rewardData;
        private final String message;

        public KarmaGoal(String name, int threshold, String rewardType, String rewardData, String message) {
            this.name = name;
            this.threshold = threshold;
            this.rewardType = rewardType;
            this.rewardData = rewardData;
            this.message = message;
        }

        public String getName() { return name; }
        public int getThreshold() { return threshold; }
        public String getRewardType() { return rewardType; }
        public String getRewardData() { return rewardData; }
        public String getMessage() { return message; }
    }
} 