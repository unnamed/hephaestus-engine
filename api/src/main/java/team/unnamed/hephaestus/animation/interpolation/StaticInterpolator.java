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

final class StaticInterpolator<T> implements Interpolator<T> {

    private final T empty;

    StaticInterpolator(T empty) {
        this.empty = empty;
    }

    @Override
    public @NotNull Interpolation<T> interpolation(@NotNull T from, @NotNull T to) {
        return new StaticInterpolation<>(empty, from, to);
    }

    static final class StaticInterpolation<T> implements Interpolation<T> {

        private final T empty;
        private final T from;
        private final T to;

        StaticInterpolation(
                final @NotNull T empty,
                final @NotNull T from,
                final @NotNull T to
        ) {
            this.empty = empty;
            this.from = from;
            this.to = to;
        }

        @Override
        public @NotNull T from() {
            return from;
        }

        @Override
        public @NotNull T to() {
            return to;
        }

        @Override
        public @NotNull T interpolate(final double progress) {
            if (progress == 0) {
                return to;
            }

            return empty;
        }
    }
}
