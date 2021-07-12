package team.unnamed.hephaestus.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoinListener implements Listener {

	// TODO: This is temporal
	private static final String RESOURCE_PACK_URL
			= "https://cdn.discordapp.com/attachments/735264804134191114/864028599887724554/hephaestus-generated.zip";

	private final Plugin plugin;

	public PlayerJoinListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage("§d" + player.getName() + "§f joined");
		player.sendMessage(
				"§d-----------------------------------------------\n"
				+ "§fWelcome to the §dUnnamed Team §fTest Server\n"
				+ "Feel free to §dinvite your friends to test our\n"
				+ "projects...\n"
				+ "§d-----------------------------------------------"
		);
		Bukkit.getScheduler().runTaskLater(
				plugin,
				() -> {
					player.setResourcePack(RESOURCE_PACK_URL);
				},
				20L
		);
	}

}
