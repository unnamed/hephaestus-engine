package team.unnamed.hephaestus.bukkit.plugin;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.bukkit.BukkitModelEngine;
import team.unnamed.hephaestus.bukkit.plugin.registry.ModelRegistry;
import team.unnamed.hephaestus.bukkit.plugin.registry.ModelViewPersistenceHandlerImpl;
import team.unnamed.hephaestus.bukkit.v1_21_4.BukkitModelEngine_v1_21_4;

import static java.util.Objects.requireNonNull;

public final class Hephaestuser {
    private final Plugin plugin;
    private final ModelRegistry registry;
    private final BukkitModelEngine engine;

    public Hephaestuser(final @NotNull Plugin plugin) {
        this.plugin = requireNonNull(plugin, "plugin");
        this.registry = new ModelRegistry();
        this.engine = BukkitModelEngine_v1_21_4.create(plugin, new ModelViewPersistenceHandlerImpl(registry));
    }

    public @NotNull Plugin plugin() {
        return plugin;
    }

    public @NotNull ModelRegistry registry() {
        return registry;
    }

    public @NotNull BukkitModelEngine engine() {
        return engine;
    }

    void close() {
        if (engine != null) {
            engine.close();
        }
    }
}
