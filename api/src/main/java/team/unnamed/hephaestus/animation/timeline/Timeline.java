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

import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.animation.interpolation.Interpolator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Timeline<T> {

    private final TimelineOptions<T> options;
    private final List<KeyFrame<T>> keyFrames = new ArrayList<>();

    private Timeline(TimelineOptions<T> options) {
        this.options = options;
    }

    public TimelineOptions<T> options() {
        return options;
    }

    public List<KeyFrame<T>> keyFrames() {
        return keyFrames;
    }

    public void put(KeyFrame<T> keyFrame) {
        keyFrames.add(keyFrame);
        keyFrames.sort(Comparator.comparing(KeyFrame::time));
    }

    public void put(int time, T value) {
        put(new KeyFrame<>(time, value, options.defaultInterpolator()));
    }

    public void put(int time, T value, Interpolator<T> interpolator) {
        put(new KeyFrame<>(time, value, interpolator));
    }

    public TickIterator<T> tickiterator() {
        return new TickIterator<>(this);
    }

    public static <T> Timeline<T> timeline(TimelineOptions<T> type) {
        return new Timeline<>(type);
    }

}
