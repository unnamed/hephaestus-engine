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
package team.unnamed.hephaestus.minestomce;

import net.minestom.server.entity.Player;
import team.unnamed.hephaestus.view.BaseModelView;
import team.unnamed.hephaestus.view.track.ModelViewTracker;
import team.unnamed.hephaestus.view.track.ModelViewTrackingRule;

final class MinestomModelViewTracker implements ModelViewTracker<Player> {

    static final MinestomModelViewTracker INSTANCE = new MinestomModelViewTracker();

    private MinestomModelViewTracker() {
    }

    @Override
    @SuppressWarnings("UnstableApiUsage") // TODO: Remove when stable
    public boolean startTracking(BaseModelView<Player> view, ModelViewTrackingRule<Player> trackingRule) {
        ModelEntity entity = ensureModelEntity(view);
        entity.updateViewableRule(player -> trackingRule.shouldView(view, player));

        boolean autoViewable = entity.isAutoViewable();
        if (autoViewable) {
            return false;
        } else {
            entity.setAutoViewable(true);
            return true;
        }
    }

    @Override
    public boolean stopTracking(BaseModelView<Player> view) {
        ModelEntity entity = ensureModelEntity(view);
        boolean autoViewable = entity.isAutoViewable();
        if (autoViewable) {
            entity.setAutoViewable(false);
            return true;
        } else {
            return false;
        }
    }

    private static ModelEntity ensureModelEntity(BaseModelView<Player> view) {
        if (view instanceof ModelEntity entity) {
            return entity;
        } else {
            throw new IllegalArgumentException("Provided model view is not" +
                    " compatible with this tracker implementation");
        }
    }

}
