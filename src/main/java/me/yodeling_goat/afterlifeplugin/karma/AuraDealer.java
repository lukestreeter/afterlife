package me.yodeling_goat.afterlifeplugin.karma;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AuraDealer implements Listener, CommandExecutor {
    
    private static final HashMap<UUID, Boolean> auraDealers = new HashMap<>();
    private static final HashMap<UUID, Integer> dealerPoints = new HashMap<>();
    private static final int MAX_POINTS_PER_DEALER = 1000; // Maximum points a dealer can give per session
    private static final int POINTS_PER_GIVE = 10; // Points given per interaction
    
    // Whitelist of player names who can use aura dealer commands
    private static final List<String> WHITELISTED_PLAYERS = new ArrayList<>();
    
    static {
        // Add whitelisted players here
        WHITELISTED_PLAYERS.add("sesudos_pitufo");
        // Add more players as needed:
        // WHITELISTED_PLAYERS.add("PlayerName2");
        // WHITELISTED_PLAYERS.add("PlayerName3");
    }
    
    public AuraDealer() {
        // Initialize with some default aura dealers (you can modify this)
        // auraDealers.put(UUID.fromString("example-uuid"), true);
    }
    
    /**
     * Check if a player is an aura dealer
     */
    public static boolean isAuraDealer(Player player) {
        return auraDealers.getOrDefault(player.getUniqueId(), false);
    }
    
    /**
     * Add a player as an aura dealer
     */
    public static void addAuraDealer(Player player) {
        if (!WHITELISTED_PLAYERS.contains(player.getName())) {
            player.sendMessage(ChatColor.RED + "You are not whitelisted to use aura dealer commands!");
            return;
        }
        
        auraDealers.put(player.getUniqueId(), true);
        dealerPoints.put(player.getUniqueId(), 0);
        player.sendMessage(ChatColor.GREEN + "=== AURA DEALER ACTIVATED ===");
        player.sendMessage(ChatColor.YELLOW + "You can now give karma points to other players!");
        player.sendMessage(ChatColor.WHITE + "");
        player.sendMessage(ChatColor.GOLD + "Commands:");
        player.sendMessage(ChatColor.YELLOW + "• /auragive <player> [points] - Give karma points");
        player.sendMessage(ChatColor.YELLOW + "• /auraremove <player> [points] - Remove karma points");
        player.sendMessage(ChatColor.YELLOW + "• /auragive PlayerName 10 - Give 10 karma points");
        player.sendMessage(ChatColor.YELLOW + "• /auraremove PlayerName 10 - Remove 10 karma points");
        player.sendMessage(ChatColor.YELLOW + "• /auragive PlayerName - Give default 10 points");
        player.sendMessage(ChatColor.WHITE + "");
        player.sendMessage(ChatColor.GRAY + "Note: You can give 1-50 points per use, max 1000 per session");
        player.sendMessage(ChatColor.GRAY + "Karma points affect player effects and status!");
    }
    
    /**
     * Remove a player as an aura dealer
     */
    public static void removeAuraDealer(Player player) {
        auraDealers.remove(player.getUniqueId());
        dealerPoints.remove(player.getUniqueId());
        player.sendMessage(ChatColor.RED + "You are no longer an Aura Dealer!");
    }
    
    /**
     * Give karma points to a target player
     */
    public static boolean giveKarmaPoints(Player dealer, Player target, int points) {
        if (!WHITELISTED_PLAYERS.contains(dealer.getName())) {
            dealer.sendMessage(ChatColor.RED + "You are not whitelisted to use aura dealer commands!");
            return false;
        }
        
        int currentPoints = dealerPoints.getOrDefault(dealer.getUniqueId(), 0);
        if (currentPoints + points > MAX_POINTS_PER_DEALER) {
            dealer.sendMessage(ChatColor.RED + "You have reached your maximum points limit for this session!");
            return false;
        }
        
        // Give karma to target
        int currentKarma = KarmaManager.getKarma(target);
        int newKarma = Math.min(100, currentKarma + points);
        KarmaManager.setKarma(target, newKarma);
        
        // Update dealer's points
        dealerPoints.put(dealer.getUniqueId(), currentPoints + points);
        
                            // Send messages
                    dealer.sendMessage(ChatColor.GREEN + "✅ Successfully gave " + ChatColor.YELLOW + points + ChatColor.GREEN + " karma points to " + ChatColor.YELLOW + target.getName());
                    dealer.sendMessage(ChatColor.GRAY + "📊 Session usage: " + (currentPoints + points) + "/" + MAX_POINTS_PER_DEALER + " points");

                    target.sendMessage(ChatColor.GREEN + "🎁 You received " + ChatColor.YELLOW + points + ChatColor.GREEN + " karma points from " + ChatColor.YELLOW + dealer.getName());
                    target.sendMessage(ChatColor.GRAY + "💡 Karma affects your afterlife effects and status!");
        
        return true;
    }
    
    /**
     * Remove karma points from a target player
     */
    public static boolean removeKarmaPoints(Player dealer, Player target, int points) {
        if (!WHITELISTED_PLAYERS.contains(dealer.getName())) {
            dealer.sendMessage(ChatColor.RED + "You are not whitelisted to use aura dealer commands!");
            return false;
        }
        
        int currentPoints = dealerPoints.getOrDefault(dealer.getUniqueId(), 0);
        if (currentPoints + points > MAX_POINTS_PER_DEALER) {
            dealer.sendMessage(ChatColor.RED + "You have reached your maximum points limit for this session!");
            return false;
        }
        
        // Remove karma from target
        int currentKarma = KarmaManager.getKarma(target);
        int newKarma = Math.max(0, currentKarma - points);
        KarmaManager.setKarma(target, newKarma);
        
        // Update dealer's points
        dealerPoints.put(dealer.getUniqueId(), currentPoints + points);
        
        // Send messages
        dealer.sendMessage(ChatColor.RED + "🗑️ Successfully removed " + ChatColor.YELLOW + points + ChatColor.RED + " karma points from " + ChatColor.YELLOW + target.getName());
        dealer.sendMessage(ChatColor.GRAY + "📊 Session usage: " + (currentPoints + points) + "/" + MAX_POINTS_PER_DEALER + " points");

        target.sendMessage(ChatColor.RED + "⚠️ You lost " + ChatColor.YELLOW + points + ChatColor.RED + " karma points from " + ChatColor.YELLOW + dealer.getName());
        target.sendMessage(ChatColor.GRAY + "💡 Karma affects your afterlife effects and status!");
        
        return true;
    }
    
    // Aura wand methods removed - only commands are used now
    
    /**
     * Handle right-click interactions with aura wand
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Aura wand functionality removed - only commands are used now
        // This method is kept for potential future use
    }
    
    /**
     * Command executor for aura dealer commands
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
                            if (command.getName().equalsIgnoreCase("auragive")) {
                        if (!WHITELISTED_PLAYERS.contains(player.getName())) {
                            player.sendMessage(ChatColor.RED + "You are not whitelisted to use aura dealer commands!");
                            return true;
                        }
            
                                    if (args.length < 1) {
                            player.sendMessage(ChatColor.RED + "=== AURA DEALER HELP ===");
                            player.sendMessage(ChatColor.YELLOW + "Usage: /auragive <player> [points]");
                            player.sendMessage(ChatColor.WHITE + "");
                            player.sendMessage(ChatColor.GOLD + "Examples:");
                            player.sendMessage(ChatColor.YELLOW + "• /auragive PlayerName 25 - Give 25 karma points");
                            player.sendMessage(ChatColor.YELLOW + "• /auragive PlayerName - Give 10 karma points (default)");
                            player.sendMessage(ChatColor.WHITE + "");
                            player.sendMessage(ChatColor.GRAY + "Note: Points range 1-50, max 1000 per session");
                            return true;
                        }
            
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            
            int points = POINTS_PER_GIVE;
            if (args.length >= 2) {
                try {
                    points = Integer.parseInt(args[1]);
                    if (points <= 0 || points > 50) {
                        player.sendMessage(ChatColor.RED + "Points must be between 1 and 50!");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid points amount!");
                    return true;
                }
            }
            
                                    giveKarmaPoints(player, target, points);
                        return true;
                    }

                    if (command.getName().equalsIgnoreCase("auraremove")) {
                        if (!WHITELISTED_PLAYERS.contains(player.getName())) {
                            player.sendMessage(ChatColor.RED + "You are not whitelisted to use aura dealer commands!");
                            return true;
                        }

                        if (args.length < 1) {
                            player.sendMessage(ChatColor.RED + "=== AURA REMOVE HELP ===");
                            player.sendMessage(ChatColor.YELLOW + "Usage: /auraremove <player> [points]");
                            player.sendMessage(ChatColor.WHITE + "");
                            player.sendMessage(ChatColor.GOLD + "Examples:");
                            player.sendMessage(ChatColor.YELLOW + "• /auraremove PlayerName 25 - Remove 25 karma points");
                            player.sendMessage(ChatColor.YELLOW + "• /auraremove PlayerName - Remove 10 karma points (default)");
                            player.sendMessage(ChatColor.WHITE + "");
                            player.sendMessage(ChatColor.GRAY + "Note: Points range 1-50, max 1000 per session");
                            return true;
                        }

                        Player target = Bukkit.getPlayer(args[0]);
                        if (target == null) {
                            player.sendMessage(ChatColor.RED + "Player not found!");
                            return true;
                        }

                        int points = POINTS_PER_GIVE;
                        if (args.length >= 2) {
                            try {
                                points = Integer.parseInt(args[1]);
                                if (points <= 0 || points > 50) {
                                    player.sendMessage(ChatColor.RED + "Points must be between 1 and 50!");
                                    return true;
                                }
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "Invalid points amount!");
                                return true;
                            }
                        }

                        removeKarmaPoints(player, target, points);
                        return true;
                    }
        
                            if (command.getName().equalsIgnoreCase("auradealer")) {
                        if (!WHITELISTED_PLAYERS.contains(player.getName())) {
                            player.sendMessage(ChatColor.RED + "You are not whitelisted to use aura dealer commands!");
                            return true;
                        }
            
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /auradealer <add|remove> <player>");
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("add")) {
                addAuraDealer(target);
                player.sendMessage(ChatColor.GREEN + "Added " + target.getName() + " as an Aura Dealer!");
            } else if (args[0].equalsIgnoreCase("remove")) {
                removeAuraDealer(target);
                player.sendMessage(ChatColor.RED + "Removed " + target.getName() + " as an Aura Dealer!");
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /auradealer <add|remove> <player>");
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Get remaining points for a dealer
     */
    public static int getRemainingPoints(Player player) {
        if (!isAuraDealer(player)) return 0;
        return MAX_POINTS_PER_DEALER - dealerPoints.getOrDefault(player.getUniqueId(), 0);
    }
    
    /**
     * Reset dealer points (for new sessions)
     */
    public static void resetDealerPoints(Player player) {
        dealerPoints.put(player.getUniqueId(), 0);
    }
} 