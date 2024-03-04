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
package team.unnamed.hephaestus.view.track;

import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.view.AbstractModelView;

/**
 * Responsible for tracking model views, this means to check players
 * entering/leaving models vision range so that when a player enters
 * the vision range of a model, it is added as a viewer (and the model
 * is shown for them), and when a player leaves the vision range of
 * a model, it is removed as viewer (and the model is hidden for them)
 *
 * @param <TViewer> The viewer/player type, depends on platform
 * @since 1.0.0
 * @see ModelViewTrackingRule
 */
public interface ModelViewTracker<TViewer> {
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
    boolean startTracking(final @NotNull AbstractModelView<TViewer> view, final @NotNull ModelViewTrackingRule<TViewer> trackingRule);

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
    default boolean startGlobalTracking(final @NotNull AbstractModelView<TViewer> view) {
        return startTracking(view, ModelViewTrackingRule.all());
    }

    /**
     * Stops tracking the given {@code view} so no new viewers
     * are automatically added by this tracker (we stop checking
     * for players entering or leaving the view's view range)
     *
     * @param view The view to stop tracking
     * @return True if success, false if view is not being tracked
     * @since 1.0.0
     */
    boolean stopTracking(final @NotNull AbstractModelView<TViewer> view);
}
