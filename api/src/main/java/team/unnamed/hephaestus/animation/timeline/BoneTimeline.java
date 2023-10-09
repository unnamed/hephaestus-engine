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
import team.unnamed.hephaestus.animation.Frame;

/**
 *
 * @since 1.0.0
 */
public final class BoneTimeline {

    private final Timeline<Vector3Float> positionTimeline = Timeline.timeline(TimelineOptions.POSITION);
    private final Timeline<Vector3Float> rotationTimeline = Timeline.timeline(TimelineOptions.ROTATION);
    private final Timeline<Vector3Float> scaleTimeline = Timeline.timeline(TimelineOptions.SCALE);

    public Timeline<Vector3Float> positions() {
        return positionTimeline;
    }

    public Timeline<Vector3Float> rotations() {
        return rotationTimeline;
    }

    public Timeline<Vector3Float> scales() {
        return scaleTimeline;
    }

    public StateIterator iterator() {
        return new StateIterator();
    }

    /**
     * Creates a new dynamic {@link BoneTimeline} instance, it
     * will generate synthetic keyframes during iteration
     * and not during creation
     *
     * @return A new dynamic timeline instance
     * @since 1.0.0
     */
    public static BoneTimeline create() {
        return new BoneTimeline();
    }

    public class StateIterator {

        private final TickIterator<Vector3Float> positions = positions().tickiterator();
        private final TickIterator<Vector3Float> rotations = rotations().tickiterator();
        private final TickIterator<Vector3Float> scales = scales().tickiterator();
        private int tick;

        public int tick() {
            return tick;
        }

        public Frame next() {
            tick++;
            return new Frame(
                    positions.next(),
                    rotations.next(),
                    scales.next()
            );
        }

    }

}
