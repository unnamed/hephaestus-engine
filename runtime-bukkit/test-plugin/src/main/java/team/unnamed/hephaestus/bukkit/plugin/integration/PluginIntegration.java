package team.unnamed.hephaestus.bukkit.plugin.integration;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a plugin integration, provides the integrated
 * plugin name and the plugin integration logic.
 */
public interface PluginIntegration {
    /**
     * Returns the plugin name for this plugin integration,
     * the name must be the exact same as the plugin's name
     * in their {@code plugin.yml} file.
     *
     * <p>The integration is eligible if and only if the plugin
     * is currently enabled on the server.</p>
     *
     * @return The plugin name
     */
    @NotNull String plugin();

    /**
     * Executes the plugin integration logic, such as registering
     * listeners.
     *
     * @param plugin The plugin which is being integrated
     */
    void enable(final @NotNull Plugin plugin);
}