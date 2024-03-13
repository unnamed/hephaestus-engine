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
package team.unnamed.hephaestus.view.modifier;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.util.Quaternion;

public interface BoneModifier {
    static @NotNull BoneModifier nop() {
        return NopBoneModifier.INSTANCE;
    }

    default @NotNull Key modifyItem(final @NotNull Key original) {
        return original;
    }

    default @NotNull CompoundBinaryTag modifyItemTag(final @NotNull CompoundBinaryTag original) {
        return original;
    }

    default @NotNull Vector3Float modifyPosition(final @NotNull Vector3Float original) {
        return original;
    }

    default @NotNull Quaternion modifyRotation(final @NotNull Quaternion original) {
        return original;
    }

    default @NotNull Vector3Float modifyScale(final @NotNull Vector3Float original) {
        return original;
    }

    default @NotNull BoneModifier andThen(final @NotNull BoneModifier modifier) {
        return new BoneModifier() {
            @Override
            public @NotNull Key modifyItem(final @NotNull Key original) {
                return modifier.modifyItem(BoneModifier.this.modifyItem(original));
            }

            @Override
            public @NotNull CompoundBinaryTag modifyItemTag(final @NotNull CompoundBinaryTag original) {
                return modifier.modifyItemTag(BoneModifier.this.modifyItemTag(original));
            }

            @Override
            public @NotNull Vector3Float modifyPosition(final @NotNull Vector3Float original) {
                return modifier.modifyPosition(BoneModifier.this.modifyPosition(original));
            }

            @Override
            public @NotNull Quaternion modifyRotation(final @NotNull Quaternion original) {
                return modifier.modifyRotation(BoneModifier.this.modifyRotation(original));
            }

            @Override
            public @NotNull Vector3Float modifyScale(final @NotNull Vector3Float original) {
                return modifier.modifyScale(BoneModifier.this.modifyScale(original));
            }
        };
    }
}
