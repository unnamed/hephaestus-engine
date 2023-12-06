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

import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;

public enum PlayerBoneType {
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
            new Vector3Float(0.47F, 1.6F, 0) // translation
    ),
    LEFT_ARM_SLIM(
            "left_arm",
            -2048,
            new Vector3Float(0, 180, 0),
            new Vector3Float(0.3515625F, 1.40625F, 0.46875F),
            new Vector3Float(-0.47F, 1.6F, 0)
    );

    private static final PlayerBoneType[] VALUES = PlayerBoneType.values();

    private final String boneName;
    private final float offset;

    private final Vector3Float rotation;
    private final Vector3Float scale;
    private final Vector3Float translation;

    PlayerBoneType(String name, float offset, Vector3Float rotation, Vector3Float scale, Vector3Float translation) {
        this.boneName = name;
        this.offset = offset;

        this.rotation = rotation;
        this.scale = scale;
        this.translation = translation;
    }

    public String boneName() {
        return boneName;
    }

    public float offset() {
        return offset;
    }

    public int modelData() {
        return this.ordinal() + 1;
    }

    public Vector3Float rotation() {
        return rotation;
    }

    public Vector3Float scale() {
        return scale;
    }

    public Vector3Float translation() {
        return translation;
    }

    public static @Nullable PlayerBoneType matchFor(Skin skin, String boneName) {
        for (PlayerBoneType type : VALUES) {
            if (type.boneName.equals(boneName)) {
                if (skin.type() == Skin.Type.SLIM) {
                    switch (type) {
                        case RIGHT_ARM -> {
                            return RIGHT_ARM_SLIM;
                        }
                        case LEFT_ARM -> {
                            return LEFT_ARM_SLIM;
                        }
                        default -> {
                            return type;
                        }
                    }
                }

                return type;
            }
        }
        return null;
    }
}