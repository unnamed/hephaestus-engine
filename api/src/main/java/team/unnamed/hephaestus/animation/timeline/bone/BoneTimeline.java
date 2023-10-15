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
package team.unnamed.hephaestus.animation.timeline.bone;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.animation.timeline.Timeline;

/**
 *
 * @since 1.0.0
 */
public interface BoneTimeline {

    /**
     * @return A new dynamic timeline instance
     * @since 1.0.0
     */
    static @NotNull Builder boneTimeline() {
        return new BoneTimelineImpl.BuilderImpl();
    }

    @NotNull Timeline<Vector3Float> positions();

    @NotNull Timeline<Vector3Float> rotations();

    @NotNull Timeline<Vector3Float> scales();

    default @NotNull BoneTimelinePlayhead createPlayhead() {
        return new BoneTimelinePlayhead(this);
    }

    interface Builder {

        /**
         * Set the positions timeline
         *
         * @param positions The positions timeline
         * @return This builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        @NotNull Builder positions(final @NotNull Timeline<Vector3Float> positions);

        /**
         * Set the rotations timeline
         *
         * @param rotations The rotations timeline
         * @return This builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        @NotNull Builder rotations(final @NotNull Timeline<Vector3Float> rotations);

        /**
         * Set the scales timeline
         *
         * @param scales The scales timeline
         * @return This builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        @NotNull Builder scales(final @NotNull Timeline<Vector3Float> scales);

        @NotNull BoneTimeline build();

    }
}
