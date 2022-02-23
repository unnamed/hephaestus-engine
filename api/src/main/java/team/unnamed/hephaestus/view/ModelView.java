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
package team.unnamed.hephaestus.view;

import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.AnimationController;
import team.unnamed.hephaestus.animation.ModelAnimation;

import java.util.Collection;
import java.util.Objects;

/**
 * Base abstraction for representing a {@link Model}
 * view, allows multiple viewers and animation playing
 *
 * <p>Platform independent, to use more specific properties
 * and avoid using generics, use their implementations in
 * runtime-* projects</p>
 *
 * @param <T> The platform-specific server player type
 * @since 1.0.0
 */
public interface ModelView<T> {

    /**
     * Returns the model being viewed
     * from this view instance
     */
    Model model();

    /**
     * Returns a collection of the bone views
     * where an entity can be attached (added
     * as passenger)
     *
     * @return The model view seats
     */
    Collection<? extends BoneView> seats();

    /**
     * Sets the {@link ModelInteractListener} for
     * this model view
     *
     * @param interactListener The interaction listener
     */
    void interactListener(ModelInteractListener<T> interactListener);

    /**
     * Colorizes this view using the specified
     * {@code r} (red), {@code g} (green) and
     * {@code b} (blue) color components
     */
    void colorize(int r, int g, int b);

    /**
     * Gets the bone with the specified name
     *
     * @param name The bone name
     * @return The bone view, null if absent
     */
    @Nullable BoneView bone(String name);

    /**
     * Returns the animation controller linked to
     * this model view
     *
     * @return The animation controller
     */
    AnimationController animationController();

    /**
     * Finds and plays the animation with the
     * specified {@code name} for this model view
     * instance.
     *
     * @param name The animation name
     * @param transitionTicks the amount of ticks for the transition between last animation and the current one
     * @see Model#animations()
     */
    default void playAnimation(String name, int transitionTicks) {
        ModelAnimation animation = model().animations().get(name);
        Objects.requireNonNull(animation, "Animation " + name);
        animationController().queue(animation, transitionTicks);
    }

    /**
     * Finds and plays the animation with the
     * specified {@code name} for this model view
     * instance.
     *
     * @param name The animation name
     * @see Model#animations()
     */
    default void playAnimation(String name) {
        playAnimation(name, 0);
    }

    /**
     * Ticks animations, makes required bones pass
     * to the next animation frame
     */
    void tickAnimations();

}
