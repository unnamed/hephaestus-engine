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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.modifier.BoneModifier;
import team.unnamed.hephaestus.util.Quaternion;

/**
 * Base abstraction for representing {@link Bone}
 * views, it is a bone that is actually present in
 * a world or minecraft instance
 *
 * <p>Bone views can be colorized, moved, rotated,
 * mounted by other entities, etc</p>
 *
 * <p>This abstraction is platform-independent, it
 * is recommended to use the platform implementation
 * to have access to some specific types, avoiding
 * conversions and having access to features that may
 * not be listed here</p>
 *
 * @since 1.0.0
 */
public interface AbstractBoneView {
    int DEFAULT_COLOR = 0xFFFFFF;

    /**
     * Returns the bone represented by this
     * bone view.
     *
     * @return The viewed bone
     * @since 1.0.0
     */
    @NotNull Bone bone();

    /**
     * Returns the name of the bone represented
     * by this bone view.
     *
     * @return The name of the bone
     * @since 1.0.0
     */
    default @NotNull String name() {
        return bone().name();
    }

    @Nullable BoneModifier modifier();

    void modifier(final @Nullable BoneModifier modifier);

    default void modifying(final @NotNull BoneModifier modifier) {
        final var previous = modifier();
        if (previous == null) {
            modifier(modifier);
        } else {
            modifier(previous.andThen(modifier));
        }
    }

    /**
     * Colorizes this bone view using the specified
     * {@code r} (red), {@code g} (green) and
     * {@code b} (blue) color components
     *
     * @param r The red component [0-255]
     * @param g The green component [0-255]
     * @param b The blue component [0-255]
     */
    void colorize(int r, int g, int b);

    /**
     * Colorizes this bone view using the specified,
     * encoded RGB (Red, Green, Blue) color
     *
     * @param rgb The encoded color
     */
    void colorize(int rgb);

    /**
     * Colorizes this view using the default,
     * initial color {@link AbstractBoneView#DEFAULT_COLOR}
     *
     * @see AbstractModelView#colorize(int)
     */
    default void colorizeDefault() {
        colorize(DEFAULT_COLOR);
    }

    /**
     * Sets the absolute position, rotation and scale
     * of this bone.
     *
     * <p>Note that updates are not necessarily made
     * immediately</p>
     *
     * @param position The absolute position
     * @param rotation The absolute rotation
     * @param scale The absolute scale
     * @since 1.0.0
     */
    void update(final @NotNull Vector3Float position, final @NotNull Quaternion rotation, final @NotNull Vector3Float scale);
}
