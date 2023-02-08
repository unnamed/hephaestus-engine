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
package team.unnamed.hephaestus;

import team.unnamed.hephaestus.view.BaseModelView;
import team.unnamed.hephaestus.view.track.ModelViewTracker;

/**
 * The model engine facade
 *
 * @param <TViewer> The model viewer/player type, depends on platform
 * @param <TLocation> The location type, depends on platform
 * @since 1.0.0
 */
public interface ModelEngine<TViewer, TLocation> {

    /**
     * Returns the model view tracker for this model engine
     *
     * @return The model view tracker
     */
    ModelViewTracker<TViewer> tracker();

    /**
     * Creates a new view for the given {@code model} at the
     * given {@code location}, note that the returned view
     * <strong>is not tracked</strong>, this means that it will not
     * be automatically shown when someone enters its vision range
     *
     * <p>To create a tracked view use the {@link ModelEngine#createViewAndTrack}
     * method, or use the tracker that the {@link ModelEngine#tracker()}
     * method returns to start tracking the view that this method returns</p>
     *
     * @param model The model that the created view will represent
     * @param location The view location
     * @return The created model view
     */
    BaseModelView<TViewer> createView(Model model, TLocation location);

    /**
     * Creates a new view for the given {@code model} at the
     * given {@code location}, the returned view <strong>is tracked</strong>,
     * this means that it will be automatically shown/hidden when
     * someone enters/leaves its vision range
     *
     * <p>This method is equivalent to use {@link ModelEngine#createView} and
     * then invoking {@link ModelViewTracker#startGlobalTracking} on the created
     * view</p>
     *
     * @param model The model that the created view will represent
     * @param location The view location
     * @return The created model view (tracked)
     */
    default BaseModelView<TViewer> createViewAndTrack(Model model, TLocation location) {
        BaseModelView<TViewer> view = createView(model, location);
        tracker().startGlobalTracking(view);
        return view;
    }

}