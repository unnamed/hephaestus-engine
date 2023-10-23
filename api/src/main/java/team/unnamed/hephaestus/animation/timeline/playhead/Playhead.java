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
import team.unnamed.hephaestus.animation.timeline.Timeline;

public interface Playhead<T> {

    @NotNull T next();

    static <T> Playhead<T> playhead(Timeline<T> timeline) {
        int len = timeline.keyFrames().size();
        if (len == 0) {
            // empty playheads always return the default value
            return new SingletonPlayhead<>(timeline.initial());
        } else if (len == 1) {
            // when a timeline has only one keyframe, no matter its time,
            // the playhead will always return that keyframe's value
            return new SingletonPlayhead<>(timeline.keyFrames().first().value());
        } else {
            return new PlayheadImpl<>(timeline);
        }
    }

}
