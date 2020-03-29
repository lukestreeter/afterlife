package me.yodeling_goat.afterlifeplugin;

import me.yodeling_goat.afterlifeplugin.Events.OnPlayerDeath;
import me.yodeling_goat.afterlifeplugin.Events.OnPlayerDeath;
import org.bukkit.plugin.java.JavaPlugin;

public final class AfterLifePlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		// Plugin startup logic
		getServer().getPluginManager().registerEvents(new OnPlayerDeath(), this);
	}
}
