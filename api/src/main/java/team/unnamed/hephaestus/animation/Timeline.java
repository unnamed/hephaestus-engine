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
package team.unnamed.hephaestus.animation;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;

import java.util.Iterator;

/**
 * Represents an animation timeline, holds and creates
 * iterators for {@link KeyFrame} instances in order to
 * perform model animations
 *
 * @since 1.0.0
 */
public interface Timeline extends Iterable<KeyFrame> {

    /**
     * Adds the given {@code value} to the timeline in
     * the specified {@code tick} and {@code channel}
     *
     * @since 1.0.0
     */
    void put(int position, Channel channel, Vector3Float value);

    /**
     * Creates an iterator that iterates over
     * keyframes stored in this timeline
     *
     * @since 1.0.0
     */
    @NotNull
    @Override
    Iterator<KeyFrame> iterator();

    enum Channel {
        POSITION(Vector3Float.ZERO),
        ROTATION(Vector3Float.ZERO),
        SCALE(Vector3Float.ONE);

        private final Vector3Float initialValue;

        Channel(Vector3Float initialValue) {
            this.initialValue = initialValue;
        }

        public Vector3Float initialValue() {
            return initialValue;
        }
    }

    /**
     * Creates a new dynamic {@link Timeline} instance, it
     * will generate synthetic keyframes during iteration
     * and not during creation
     *
     * @return A new dynamic timeline instance
     * @since 1.0.0
     */
    static Timeline dynamic(int length) {
        return new DynamicTimeline(length);
    }

}
