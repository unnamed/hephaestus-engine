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

import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;

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
public interface BoneView {

    /**
     * Returns the bone represented by this
     * bone view
     *
     * @return The viewed bone
     */
    Bone bone();

    /**
     * Colorizes this bone using the specified RGB color
     * components, all components are between zero and
     * {@code 255}
     */
    void colorize(int r, int g, int b);

    /**
     * Sets the relative position of this bone to the given
     * relative {@code position} (added to the global model
     * position to obtain the global bone position)
     *
     * @param position The relative target position
     */
    void position(Vector3Float position);

    /**
     * Sets the rotation of this bone to the given {@code rotation},
     * specified in radians
     *
     * @param rotation The target rotation
     */
    void rotation(Vector3Float rotation);

}