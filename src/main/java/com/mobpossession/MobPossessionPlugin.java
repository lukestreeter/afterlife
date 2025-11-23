package com.mobpossession;

import org.bukkit.plugin.java.JavaPlugin;

public class MobPossessionPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new MobSpectateListener(), this);
        getLogger().info("MobPossession enabled!");
    }
} 