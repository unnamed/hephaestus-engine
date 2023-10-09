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

import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
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
public interface BaseBoneView extends NamedEntity {

    int DEFAULT_COLOR = 0xFFFFFF;

    /**
     * Returns the bone represented by this
     * bone view
     *
     * @return The viewed bone
     */
    Bone bone();

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
     * initial color {@link BaseBoneView#DEFAULT_COLOR}
     *
     * @see BaseModelView#colorize(int)
     */
    default void colorizeDefault() {
        colorize(DEFAULT_COLOR);
    }


    /**
     * Sets the relative position and the rotation of
     * this bone
     * @param position The relative target position
     * @param rotation The target rotation
     */
    void update(Vector3Float position, Quaternion rotation);

}
