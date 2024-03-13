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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.view.AbstractBoneView;

import java.util.function.Consumer;

public interface BoneModifierMap extends BoneModifier {
    static @NotNull BoneModifierMap create(final @NotNull AbstractBoneView bone) {
        return new BoneModifierMapImpl(bone);
    }

    <T extends BoneModifier> void removeModifier(final @NotNull BoneModifierType<T> type);

    <T extends BoneModifier> void configure(final @NotNull BoneModifierType<T> type, final @NotNull Consumer<T> configure);

    boolean hasModifier(final @NotNull BoneModifierType<?> type);

    interface Forwarding extends BoneModifierMap {
        @NotNull BoneModifierMap modifiers();

        @Override
        default <T extends BoneModifier> void removeModifier(final @NotNull BoneModifierType<T> type) {
            modifiers().removeModifier(type);
        }

        @Override
        default <T extends BoneModifier> void configure(final @NotNull BoneModifierType<T> type, final @NotNull Consumer<T> configure) {
            modifiers().configure(type, configure);
        }

        @Override
        default boolean hasModifier(final @NotNull BoneModifierType<?> type) {
            return modifiers().hasModifier(type);
        }
    }
}
