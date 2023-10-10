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
package team.unnamed.hephaestus.animation.timeline;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.hephaestus.animation.interpolation.Interpolator;

import java.util.Comparator;
import java.util.List;

final class TimelineImpl<T> implements Timeline<T> {

    private final T initialValue;
    private final Interpolator<T> defaultInterpolator;
    private final List<KeyFrame<T>> keyFrames;

    TimelineImpl(T initialValue, Interpolator<T> defaultInterpolator, List<KeyFrame<T>> keyFrames) {
        this.initialValue = initialValue;
        this.defaultInterpolator = defaultInterpolator;
        this.keyFrames = keyFrames;
        keyFrames.sort(Comparator.comparing(KeyFrame::time));
    }

    @Override
    public @NotNull T initial() {
        return initialValue;
    }

    @Override
    public @NotNull Interpolator<T> defaultInterpolator() {
        return defaultInterpolator;
    }

    @Override
    public @NotNull @Unmodifiable List<KeyFrame<T>> keyFrames() {
        return keyFrames;
    }

    @Override
    public Playhead<T> createPlayhead() {
        return null;
    }

    static final class BuilderImpl<T> implements Builder<T> {

        @Override
        public Builder<T> initial(T value) {
            return null;
        }

        @Override
        public Builder<T> defaultInterpolator(Interpolator<T> interpolator) {
            return null;
        }

        @Override
        public Builder<T> keyFrame(KeyFrame<T> keyFrame) {
            return null;
        }

        @Override
        public Timeline<T> build() {
            return null;
        }
    }
}
