/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
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

import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.view.BaseModelView;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Represents a Model animation, applicable to
 * {@link BaseModelView} instances via their
 * {@link AnimationController} reference
 *
 * @since 1.0.0
 */
public final class Animation implements Examinable {

    private final String name;
    private final LoopMode loopMode;
    private final Map<String, Timeline> timelines;

    public Animation(
            String name,
            LoopMode loopMode,
            Map<String, Timeline> timelines
    ) {
        this.name = Objects.requireNonNull(name, "name");
        this.loopMode = Objects.requireNonNull(loopMode, "loopMode");
        this.timelines = Objects.requireNonNull(timelines, "timelines");
    }

    /**
     * Returns the animation name
     *
     * @return The animation name
     * @since 1.0.0
     */
    public String name() {
        return name;
    }

    /**
     * Returns the animation loop mode,
     * which specifies what the animation
     * controller should do when the
     * animation finishes
     *
     * @return The animation loop mode
     * @since 1.0.0
     */
    public LoopMode loopMode() {
        return loopMode;
    }

    /**
     * Returns the animation bone timelines,
     * they hold the keyframes for every model
     * bone in this animation
     *
     * <p>Note that the returned map is not
     * necessarily mutable, so you should not
     * try to modify it</p>
     *
     * @return The animation bone timelines
     * @since 1.0.0
     */
    public Map<String, Timeline> timelines() {
        return timelines;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("name", name),
                ExaminableProperty.of("loopMode", loopMode),
                ExaminableProperty.of("timelines", timelines)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animation that = (Animation) o;
        return name.equals(that.name)
                && loopMode == that.loopMode
                && timelines.equals(that.timelines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, loopMode, timelines);
    }

    @Override
    public String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    /**
     * An enum containing all the possible
     * loop mode values, they specify what the
     * animation controller should do when an
     * animation finishes
     *
     * @since 1.0.0
     */
    public enum LoopMode {

        /**
         * ONCE, makes the animation play once
         * and then the model is reset to its
         * initial state
         *
         * @since 1.0.0
         */
        ONCE,

        /**
         * HOLD, keeps the model in the last
         * animation keyframe
         *
         * @since 1.0.0
         */
        HOLD,

        /**
         * LOOP, puts the animation again in
         * the animation queue
         *
         * @since 1.0.0
         */
        LOOP
    }

}