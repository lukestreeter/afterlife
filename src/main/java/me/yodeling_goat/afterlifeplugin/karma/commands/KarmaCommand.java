package me.yodeling_goat.afterlifeplugin.karma.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.yodeling_goat.afterlifeplugin.karma.KarmaManager;
import me.yodeling_goat.afterlifeplugin.karma.events.KarmaChangeRequestEvent;

public class KarmaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("karma")) {
            return false;
        }



        if (args.length < 2) {
            sender.sendMessage("§cUsage: /karma <player> <amount> [reason]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + args[0]);
            return true;
        }

        try {
            int karmaChange = Integer.parseInt(args[1]);
            String reason = args.length > 2 ? String.join(" ", args).substring(args[0].length() + args[1].length() + 2) : "Command";
            
            // Fire karma change request event
            KarmaChangeRequestEvent event = new KarmaChangeRequestEvent(target, karmaChange, reason);
            Bukkit.getPluginManager().callEvent(event);
            
            if (!event.isCancelled()) {
                sender.sendMessage("§aKarma change applied to " + target.getName() + ": " + karmaChange);
            } else {
                sender.sendMessage("§cKarma change was cancelled");
            }
            
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid karma amount: " + args[1]);
        }

        return true;
    }
} 