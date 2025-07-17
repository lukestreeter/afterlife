package me.yodeling_goat.afterlifeplugin;

import me.yodeling_goat.afterlifeplugin.Events.OnPlayerDeath;
import me.yodeling_goat.afterlifeplugin.Events.OnPlayerDeath;
import org.bukkit.plugin.java.JavaPlugin;

public final class AfterLifePlugin extends JavaPlugin {

	private static AfterLifePlugin instance;

	@Override
	public void onEnable() {
		// Plugin startup logic
		instance = this;
		getServer().getPluginManager().registerEvents(new OnPlayerDeath(), this);
	}

	public static AfterLifePlugin getInstance() {
		return instance;
	}
}
