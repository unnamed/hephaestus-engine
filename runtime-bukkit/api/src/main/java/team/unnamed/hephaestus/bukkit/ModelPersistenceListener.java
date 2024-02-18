/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.hephaestus.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.bukkit.track.ModelViewPersistenceHandler;

import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

public final class ModelPersistenceListener implements Listener {
    private final Plugin plugin;
    private final BukkitModelEngine engine;
    private final ModelViewPersistenceHandler persistenceHandler;

    public ModelPersistenceListener(final @NotNull Plugin plugin, final @NotNull BukkitModelEngine engine, final @NotNull ModelViewPersistenceHandler persistenceHandler) {
        this.plugin = requireNonNull(plugin, "plugin");
        this.engine = requireNonNull(engine, "engine");
        this.persistenceHandler = requireNonNull(persistenceHandler, "persistenceHandler");

        postRegister();
    }

    private void postRegister() {
        for (final var world : Bukkit.getWorlds()) {
            for (final var entity : world.getEntities()) {
                // Check persistence
                plugin.getLogger().info("Checking entity before EntitiesLoadEvent " + entity.getUniqueId() + "(" + entity.getType() + ")");
                final var view = engine.tracker().getViewOnBase(entity);
                if (view != null) {
                    // This entity already has a model view, this means it was already loaded
                    continue;
                }
                persistenceHandler.determineModel(entity).whenComplete((model, err) -> {
                    if (err != null) {
                        plugin.getLogger().log(Level.WARNING, "Unhandled exception while determining model for entity " + entity, err);
                    } else if (model != null) {
                        if (!entity.isValid()) {
                            plugin.getLogger().warning("Entity " + entity + " is not valid, skipping model view creation");
                            return;
                        }
                        // This entity has a model!
                        engine.tracker().startGlobalTrackingOn(engine.createView(model, entity.getLocation()), entity);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onEntitiesLoad(final @NotNull EntitiesLoadEvent event) {
        for (final var entity : event.getEntities()) {
            persistenceHandler.determineModel(entity).whenComplete((model, err) -> {
                if (err != null) {
                    plugin.getLogger().log(Level.WARNING, "Unhandled exception while determining model for entity " + entity, err);
                } else if (model != null) {
                    if (!entity.isValid()) {
                        plugin.getLogger().warning("Entity " + entity + " is not valid, skipping model view creation");
                        return;
                    }
                    // This entity has a model!
                    final var view = engine.createView(model, entity.getLocation());
                    engine.tracker().startGlobalTrackingOn(view, entity);
                }
            });
        }
    }

    @EventHandler
    public void onEntitiesUnload(final @NotNull EntitiesUnloadEvent event) {
        for (final var entity : event.getEntities()) {
            final var view = engine.tracker().getViewOnBase(entity);
            if (view != null) {
                persistenceHandler.saveModel(entity, view);
            }
        }
    }

    public void onPluginDisable() { // Called by BukkitModelEngine#close()
        for (final var world : Bukkit.getWorlds()) {
            for (final var entity : world.getEntities()) {
                final var view = engine.tracker().getViewOnBase(entity);
                if (view != null) {
                    persistenceHandler.saveModel(entity, view);
                }
            }
        }
    }
}
