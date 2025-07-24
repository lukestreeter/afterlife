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
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.enchantments.Enchantment;

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
                String rewardType = goalSection.getString("reward_type", "item");
                String message = goalSection.getString("message", "§a§lKARMA GOAL ACHIEVED!");
                List<RewardItem> rewardItems = new ArrayList<>();
                if (goalSection.isList("reward_items")) {
                    List<?> items = goalSection.getList("reward_items");
                    for (Object obj : items) {
                        if (obj instanceof Map) {
                            Map<?, ?> map = (Map<?, ?>) obj;
                            String id = map.get("id") != null ? map.get("id").toString() : "STONE";
                            String name = map.get("name") != null ? map.get("name").toString() : id;
                            int multiplier = map.get("multiplier") != null ? ((Number) map.get("multiplier")).intValue() : 1;
                            double chance = map.get("chance") != null ? ((Number) map.get("chance")).doubleValue() : 1.0;
                            String enchantment = map.get("enchantment") != null ? map.get("enchantment").toString() : null;
                            Integer enchantmentLevel = map.get("enchantment_level") != null ? ((Number) map.get("enchantment_level")).intValue() : null;
                            rewardItems.add(new RewardItem(id, name, multiplier, chance, enchantment, enchantmentLevel));
                        }
                    }
                }
                karmaGoals.put(goalName, new KarmaGoal(goalName, threshold, rewardType, rewardItems, message));
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
            goal.getRewardType(), goal.getRewardItems(), goal.getMessage()
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
        if ("item".equals(goal.getRewardType()) && !goal.getRewardItems().isEmpty()) {
            RewardItem selected = selectWeightedRandomReward(goal.getRewardItems());
            if (selected == null) {
                plugin.getLogger().warning("No valid reward item selected for goal: " + goal.getName());
                return;
            }
            Material material;
            try {
                material = Material.valueOf(selected.getId());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material for karma goal reward: " + selected.getId());
                return;
            }
            ItemStack item = new ItemStack(material, selected.getMultiplier());
            if (selected.getEnchantment() != null && selected.getEnchantmentLevel() != null) {
                Enchantment ench = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(selected.getEnchantment().toLowerCase()));
                if (ench != null) {
                    item.addUnsafeEnchantment(ench, selected.getEnchantmentLevel());
                }
            }
            // Try to add to inventory, drop if full
            if (player.getInventory().addItem(item).isEmpty()) {
                player.sendMessage("§aReward added to your inventory!");
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                player.sendMessage("§aReward dropped at your feet (inventory full)!");
            }
            // Custom message with reward details
            String rewardMsg = goal.getMessage() + "  You've been given " + selected.getName() + (selected.getMultiplier() > 1 ? (" x" + selected.getMultiplier()) : "") + " for achieving " + goal.getThreshold() + " karma!";
            player.sendMessage(rewardMsg);
        }
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
        private final List<RewardItem> rewardItems;
        private final String message;

        public KarmaGoal(String name, int threshold, String rewardType, List<RewardItem> rewardItems, String message) {
            this.name = name;
            this.threshold = threshold;
            this.rewardType = rewardType;
            this.rewardItems = rewardItems;
            this.message = message;
        }
        public String getName() { return name; }
        public int getThreshold() { return threshold; }
        public String getRewardType() { return rewardType; }
        public List<RewardItem> getRewardItems() { return rewardItems; }
        public String getMessage() { return message; }
    }
    public static class RewardItem {
        private final String id;
        private final String name;
        private final int multiplier;
        private final double chance;
        private final String enchantment;
        private final Integer enchantmentLevel;
        public RewardItem(String id, String name, int multiplier, double chance, String enchantment, Integer enchantmentLevel) {
            this.id = id;
            this.name = name;
            this.multiplier = multiplier;
            this.chance = chance;
            this.enchantment = enchantment;
            this.enchantmentLevel = enchantmentLevel;
        }
        public String getId() { return id; }
        public String getName() { return name; }
        public int getMultiplier() { return multiplier; }
        public double getChance() { return chance; }
        public String getEnchantment() { return enchantment; }
        public Integer getEnchantmentLevel() { return enchantmentLevel; }
    }
    private RewardItem selectWeightedRandomReward(List<RewardItem> items) {
        double total = 0.0;
        for (RewardItem item : items) total += item.getChance();
        double r = new Random().nextDouble() * total;
        double cumulative = 0.0;
        for (RewardItem item : items) {
            cumulative += item.getChance();
            if (r <= cumulative) return item;
        }
        return items.isEmpty() ? null : items.get(0);
    }
} 