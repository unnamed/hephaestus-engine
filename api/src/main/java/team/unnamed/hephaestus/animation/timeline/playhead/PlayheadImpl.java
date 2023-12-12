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
package team.unnamed.hephaestus.animation.timeline.playhead;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.animation.interpolation.Interpolation;
import team.unnamed.hephaestus.animation.interpolation.Interpolators;
import team.unnamed.hephaestus.animation.interpolation.KeyFrameInterpolator;
import team.unnamed.hephaestus.animation.timeline.KeyFrame;
import team.unnamed.hephaestus.animation.timeline.Timeline;

import java.util.Iterator;

final class PlayheadImpl<T> implements Playhead<T> {

    private final Timeline<T> timeline;
    private final Iterator<KeyFrame<T>> keyFrameIterator;

    // the previous and next keyframes,
    // it is know that the keyframe returned
    // by next()
    private KeyFrame<T> previous;
    private @Nullable KeyFrame<T> next;
    private @Nullable KeyFrame<T> after;

    // the current interpolation between the previous
    // and the next keyframes, it is null if the next
    // keyframe is null
    private Interpolation<T> interpolation;

    // the current tick
    private int tick = 0;

    PlayheadImpl(Timeline<T> timeline) {
        this.timeline = timeline;
        // set up
        // it is ensured from Playhead#playhead that the keyframes
        // list will have at least two elements
        keyFrameIterator = timeline.keyFrames().iterator();
        KeyFrame<T> firstKeyFrame = keyFrameIterator.next();
        KeyFrame<T> secondKeyFrame = keyFrameIterator.next();

        if (firstKeyFrame.time() > 0) {
            // if first key frame is not located at the start,
            // create a key frame with time 0 and the same value
            // as the first keyframe
            // |-|         |           |
            // (X)        first       second
            previous = new KeyFrame<>(0, firstKeyFrame.value(), Interpolators.always(firstKeyFrame.value()));
            next = firstKeyFrame;
            after = secondKeyFrame;
        } else {
            //   |-|        |           |
            //  first     second      after?
            previous = firstKeyFrame;
            next = secondKeyFrame;
            after = keyFrameIterator.hasNext() ? keyFrameIterator.next() : null;
        }

        interpolation = computeInterpolator().interpolation(null, previous, next, after);
    }

    private KeyFrameInterpolator<T> computeInterpolator() {
        KeyFrameInterpolator<T> interpolator;
        if (next == null) {
            interpolator = Interpolators.always(previous.value());
        } else {
            interpolator = previous.interpolatorOr(timeline.defaultInterpolator())
                    .combineRight(next.interpolatorOr(timeline.defaultInterpolator()));
        }
        return interpolator;
    }

    @Override
    public @NotNull T next() {
        // if there is no next keyframe to interpolate,
        // just return the previous keyframe
        if (next == null) {
            return previous.value();
        }

        if (tick == next.time()) {
            tick++;
            return next.value();
        }

        // if the current tick is greater than the next keyframe's time,
        // then we need to update the previous and next keyframes
        if (tick > next.time()) {
            @Nullable KeyFrame<T> before = previous;
            previous = next;
            next = after;

            if (next == null) {
                interpolation = null;
                return previous.value();
            }

            if (keyFrameIterator.hasNext()) {
                after = keyFrameIterator.next();
            } else {
                after = null;
            }

            interpolation = computeInterpolator().interpolation(before, previous, next, after);
        }

        // interpolate the previous and next keyframes
        double progress = ((double) (tick - previous.time())) / ((double) (next.time() - previous.time()));
        tick++;
        return interpolation.interpolate(progress);
    }

}
