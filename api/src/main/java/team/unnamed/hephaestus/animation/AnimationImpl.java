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

import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.animation.timeline.bone.BoneTimeline;
import team.unnamed.hephaestus.animation.timeline.effect.EffectsTimeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

final class AnimationImpl implements Animation {

    private final String name;
    private final int length;
    private final LoopMode loopMode;
    private final int priority;
    private final Map<String, BoneTimeline> timelines;
    private final EffectsTimeline effectsTimeline;

    AnimationImpl(
            final @NotNull String name,
            final int length,
            final @NotNull LoopMode loopMode,
            final int priority,
            final @NotNull Map<String, BoneTimeline> timelines,
            final @NotNull EffectsTimeline effectsTimeline
    ) {
        this.name = requireNonNull(name, "name");
        this.length = length;
        this.loopMode = requireNonNull(loopMode, "loopMode");
        this.priority = priority;
        this.timelines = requireNonNull(timelines, "timelines");
        this.effectsTimeline = requireNonNull(effectsTimeline, "effectsTimeline");;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public @NotNull LoopMode loopMode() {
        return loopMode;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public @NotNull Map<String, BoneTimeline> timelines() {
        return timelines;
    }

    @Override
    public @NotNull EffectsTimeline effectsTimeline() {
        return effectsTimeline;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("name", name),
                ExaminableProperty.of("length", length),
                ExaminableProperty.of("loopMode", loopMode),
                ExaminableProperty.of("priority", priority),
                ExaminableProperty.of("timelines", timelines)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimationImpl that = (AnimationImpl) o;
        return name.equals(that.name)
                && length == that.length
                && loopMode == that.loopMode
                && timelines.equals(that.timelines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, length, loopMode, timelines);
    }

    @Override
    public String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    static final class BuilderImpl implements Builder {

        private String name;
        private int length;
        private LoopMode loopMode;
        private int priority;
        private Map<String, BoneTimeline> timelines;
        private EffectsTimeline effectsTimeline;

        @Override
        public @NotNull Builder name(final @NotNull String name) {
            this.name = requireNonNull(name, "name");
            return this;
        }

        @Override
        public @NotNull Builder length(final int length) {
            this.length = length;
            return this;
        }

        @Override
        public @NotNull Builder loopMode(final @NotNull LoopMode loopMode) {
            this.loopMode = requireNonNull(loopMode, "loopMode");
            return this;
        }

        @Override
        public @NotNull Builder priority(final int priority) {
            this.priority = priority;
            return this;
        }

        @Override
        public @NotNull Builder timelines(@NotNull Map<String, BoneTimeline> timelines) {
            this.timelines = new HashMap<>(requireNonNull(timelines, "timelines"));
            return this;
        }

        @Override
        public @NotNull Builder effectsTimeline(@NotNull EffectsTimeline timeline) {
            this.effectsTimeline = requireNonNull(timeline, "effectsTimeline");
            return this;
        }

        @Override
        public @NotNull Builder timeline(@NotNull String boneName, @NotNull BoneTimeline timeline) {
            requireNonNull(boneName, "boneName");
            requireNonNull(timeline, "timeline");
            if (timelines == null) {
                timelines = new HashMap<>();
            }
            timelines.put(boneName, timeline);
            return this;
        }

        @Override
        public @NotNull Animation build() {
            return new AnimationImpl(name, length, loopMode, priority, timelines, effectsTimeline);
        }
    }
}
