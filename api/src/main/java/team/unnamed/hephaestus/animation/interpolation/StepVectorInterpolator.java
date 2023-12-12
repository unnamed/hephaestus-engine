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

import static java.util.Objects.requireNonNull;

final class StepVectorInterpolator implements Interpolator<Vector3Float> {
    static final Interpolator<Vector3Float> INSTANCE = new StepVectorInterpolator();

    private StepVectorInterpolator() {
    }

    @Override
    public @NotNull Interpolation<Vector3Float> interpolation(final @NotNull Vector3Float from, final @NotNull Vector3Float to) {
        return new StepVectorInterpolation(from, to);
    }

    @Override
    public @NotNull KeyFrameInterpolator<Vector3Float> combineRight(final @NotNull KeyFrameInterpolator<Vector3Float> right) {
        requireNonNull(right, "right");
        // no matter what "right" interpolator is, combining a step
        // interpolator and any other interpolator, results in a step
        // interpolation
        return this;
    }

    static final class StepVectorInterpolation implements Interpolation<Vector3Float> {
        private final Vector3Float from;
        private final Vector3Float to;

        StepVectorInterpolation(
            final @NotNull Vector3Float from,
            final @NotNull Vector3Float to
        ) {
            this.from = requireNonNull(from, "from");
            this.to = requireNonNull(to, "to");
        }

        @Override
        public @NotNull Vector3Float interpolate(final double progress) {
            return progress < 1 ? from : to;
        }
    }
}
