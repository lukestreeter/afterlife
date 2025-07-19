package me.yodeling_goat.afterlifeplugin.impersonation.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import me.yodeling_goat.afterlifeplugin.impersonation.ImpersonationManager;

public class ImpersonationCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (command.getName().equalsIgnoreCase("unimpersonate")) {
            if (args.length == 0) {
                // Player wants to stop impersonating
                if (ImpersonationManager.isPlayerImpersonating(player)) {
                    ImpersonationManager.unimpersonateEntity(player);
                } else {
                    player.sendMessage("§cYou are not currently impersonating any animal!");
                }
            } else {
                player.sendMessage("§cUsage: /unimpersonate");
            }
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("impersonate")) {
            if (args.length == 0) {
                player.sendMessage("§eTo impersonate an animal:");
                player.sendMessage("§7- You must be in the afterlife");
                player.sendMessage("§7- Right-click on an animal with an empty hand");
                player.sendMessage("§7- The animal will be removed and you'll take its form");
                player.sendMessage("§7- Use /unimpersonate to return to normal");
            } else {
                player.sendMessage("§cUsage: /impersonate");
            }
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("debugimpersonate")) {
            if (args.length == 0) {
                player.sendMessage("§eDebug information:");
                player.sendMessage("§7- Is impersonating: " + ImpersonationManager.isPlayerImpersonating(player));
                player.sendMessage("§7- Has metadata: " + player.hasMetadata("animal_disguise"));
                if (player.hasMetadata("animal_disguise")) {
                    player.sendMessage("§7- Metadata value: " + player.getMetadata("animal_disguise").get(0).value());
                }
                
                // Count entities with animal_disguise tag
                int count = 0;
                for (Entity entity : player.getWorld().getEntities()) {
                    if (entity.getScoreboardTags().contains("animal_disguise")) {
                        count++;
                        player.sendMessage("§7- Found animal disguise entity: " + entity.getType() + " at " + entity.getLocation());
                    }
                }
                player.sendMessage("§7- Total animal disguise entities: " + count);
            }
            return true;
        }
        
        return false;
    }
} 