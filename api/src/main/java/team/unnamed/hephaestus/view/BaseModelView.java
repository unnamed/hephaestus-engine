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
package team.unnamed.hephaestus.view;

import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.AnimationController;
import team.unnamed.hephaestus.animation.Animation;

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
 * @since 1.0.0
 */
public interface BaseModelView<TViewer> {

    /**
     * Returns the model being viewed
     * from this view instance
     */
    Model model();

    Collection<TViewer> viewers();

    boolean addViewer(TViewer viewer);

    boolean removeViewer(TViewer viewer);

    /**
     * Colorizes this view using the specified
     * {@code r} (red), {@code g} (green) and
     * {@code b} (blue) color components
     * 
     * @param r The red component [0-255]
     * @param g The green component [0-255]
     * @param b The blue component [0-255]
     */
    default void colorize(int r, int g, int b) {
        for (BaseBoneView bone : bones()) {
            bone.colorize(r, g, b);
        }
    }

    /**
     * Colorizes this view using the specified,
     * encoded RGB (Red, Green, Blue) color
     *
     * @param rgb The encoded color
     */
    default void colorize(int rgb) {
        for (BaseBoneView bone : bones()) {
            bone.colorize(rgb);
        }
    }

    /**
     * Colorizes this view using the default,
     * initial color {@link BaseBoneView#DEFAULT_COLOR}
     *
     * @see BaseModelView#colorize(int)
     */
    default void colorizeDefault() {
        colorize(BaseBoneView.DEFAULT_COLOR);
    }

    /**
     * Returns a collection holding <strong>all</strong>
     * the bones created this model view
     *
     * @return The model view bone views
     * @since 1.0.0
     */
    Collection<? extends BaseBoneView> bones();

    /**
     * Gets the bone with the specified name
     *
     * @param name The bone name
     * @return The bone view, null if absent
     */
    @Nullable BaseBoneView bone(String name);

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
        Animation animation = model().animations().get(name);
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
