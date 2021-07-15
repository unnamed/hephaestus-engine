package team.unnamed.hephaestus.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class ResourcePackApplyListener implements Listener {

    private final Plugin plugin;
    private final String resourcePackUrl;
    private final byte[] resourcePackHash;

    public ResourcePackApplyListener(
            Plugin plugin,
            String resourcePackUrl,
            byte[] resourcePackHash
    ) {
        this.plugin = plugin;
        this.resourcePackUrl = resourcePackUrl;
        this.resourcePackHash = resourcePackHash;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> {
                    if (player.isOnline()) {
                        player.setResourcePack(resourcePackUrl, resourcePackHash);
                    }
                },
                20L
        );
    }

}
