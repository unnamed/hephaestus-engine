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
package team.unnamed.hephaestus.bukkit.track;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.bukkit.ModelView;

import java.util.concurrent.CompletableFuture;

/**
 * Responsible for managing the persistence of model views
 * that are being tracked by a {@link BukkitModelViewTracker}.
 *
 * @since 1.0.0
 */
public interface ModelViewPersistenceHandler {
    /**
     * Determines the model of the given {@code entity}, if
     * the entity doesn't have a model, this method should
     * return null.
     *
     * <p>Note that the engine doesn't have any previous
     * information about the provided entity, so calls to
     * methods like {@link BukkitModelViewTracker#getViewOnBase(Entity)}
     * will not work.</p>
     *
     * @param entity The entity to get the model from
     * @return The model of the entity, or null if the entity
     * doesn't have a model
     * @since 1.0.0
     */
    @NotNull CompletableFuture<Model> determineModel(final @NotNull Entity entity);

    void saveModel(final @NotNull Entity entity, final @NotNull ModelView view);

    default void onSyntheticModelViewBaseCreation(final @NotNull Entity entity, final @NotNull ModelView view) {
    }

    default void onDisableHandleSyntheticModelViewBase(final @NotNull Entity entity, final @NotNull ModelView view) {

    }
}
