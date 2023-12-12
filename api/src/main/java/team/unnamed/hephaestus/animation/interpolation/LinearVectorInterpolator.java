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
package team.unnamed.hephaestus.animation.interpolation;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;

final class LinearVectorInterpolator implements Interpolator<Vector3Float> {
    static final Interpolator<Vector3Float> INSTANCE = new LinearVectorInterpolator();

    private LinearVectorInterpolator() {
    }

    @Override
    public @NotNull Interpolation<Vector3Float> interpolation(final @NotNull Vector3Float from, final @NotNull Vector3Float to) {
        return new LinearVectorInterpolation(from, to);
    }

    @Override
    public @NotNull KeyFrameInterpolator<Vector3Float> combineRight(final @NotNull KeyFrameInterpolator<Vector3Float> right) {
        if (right instanceof LinearVectorInterpolator || right instanceof StepVectorInterpolator) {
            // only keep linear vector interpolation if the right
            // interpolator is also linear, or step
            return this;
        } else {
            return right;
        }
    }

    static final class LinearVectorInterpolation implements Interpolation<Vector3Float> {
        private final Vector3Float from;
        private final Vector3Float to;

        LinearVectorInterpolation(
                final @NotNull Vector3Float from,
                final @NotNull Vector3Float to
        ) {
            this.from = from;
            this.to = to;
        }

        @Override
        public @NotNull Vector3Float interpolate(final double progress) {
            final double complement = 1 - progress;
            // Vs = V0(1 - s) + Vf * s
            return new Vector3Float(
                    (float) (from.x() * complement + to.x() * progress),
                    (float) (from.y() * complement + to.y() * progress),
                    (float) (from.z() * complement + to.z() * progress)
            );
        }
    }
}
