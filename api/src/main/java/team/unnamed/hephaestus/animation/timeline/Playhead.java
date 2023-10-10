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

import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.animation.interpolation.Interpolation;

import java.util.Iterator;

public final class Playhead<T> {

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

    Playhead(Timeline<T> timeline) {
        this.timeline = timeline;
        // set up
        keyFrameIterator = timeline.keyFrames().iterator();
        previous = new KeyFrame<>(0, timeline.initial(), timeline.defaultInterpolator());
        if (keyFrameIterator.hasNext()) {
            next = keyFrameIterator.next();
            if (keyFrameIterator.hasNext()) {
                after = keyFrameIterator.next();
            }

            interpolation = previous.interpolatorOr(timeline.defaultInterpolator())
                    .interpolation(previous.value(), next.value());
        }
    }

    public T next() {
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

            interpolation = previous.interpolatorOr(timeline.defaultInterpolator())
                    .interpolation(
                            before == null ? null : before.value(),
                            previous.value(),
                            next.value(),
                            after == null ? null : after.value()
                    );
        }

        // interpolate the previous and next keyframes
        double progress = ((double) (tick - previous.time())) / ((double) (next.time() - previous.time()));
        tick++;
        return interpolation.interpolate(progress);
    }

}
