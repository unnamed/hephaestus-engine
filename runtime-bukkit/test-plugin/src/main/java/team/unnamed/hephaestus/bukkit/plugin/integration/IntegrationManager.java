package team.unnamed.hephaestus.bukkit.plugin.integration;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

/**
 * Manages the integration of this plugin with other plugins,
 * allowing to execute the integration logic when the plugins
 * are enabled.
 */
public final class IntegrationManager {
    private final Plugin plugin;

    private final Map<String, PluginIntegration> integrations = new LinkedHashMap<>();

    private IntegrationManager(final @NotNull Plugin plugin) {
        this.plugin = requireNonNull(plugin, "plugin");
    }

    /**
     * Registers a new plugin integration.
     *
     * @return This manager, for chaining
     */
    public @NotNull IntegrationManager register(final @NotNull PluginIntegration integration) {
        requireNonNull(integration, "integration");
        integrations.put(integration.plugin(), integration);
        return this;
    }

    /**
     * Finds plugins for all the registered integrations,
     * and enables them if the plugin is enabled in this server.
     *
     * @return The enabled integrations
     */
    public @NotNull Set<PluginIntegration> check() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        final Set<PluginIntegration> enabled = new HashSet<>();

        for (final PluginIntegration integration : integrations.values()) {
            final String pluginName = integration.plugin();
            final Plugin integrationPlugin = pluginManager.getPlugin(pluginName);

            if (integrationPlugin == null || !integrationPlugin.isEnabled()) {
                continue;
            }

            try {
                integration.enable(integrationPlugin);
                enabled.add(integration);
            } catch (final Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to enable integration with '" + integration.plugin() + "'", e);
            }
        }
        return enabled;
    }

    /**
     * Creates a new integration manager for the given plugin, which is
     * used for more information, such as logs.
     *
     * @param plugin The plugin
     * @return The integration manager
     */
    public static @NotNull IntegrationManager integrationManager(final @NotNull Plugin plugin) {
        return new IntegrationManager(plugin);
    }
}