/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
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

import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.view.BaseModelView;

/**
 * The hephaestus model engine abstraction for
 * Bukkit-based server implementations such as
 * Spigot and Paper
 *
 * @since 1.0.0
 */
public interface ModelEngine {

    /**
     * Spawns the given {@link Model} model
     * instance at the given world location
     *
     * @param model    The spawned model
     * @param location The model entity location
     * @param reason The model entity spawn reason
     * @return The spawned model entity
     * @since 1.0.0
     */
    ModelEntity spawn(Model model, Location location, CreatureSpawnEvent.SpawnReason reason);

    /**
     * Spawns a {@link Model} instance at the
     * given location
     *
     * @param model The spawned model
     * @param location The model entity location
     * @return The created model entity
     * @since 1.0.0
     */
    default ModelEntity spawn(Model model, Location location) {
        return spawn(model, location, CreatureSpawnEvent.SpawnReason.DEFAULT);
    }

}