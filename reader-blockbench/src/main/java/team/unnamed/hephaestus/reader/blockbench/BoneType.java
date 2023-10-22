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
package team.unnamed.hephaestus.reader.blockbench;

import java.util.function.Predicate;

enum BoneType {
    // name matching based on Model-Engine by Ticxo, for
    // compatibility with existing Model-Engine models
    // todo: support more types
    BOUNDING_BOX(exact("hitbox")),
    // SUB_BOUNDING_BOX(prefixed("b_")),
    // DRIVER_SEAT(exact("mount")),
    // PASSENGER_SEAT(prefixed("p_")),
    // NAME_TAG(prefixed("tag_")),
    // LEFT_HAND(prefixed("il_")),
    // RIGHT_HAND(prefixed("ir_")),
    NONE(v -> true);

    private static final BoneType[] VALUES = BoneType.values();

    private final Predicate<String> matcher;

    BoneType(Predicate<String> matcher) {
        this.matcher = matcher;
    }

    public boolean matches(String name) {
        return matcher.test(name);
    }

    public static BoneType matchByBoneName(String name) {
        for (BoneType type : VALUES) {
            // "NONE" is a special case, since I do not
            // want to depend on the enum values order,
            // we do this
            if (type != NONE && type.matches(name)) {
                return type;
            }
        }
        return NONE;
    }

    private static Predicate<String> exact(String value) {
        return v -> v.equals(value);
    }

    private static Predicate<String> prefixed(String prefix) {
        return v -> v.startsWith(prefix);
    }
}
