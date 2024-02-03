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
package team.unnamed.hephaestus.player;

import team.unnamed.creative.base.Vector3Float;

public enum SimplePlayerBoneType implements PlayerBoneType {
    HEAD(
            "head",
            0,
            new Vector3Float(0, 180, 0), // rotation
            new Vector3Float(0.9375F, 0.9375F, 0.9375F), // scale
            new Vector3Float(0, 7.3F, 0) // translation
    ),
    RIGHT_ARM(
            "right_arm",
            -1024,
            new Vector3Float(0, 180, 0), // rotation
            new Vector3Float(0.46875F, 1.40625F, 0.46875F), // scale
            new Vector3Float(0.96F, 1.6F, 0) // translation
    ),
    LEFT_ARM(
            "left_arm",
            -2048,
            new Vector3Float(0, 180, 0), // rotation
            new Vector3Float(0.46875F, 1.40625F, 0.46875F), // scale
            new Vector3Float(-0.96F, 1.6F, 0) // translation
    ),
    TORSO(
            "torso",
            -3072,
            new Vector3Float(0, 180, 0), // rotation
            new Vector3Float(0.9375F, 1.40625F, 0.46875F), // scale
            new Vector3Float(0, 11, 0) // translation
    ),
    RIGHT_LEG(
            "right_leg",
            -4096,
            new Vector3Float(0, 180, 0), // rotation
            new Vector3Float(0.46875F, 1.40625F, 0.46875F), // scale
            new Vector3Float(-0.08F, -0.2F, 0) // translation
    ),
    LEFT_LEG(
            "left_leg",
            -5120,
            new Vector3Float(0, 180, 0), // rotation
            new Vector3Float(0.46875F, 1.40625F, 0.46875F), // scale
            new Vector3Float(-0.08F, -0.2F, 0) // translation
    ),
    RIGHT_ARM_SLIM(
            "right_arm",
            -1024,
            new Vector3Float(0, 180, 0), // rotation
            new Vector3Float(0.3515625F, 1.40625F, 0.46875F), // scale
            new Vector3Float(0.47F, 1.6F, 0), // translation
            true
    ),
    LEFT_ARM_SLIM(
            "left_arm",
            -2048,
            new Vector3Float(0, 180, 0),
            new Vector3Float(0.3515625F, 1.40625F, 0.46875F),
            new Vector3Float(-0.47F, 1.6F, 0),
            true
    );

    private final String boneName;
    private final float offset;

    private final Vector3Float rotation;
    private final Vector3Float scale;
    private final Vector3Float translation;
    private final boolean slim;

    SimplePlayerBoneType(String name, float offset, Vector3Float rotation, Vector3Float scale, Vector3Float translation) {
        this(name, offset, rotation, scale, translation, false);
    }

    SimplePlayerBoneType(String name, float offset, Vector3Float rotation, Vector3Float scale, Vector3Float translation, boolean slim) {
        this.boneName = name;
        this.offset = offset;
        this.rotation = rotation;
        this.scale = scale;
        this.translation = translation;
        this.slim = slim;
    }

    @Override
    public String boneName() {
        return boneName;
    }

    @Override
    public float offset() {
        return offset;
    }

    @Override
    public int modelData() {
        return this.ordinal() + 1;
    }

    @Override
    public Vector3Float rotation() {
        return rotation;
    }

    @Override
    public Vector3Float scale() {
        return scale;
    }

    @Override
    public Vector3Float translation() {
        return translation;
    }

    @Override
    public boolean slim() {
        return slim;
    }
}