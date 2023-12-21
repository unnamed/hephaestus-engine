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
package team.unnamed.hephaestus.animation.controller;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.util.Quaternion;
import team.unnamed.hephaestus.view.BaseModelView;
import team.unnamed.molang.MolangEngine;

/**
 * Represents the object responsible to animate
 * a single model or group of models
 *
 * @since 1.0.0
 */
public interface AnimationController {
    /**
     * Queues the given {@link Animation} so that
     * it will be played in the next ticks
     *
     * @param animation The queued animation
     * @param transitionTicks The animation transition ticks
     * @since 1.0.0
     */
    void queue(final @NotNull Animation animation, final int transitionTicks);

    /**
     * Queues the given {@link Animation} so that
     * it will be played in the next ticks, similar
     * to calling {@link AnimationController#queue}
     * using zero transition ticks
     *
     * @param animation The queued animation
     * @since 1.0.0
     */
    default void queue(final @NotNull Animation animation) {
        queue(animation, 0);
    }

    /**
     * Clears the animation queue and stops current
     * animation.
     *
     * @since 1.0.0
     */
    void clearQueue();

    /**
     * Passes to the next animation frame using
     * the given model rotation and position
     *
     * @param initialRotation The initial model rotation
     * @param initialPosition The initial model position
     * @since 1.0.0
     */
    void tick(final @NotNull Quaternion initialRotation, final @NotNull Vector3Float initialPosition);

    /**
     * Passes to the next animation frame
     *
     * @since 1.0.0
     */
    default void tick() {
        tick(Quaternion.IDENTITY, Vector3Float.ZERO);
    }

    /**
     * Passes to the next animation frame using
     * the given model rotation.
     *
     * @param yaw The model yaw
     * @param pitch The model pitch
     * @since 1.0.0
     */
    default void tick(final float yaw, final float pitch) {
        tick(yaw, pitch, Vector3Float.ZERO);
    }

    /**
     * Passes to the next animation frame using
     * the given model rotation and initial position
     * offset.
     *
     * @param yaw The model yaw
     * @param pitch The model pitch
     * @param initialPosition The initial model position
     * @since 1.0.0
     */
    default void tick(final float yaw, float pitch, @NotNull final Vector3Float initialPosition) {
        final Quaternion initialRotation = Quaternion.fromEulerDegrees(new Vector3Float(pitch, 360 - yaw, 0));
        tick(initialRotation, initialPosition);
    }

    /**
     * Returns the script engine for this animation controller.
     *
     * @return The script engine
     * @since 1.0.0
     */
    @NotNull MolangEngine<BaseModelView<?>> scriptEngine();

    /**
     * Creates a new {@link AnimationController} for the given
     * {@link BaseModelView model view}.
     *
     * @param view the model view to use
     * @return The created animation controller
     * @since 1.0.0
     */
    static @NotNull AnimationController create(final @NotNull BaseModelView<?> view) {
        return new AnimationControllerImpl(view);
    }
}
