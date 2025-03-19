package team.unnamed.hephaestus.bukkit.plugin.integration.mythicmobs;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestuser.integration.PluginIntegration;

public final class MythicMobsIntegration implements PluginIntegration {
    @Override
    public @NotNull String plugin() {
        return "MythicMobs";
    }

    @Override
    public void enable(final @NotNull Plugin plugin) {

    }
}
