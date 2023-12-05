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

import net.kyori.examination.Examinable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.hephaestus.animation.interpolation.Interpolator;
import team.unnamed.hephaestus.animation.interpolation.KeyFrameInterpolator;
import team.unnamed.hephaestus.animation.timeline.playhead.Playhead;

import java.util.SortedSet;

/**
 * Represents a timeline. A timeline is an ordered collection
 * of keyframes, where a keyframe is a state at a given time.
 *
 * <p>Keyframes may have more options such as the interpolation
 * function to use when tick-iterating over them.</p>
 *
 * @param <T> The type of values in this timeline
 */
public interface Timeline<T> extends Examinable {

    static <T> Builder<T> timeline() {
        return new TimelineImpl.BuilderImpl<>();
    }

    @NotNull T initial();

    @NotNull KeyFrameInterpolator<T> defaultInterpolator();

    @NotNull @Unmodifiable SortedSet<KeyFrame<T>> keyFrames();

    @Contract("-> new")
    Playhead<T> createPlayhead();

    interface Builder<T> {

        Builder<T> initial(T value);

        Builder<T> defaultInterpolator(KeyFrameInterpolator<T> interpolator);

        Builder<T> keyFrame(KeyFrame<T> keyFrame);

        @Contract("_, _ -> this")
        default @NotNull Builder<T> keyFrame(final int time, final @NotNull T value) {
            return keyFrame(new KeyFrame<>(time, value, null));
        }

        @Contract("_, _, _ -> this")
        default @NotNull Builder<T> keyFrame(final int time, final @NotNull T value, final @NotNull KeyFrameInterpolator<T> interpolator) {
            return keyFrame(new KeyFrame<>(time, value, interpolator));
        }

        Timeline<T> build();

    }

}
