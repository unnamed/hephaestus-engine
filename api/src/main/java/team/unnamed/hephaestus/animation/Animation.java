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

import net.kyori.examination.Examinable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.animation.timeline.bone.BoneTimeline;
import team.unnamed.hephaestus.animation.timeline.effect.EffectsTimeline;

import java.util.Map;

/**
 * Represents a Model animation. Animations are a compound
 * of keyframes by bone that modify the bone position,
 * rotation and/or scale at a specific time.
 *
 * @since 1.0.0
 */
public interface Animation extends Examinable {
    /**
     * Creates a new animation builder
     *
     * @return The created animation builder
     * @since 1.0.0
     */
    static @NotNull Builder animation() {
        return new AnimationImpl.BuilderImpl();
    }

    /**
     * Returns the animation name
     *
     * @return The animation name
     * @since 1.0.0
     */
    @NotNull String name();

    /**
     * Returns the animation length, in
     * ticks
     *
     * @return The animation length
     * @since 1.0.0
     */
    int length();

    /**
     * Returns the animation loop mode,
     * which specifies what the animation
     * controller should do when the
     * animation finishes
     *
     * @return The animation loop mode
     * @since 1.0.0
     */
    @NotNull LoopMode loopMode();

    /**
     * Returns the animation priority, which is
     * used for animation blending and transition.
     *
     * <p>Note that more priority means that the
     * animation will be more important than others,
     * so it will be blended first and it will be
     * harder to override</p>
     *
     * @return The priority for this animation
     * @since 1.0.0
     */
    int priority();

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
    @NotNull Map<String, BoneTimeline> timelines();

    @NotNull EffectsTimeline effectsTimeline();

    /**
     * An enum containing all the possible
     * loop mode values, they specify what the
     * animation controller should do when an
     * animation finishes
     *
     * @since 1.0.0
     */
    enum LoopMode {

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

    /**
     * A builder for animations
     *
     * @since 1.0.0
     */
    interface Builder {
        /**
         * Sets the animation name
         *
         * @param name The animation name
         * @return This builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        @NotNull Builder name(@NotNull String name);

        /**
         * Sets the animation length, in ticks
         *
         * @param length The animation length
         * @return This builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        @NotNull Builder length(final int length);

        /**
         * Sets the animation loop mode
         *
         * @param loopMode The animation loop mode
         * @return This builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        @NotNull Builder loopMode(final @NotNull LoopMode loopMode);

        /**
         * Sets the animation priority. The default
         * priority is 0.
         *
         * <p>Note that more priority means that the
         * animation will be more important than others,
         * so it will be blended first and it will be
         * harder to override</p>
         *
         * @param priority The animation priority
         * @return This builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        @NotNull Builder priority(final int priority);

        /**
         * Sets the animation bone timelines
         *
         * @param timelines The animation bone timelines
         * @return This builder
         * @since 1.0.0
         */
        @NotNull Builder timelines(final @NotNull Map<String, BoneTimeline> timelines);

        /**
         * Sets the animation effects timeline
         *
         * @param timeline The animation effects timeline
         * @return This builder
         * @since 1.0.0
         */
        @NotNull Builder effectsTimeline(final @NotNull EffectsTimeline timeline);

        /**
         * Adds a bone timeline to the animation
         *
         * @param boneName The bone name
         * @param timeline The bone timeline
         * @return This builder
         * @since 1.0.0
         */
        @Contract("_, _ -> this")
        @NotNull Builder timeline(final @NotNull String boneName, final @NotNull BoneTimeline timeline);

        /**
         * Builds the animation
         *
         * @return The built animation
         * @since 1.0.0
         */
        @Contract("-> new")
        @NotNull Animation build();
    }
}