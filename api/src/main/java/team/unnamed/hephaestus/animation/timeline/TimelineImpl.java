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
import team.unnamed.hephaestus.animation.interpolation.KeyFrameInterpolator;
import team.unnamed.hephaestus.animation.timeline.playhead.Playhead;

import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Objects.requireNonNull;

final class TimelineImpl<T> implements Timeline<T> {

    private final T initialValue;
    private final KeyFrameInterpolator<T> defaultInterpolator;
    private final SortedSet<KeyFrame<T>> keyFrames;

    TimelineImpl(T initialValue, KeyFrameInterpolator<T> defaultInterpolator, SortedSet<KeyFrame<T>> keyFrames) {
        this.initialValue = requireNonNull(initialValue, "initial");
        this.defaultInterpolator = requireNonNull(defaultInterpolator, "defaultInterpolator");
        this.keyFrames = requireNonNull(keyFrames, "keyFrames");
    }

    @Override
    public @NotNull T initial() {
        return initialValue;
    }

    @Override
    public @NotNull KeyFrameInterpolator<T> defaultInterpolator() {
        return defaultInterpolator;
    }

    @Override
    public @NotNull @Unmodifiable SortedSet<KeyFrame<T>> keyFrames() {
        return keyFrames;
    }

    @Override
    public Playhead<T> createPlayhead() {
        return Playhead.playhead(this);
    }

    @Override
    public String toString() {
        return "TimelineImpl{" +
                "initialValue=" + initialValue +
                ", defaultInterpolator=" + defaultInterpolator +
                ", keyFrames=" + keyFrames +
                '}';
    }

    static final class BuilderImpl<T> implements Builder<T> {

        private T initialValue;
        private KeyFrameInterpolator<T> interpolator;
        private final SortedSet<KeyFrame<T>> keyFrames = new TreeSet<>();

        @Override
        public Builder<T> initial(T value) {
            this.initialValue = requireNonNull(value, "value");
            return this;
        }

        @Override
        public Builder<T> defaultInterpolator(KeyFrameInterpolator<T> interpolator) {
            this.interpolator = requireNonNull(interpolator, "interpolator");
            return this;
        }

        @Override
        public Builder<T> keyFrame(KeyFrame<T> keyFrame) {
            requireNonNull(keyFrame, "keyFrame");
            keyFrames.add(keyFrame);
            return this;
        }

        @Override
        public Timeline<T> build() {
            return new TimelineImpl<>(initialValue, interpolator, keyFrames);
        }
    }
}
