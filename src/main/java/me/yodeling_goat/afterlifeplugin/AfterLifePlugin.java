package me.yodeling_goat.afterlifeplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

// Karma system
import me.yodeling_goat.afterlifeplugin.karma.KarmaManager;
// Afterlife system
import me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeEffectsListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeRestrictionListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.PlayerDeathListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.EntityDeathListener;
import me.yodeling_goat.afterlifeplugin.afterlife.listeners.MobSpectateListener;
import me.yodeling_goat.afterlifeplugin.afterlife.util.MobMorphManager;
// Grave system
import me.yodeling_goat.afterlifeplugin.grave.GraveManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

public class AfterLifePlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("AfterLifePlugin is starting up...");
        KarmaManager karmaManager = new KarmaManager();
        GraveManager graveManager = new GraveManager();
        MobMorphManager morphManager = new MobMorphManager(this);
        Bukkit.getPluginManager().registerEvents(karmaManager, this);
        Bukkit.getPluginManager().registerEvents(graveManager, this);
        Bukkit.getPluginManager().registerEvents(new AfterlifeEffectsListener(), this);
        Bukkit.getPluginManager().registerEvents(new AfterlifeRestrictionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new MobSpectateListener(), this);
        Bukkit.getPluginManager().registerEvents(morphManager, this);
        Bukkit.getPluginManager().registerEvents(this, this);
        
        // Register commands
        getCommand("exitafterlife").setExecutor(new ExitAfterlifeCommand());
        getCommand("checkafterlife").setExecutor(new CheckAfterlifeCommand());
        getCommand("forceexitafterlife").setExecutor(new ForceExitAfterlifeCommand());
        getCommand("morphdebug").setExecutor(new MorphDebugCommand());
        getCommand("testmorph").setExecutor(new TestMorphCommand());
        getCommand("enterafterlife").setExecutor(new EnterAfterlifeCommand());
        
        getLogger().info("AfterLifePlugin has been enabled!");
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        KarmaManager.initializeKarmaDisplay(player);
        AfterlifeManager.initializeAfterlifeState(player);
    }
    @Override
    public void onDisable() {
        getLogger().info("AfterLifePlugin is shutting down...");
        getLogger().info("AfterLifePlugin has been disabled!");
    }
}

class ExitAfterlifeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!AfterlifeManager.isInAfterlife(player)) {
            player.sendMessage(ChatColor.RED + "You are not in the afterlife!");
            return true;
        }
        
        AfterlifeManager.removeFromAfterlife(player);
        return true;
    }
}

class CheckAfterlifeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (AfterlifeManager.isInAfterlife(player)) {
            player.sendMessage(ChatColor.YELLOW + "You are currently in the afterlife.");
        } else {
            player.sendMessage(ChatColor.GREEN + "You are not in the afterlife.");
        }
        
        return true;
    }
}

class ForceExitAfterlifeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("afterlife.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /forceexitafterlife <player>");
            return true;
        }
        
        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);
        
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player " + playerName + " is not online!");
            return true;
        }
        
        if (!AfterlifeManager.isInAfterlife(targetPlayer)) {
            sender.sendMessage(ChatColor.RED + "Player " + playerName + " is not in the afterlife!");
            return true;
        }
        
        AfterlifeManager.removeFromAfterlife(targetPlayer);
        sender.sendMessage(ChatColor.GREEN + "Successfully removed " + playerName + " from the afterlife!");
        
        return true;
    }
}

class MorphDebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        sender.sendMessage(ChatColor.GOLD + "=== Morph Debug Info ===");
        sender.sendMessage(ChatColor.YELLOW + "In Afterlife: " + ChatColor.WHITE + AfterlifeManager.isInAfterlife(player));
        sender.sendMessage(ChatColor.YELLOW + "Is Morphed: " + ChatColor.WHITE + MobMorphManager.isMorphed(player));
        sender.sendMessage(ChatColor.YELLOW + "Game Mode: " + ChatColor.WHITE + player.getGameMode());
        sender.sendMessage(ChatColor.YELLOW + "Can Fly: " + ChatColor.WHITE + player.getAllowFlight());
        sender.sendMessage(ChatColor.YELLOW + "Is Flying: " + ChatColor.WHITE + player.isFlying());
        
        if (MobMorphManager.isMorphed(player)) {
            Entity morphedEntity = MobMorphManager.getMorphedEntity(player);
            sender.sendMessage(ChatColor.YELLOW + "Morphed Entity: " + ChatColor.WHITE + (morphedEntity != null ? morphedEntity.getName() : "null"));
            if (morphedEntity != null) {
                sender.sendMessage(ChatColor.YELLOW + "Entity Location: " + ChatColor.WHITE + morphedEntity.getLocation());
                sender.sendMessage(ChatColor.YELLOW + "Entity AI: " + ChatColor.WHITE + (morphedEntity instanceof Mob ? ((Mob) morphedEntity).hasAI() : "N/A"));
            }
        }
        
        return true;
    }
}

class TestMorphCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Find a nearby mob to morph into
        LivingEntity nearestMob = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                double distance = player.getLocation().distance(entity.getLocation());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestMob = (LivingEntity) entity;
                }
            }
        }
        
        if (nearestMob == null) {
            sender.sendMessage(ChatColor.RED + "No mobs found within 10 blocks!");
            return true;
        }
        
        sender.sendMessage(ChatColor.GREEN + "Attempting to morph into " + nearestMob.getName() + "...");
        MobMorphManager.morphIntoMob(player, nearestMob);
        
        return true;
    }
}

class EnterAfterlifeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("afterlife.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (AfterlifeManager.isInAfterlife(player)) {
            sender.sendMessage(ChatColor.RED + "You are already in the afterlife!");
            return true;
        }
        
        AfterlifeManager.sendToAfterlife(player);
        AfterlifeManager.applyAllAfterlifeEffects(player);
        sender.sendMessage(ChatColor.GREEN + "You have been sent to the afterlife for testing!");
        
        return true;
    }
}
