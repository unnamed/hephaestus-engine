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

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.animation.timeline.Timeline;

import static java.util.Objects.requireNonNull;

final class BoneTimelineImpl implements BoneTimeline {

    private final Timeline<Vector3Float> positions;
    private final Timeline<Vector3Float> rotations;
    private final Timeline<Vector3Float> scales;

    BoneTimelineImpl(
            Timeline<Vector3Float> positions,
            Timeline<Vector3Float> rotations,
            Timeline<Vector3Float> scales
    ) {
        this.positions = requireNonNull(positions, "positions");
        this.rotations = requireNonNull(rotations, "rotations");
        this.scales = requireNonNull(scales, "scales");
    }

    @Override
    public @NotNull Timeline<Vector3Float> positions() {
        return positions;
    }

    @Override
    public @NotNull Timeline<Vector3Float> rotations() {
        return rotations;
    }

    @Override
    public @NotNull Timeline<Vector3Float> scales() {
        return scales;
    }

    static final class BuilderImpl implements Builder {

        private Timeline<Vector3Float> positions;
        private Timeline<Vector3Float> rotations;
        private Timeline<Vector3Float> scales;

        BuilderImpl() {
        }

        @Override
        public @NotNull Builder positions(@NotNull Timeline<Vector3Float> positions) {
            this.positions = requireNonNull(positions, "positions");
            return this;
        }

        @Override
        public @NotNull Builder rotations(@NotNull Timeline<Vector3Float> rotations) {
            this.rotations = requireNonNull(rotations, "rotations");
            return this;
        }

        @Override
        public @NotNull Builder scales(@NotNull Timeline<Vector3Float> scales) {
            this.scales = requireNonNull(scales, "scales");
            return this;
        }

        @Override
        public @NotNull BoneTimeline build() {
            return new BoneTimelineImpl(positions, rotations, scales);
        }
    }
}
