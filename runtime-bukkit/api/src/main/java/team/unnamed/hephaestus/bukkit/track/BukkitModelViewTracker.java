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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.bukkit.ModelView;
import team.unnamed.hephaestus.view.AbstractModelView;
import team.unnamed.hephaestus.view.track.ModelViewTracker;
import team.unnamed.hephaestus.view.track.ModelViewTrackingRule;

/**
 * Responsible for tracking model views on Bukkit servers, this means
 * to check players entering/leaving models vision range so that when
 * a player enters the vision range of a model, it is added as a viewer
 * (and the model is shown for them), and when a player leaves the vision
 * range of a model, it is removed as viewer (and the model is hidden for
 * them)
 *
 * @since 1.0.0
 * @see ModelViewTrackingRule
 */
public interface BukkitModelViewTracker extends ModelViewTracker<Player> {
    /**
     * Starts globally tracking the given {@code view}, this means
     * to handle the viewers of this view, adding and removing players
     * that get in/out of the view's view range.
     *
     * <p>Note that this method will use the given {@code base} entity
     * as the "wrapped" entity, or replaced entity.</p>
     *
     * @param view The view to track
     * @param base The base entity, the model view will be mounted there
     * @return True if the view is now being tracked, false if
     * it was already being tracked
     * @since 1.0.0
     */
    boolean startGlobalTrackingOn(final @NotNull ModelView view, final @NotNull Entity base);

    /**
     * Returns the view mounted on the given {@code base} entity
     * if there's any, or null if there's no view mounted on the
     * given entity
     *
     * @param base The base entity
     * @return The view mounted on the given entity, or null if
     * there's no view mounted on the given entity
     * @since 1.0.0
     * @see #startGlobalTrackingOn(ModelView, Entity)
     */
    @Nullable ModelView getViewOnBase(final @NotNull Entity base);


    /**
     * Starts tracking the given {@code view}, this means to
     * handle the viewers of this view, adding and removing
     * players that get in/out of the view's view range
     *
     * <p>The {@link ModelViewTrackingRule} trackingRule parameter
     * determines whether a new viewer candidate should be added
     * as a viewer</p>
     *
     * <p>Note that this method will use the given {@code base} entity
     * as the "wrapped" entity, or replaced entity.</p>
     *
     * @param view The view to track
     * @param base The base entity
     * @param trackingRule The tracking rule, filters viewers
     * @return True if the view is now being tracked, false if
     * it was already being tracked
     * @since 1.0.0
     */
    boolean startTrackingOn(final @NotNull AbstractModelView<Player> view, final @NotNull Entity base, final @NotNull ModelViewTrackingRule<Player> trackingRule);

    /**
     * Starts tracking the given {@code view}, this means to
     * handle the viewers of this view, adding and removing
     * players that get in/out of the view's view range
     *
     * <p>The {@link ModelViewTrackingRule} trackingRule parameter
     * determines whether a new viewer candidate should be added
     * as a viewer</p>
     *
     * @param view The view to track
     * @param trackingRule The tracking rule, filters viewers
     * @return True if the view is now being tracked, false if
     * it was already being tracked
     * @since 1.0.0
     */
    @Override
    boolean startTracking(final @NotNull AbstractModelView<Player> view, final @NotNull ModelViewTrackingRule<Player> trackingRule);

    /**
     * Starts tracking the given {@code view}, this means to
     * handle the viewers of this view, adding and removing
     * players that get in/out of the view's view range
     *
     * <p>This method is the same as calling {@link ModelViewTracker#startTracking}
     * with {@link ModelViewTrackingRule#all()} (which lets all
     * the players be added as viewers for this view)</p>
     *
     * @param view The view to track
     * @return True if the view is now being tracked, false if
     * it was already being tracked
     * @since 1.0.0
     */
    @Override
    default boolean startGlobalTracking(final @NotNull AbstractModelView<Player> view) {
        return startTracking(view, ModelViewTrackingRule.all());
    }
}
