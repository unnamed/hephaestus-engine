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
package team.unnamed.hephaestus.playermodel;

public enum PlayerBoneType {

    HEAD("head", 0),
    RIGHT_ARM("right_arm", -1024),
    LEFT_ARM("left_arm", -2048),
    TORSO("torso", -3072),
    RIGHT_LEG("right_leg", -4096),
    LEFT_LEG("left_leg", -5120),
    RIGHT_ARM_SLIM("right_arm", -1024),
    LEFT_ARM_SLIM("left_arm", -2048),
    UNKNOWN("unknown", 0);

    private static final PlayerBoneType[] VALUES = PlayerBoneType.values();

    private final String boneName;
    private final float offset;

    PlayerBoneType(String name, float offset) {
        this.boneName = name;
        this.offset = offset;
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

    public static PlayerBoneType matchFor(Skin skin, String boneName) {
        for (PlayerBoneType type : VALUES) {
            if (type != UNKNOWN && type.boneName.equals(boneName)) {
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

        return UNKNOWN;
    }
}