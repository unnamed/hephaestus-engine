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
package team.unnamed.hephaestus.bukkit.v1_19_R3;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.bukkit.ModelPersistenceListener;
import team.unnamed.hephaestus.bukkit.ModelView;
import team.unnamed.hephaestus.bukkit.track.BukkitModelViewTracker;
import team.unnamed.hephaestus.bukkit.track.ModelViewPersistenceHandler;

import static java.util.Objects.requireNonNull;

final class BukkitModelEngine_v1_19_R3Impl implements BukkitModelEngine_v1_19_R3 {
    private final Plugin plugin;
    private final ModelViewPersistenceHandler persistenceHandler;
    private final ModelPersistenceListener persistenceListener;

    BukkitModelEngine_v1_19_R3Impl(final @NotNull Plugin plugin, final @NotNull ModelViewPersistenceHandler persistenceHandler) {
        this.plugin = requireNonNull(plugin, "plugin");
        this.persistenceHandler = requireNonNull(persistenceHandler, "persistenceHandler");

        Bukkit.getPluginManager().registerEvents(new ModelInteractListener(plugin), plugin);
        this.persistenceListener = new ModelPersistenceListener(plugin, this, persistenceHandler);
        Bukkit.getPluginManager().registerEvents(persistenceListener, plugin);;
    }

    @Override
    public @NotNull BukkitModelViewTracker tracker() {
        return BukkitModelViewTrackerImpl.INSTANCE;
    }

    @Override
    public @NotNull ModelViewPersistenceHandler persistence() {
        return persistenceHandler;
    }

    @Override
    public @NotNull ModelView createViewAndTrack(Model model, Location location, CreatureSpawnEvent.SpawnReason reason) {
        final var view = createView(model, location);
        tracker().startGlobalTracking(view);
        return view;
    }

    @Override
    public @NotNull ModelView createView(Model model, Location location) {
        return new ModelViewImpl(plugin, model, location, 1.0f);
    }

    @Override
    public void close() {
        persistenceListener.onPluginDisable();
    }
}
