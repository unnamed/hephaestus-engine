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
package team.unnamed.hephaestus.bukkit.plugin;

import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.player.PlayerBoneType;
import team.unnamed.hephaestus.player.Skin;
import team.unnamed.hephaestus.player.Skin.Type;

public enum DetailedPlayerBoneType implements PlayerBoneType {
    HEAD("head", 0.0F, new Vector3Float(0.9375F, 0.9375F, 0.9375F), new Vector3Float(0.0F, 7.3F, 0.0F)),
    RIGHT_ARM("right_arm", -1024.0F, new Vector3Float(0.46875F, 0.703125F, 0.46875F), new Vector3Float(-0.65F, 1.5F, 0F)),
    LEFT_ARM("left_arm", -1024.0F * 2, new Vector3Float(0.46875F, 0.703125F, 0.46875F), new Vector3Float(0.65F, 1.5F, 0.0F)),
    RIGHT_FOREARM("right_forearm", -1024.0F * 3, new Vector3Float(0.46875F, 0.703125F, 0.46875F), new Vector3Float(0.0F, 0F, 0.0F)),
    LEFT_FOREARM("left_forearm", -1024.0F * 4, new Vector3Float(0.46875F, 0.703125F, 0.46875F), new Vector3Float(0.0F, 0F, 0.0F)),
    HIP("hip", -1024.0F * 5, new Vector3Float(0.9375F, 0.46875F, 0.46875F), new Vector3Float(0.0F, 3.75F, 0.0F)),
    WAIST("waist", -1024.0F * 6, new Vector3Float(0.9375F, 0.46875F, 0.46875F), new Vector3Float(0.0F, 3.75F, 0.0F)),
    CHEST("chest", -1024.0F * 7, new Vector3Float(0.9375F, 0.46875F, 0.46875F), new Vector3Float(0.0F, 3.75F, 0.0F)),
    RIGHT_LEG("right_leg", -1024.0F * 8, new Vector3Float(0.46875F, 0.703125F, 0.46875F), new Vector3Float(-0.08F, 0F, 0.0F)),
    LEFT_LEG("left_leg", -1024.0F * 9, new Vector3Float(0.46875F, 0.703125F, 0.46875F), new Vector3Float(0.08F, 0F, 0.0F)),
    RIGHT_FORELEG("right_foreleg", -1024.0F * 10, new Vector3Float(0.46875F, 0.703125F, 0.46875F), new Vector3Float(-0.07F, 0F, 0.0F)),
    LEFT_FORELEG("left_foreleg", -1024.0F * 11, new Vector3Float(0.46875F, 0.703125F, 0.46875F), new Vector3Float(0.07F, 0F, 0.0F)),

    RIGHT_ARM_SLIM("right_arm", -1024.0F, new Vector3Float(0.3515625F, 0.703125F, 0.46875F), new Vector3Float(-0.3F, 1.5F, 0.0F), true),
    LEFT_ARM_SLIM("left_arm", -1024.0F * 2, new Vector3Float(0.3515625F, 0.703125F, 0.46875F), new Vector3Float(0.3F, 1.5F, 0.0F), true),
    RIGHT_FOREARM_SLIM("right_arm", -1024.0F * 3, new Vector3Float(0.3515625F, 0.703125F, 0.46875F), new Vector3Float(0.3F, 0F, 0.0F), true),
    LEFT_FOREARM_SLIM("left_arm", -1024.0F * 4, new Vector3Float(0.3515625F, 0.703125F, 0.46875F), new Vector3Float(-0.3F, 0F, 0.0F), true),

    ;
    private static final DetailedPlayerBoneType[] VALUES = values();
    private final String boneName;
    private final float offset;
    private Vector3Float scale;
    public Vector3Float translation;
    private final boolean slim;

    DetailedPlayerBoneType(String name, float offset, Vector3Float scale, Vector3Float translation) {
        this(name, offset, scale, translation, false);
    }

    DetailedPlayerBoneType(String name, float offset, Vector3Float scale, Vector3Float translation, boolean slim) {
        this.boneName = name;
        this.offset = offset;
        this.scale = scale;
        this.translation = translation;
        this.slim = slim;
    }

    public String boneName() {
        return this.boneName;
    }

    public float offset() {
        return this.offset;
    }

    public int modelData() {
        return this.ordinal() + 1;
    }

    @Override
    public Vector3Float rotation() {
        return new Vector3Float(0, 180, 0);
    }

    public Vector3Float scale() {
        return this.scale;
    }

    public Vector3Float translation() {
        return this.translation;
    }

    @Override
    public boolean isSlim() {
        return slim;
    }

    public static @Nullable DetailedPlayerBoneType matchFor(Skin skin, String boneName) {

        for (DetailedPlayerBoneType type : VALUES) {
            if (type.boneName.equals(boneName)) {
                if (skin.type() == Type.SLIM) {
                    return switch (type) {
                        case RIGHT_ARM -> RIGHT_ARM_SLIM;
                        case LEFT_ARM -> LEFT_ARM_SLIM;
                        case RIGHT_FOREARM -> RIGHT_FOREARM_SLIM;
                        case LEFT_FOREARM -> LEFT_FOREARM_SLIM;
                        default -> type;
                    };
                }

                return type;
            }
        }

        return null;
    }
}
