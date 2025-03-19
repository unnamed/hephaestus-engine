package team.unnamed.hephaestus.bukkit.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.bukkit.plugin.HephaestusPlugin;
import team.unnamed.hephaestus.bukkit.plugin.Hephaestuser;
import team.unnamed.hephaestus.bukkit.plugin.modifier.OnGroundBoneModifier;

import static java.util.Objects.requireNonNull;

public final class DisguisedPlayerJoinListener implements Listener {
    private final Hephaestuser hephaestuser;

    public DisguisedPlayerJoinListener(final @NotNull Hephaestuser hephaestuser) {
        this.hephaestuser = requireNonNull(hephaestuser, "hephaestuser");
    }

    @EventHandler
    public void onJoin(final @NotNull PlayerJoinEvent event) {
        final var player = event.getPlayer();
        final var data = player.getPersistentDataContainer();
        final var disguisedAs = data.get(HephaestusPlugin.DISGUISED_AS_KEY, PersistentDataType.STRING);

        if (disguisedAs != null) {
            final var model = hephaestuser.registry().model(disguisedAs);
            if (model == null) {
                player.sendPlainMessage("You were disguised as a model that doesn't exist anymore, undisguising you...");
                data.remove(HephaestusPlugin.DISGUISED_AS_KEY);
                return;
            }

            // (delay a bit since the server needs to add the player
            //  to the player list before we replace their tracker)
            Bukkit.getScheduler().runTaskLater(hephaestuser.plugin(), () -> {
                // Spawn the model view on the player
                final var view = hephaestuser.engine().spawn(model, player);

                // On ground
                new OnGroundBoneModifier(player).apply(view);

                // Register
                hephaestuser.registry().view(view);
            }, 2L);
        }
    }
}
