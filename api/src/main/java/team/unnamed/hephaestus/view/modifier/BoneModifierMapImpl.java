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
import team.unnamed.hephaestus.view.AbstractBoneView;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

final class BoneModifierMapImpl implements BoneModifierMap {
    private final Map<BoneModifierType<?>, BoneModifier> modifiers = new HashMap<>();
    private final AbstractBoneView bone;

    public BoneModifierMapImpl(final @NotNull AbstractBoneView bone) {
        this.bone = bone;
    }

    @Override
    public <T extends BoneModifier> void removeModifier(final @NotNull BoneModifierType<T> type) {
        modifiers.remove(type);
    }

    @Override
    public <T extends BoneModifier> void configure(final @NotNull BoneModifierType<T> type, final @NotNull Consumer<T> configure) {
        if (modifiers.containsKey(type)) {
            configure.accept((T) modifiers.get(type));
        } else {
            T modifier = type.create(bone);
            modifiers.put(type, modifier);
            configure.accept(modifier);
        }
    }

    @Override
    public boolean hasModifier(final @NotNull BoneModifierType<?> type) {
        return modifiers.containsKey(type);
    }

    @Override
    public @NotNull Key modifyItem(@NotNull Key previous) {
        for (BoneModifier modifier : modifiers.values()) {
            previous = modifier.modifyItem(previous);
        }
        return previous;
    }

    @Override
    public @NotNull CompoundBinaryTag modifyItemTag(@NotNull CompoundBinaryTag previous) {
        for (BoneModifier modifier : modifiers.values()) {
            previous = modifier.modifyItemTag(previous);
        }
        return previous;
    }

    @Override
    public @NotNull Vector3Float modifyPosition(@NotNull Vector3Float position) {
        for (BoneModifier modifier : modifiers.values()) {
            position = modifier.modifyPosition(position);
        }
        return position;
    }

    @Override
    public @NotNull Quaternion modifyRotation(@NotNull Quaternion rotation) {
        for (BoneModifier modifier : modifiers.values()) {
            rotation = modifier.modifyRotation(rotation);
        }
        return rotation;
    }

    @Override
    public @NotNull Vector3Float modifyScale(@NotNull Vector3Float scale) {
        for (BoneModifier modifier : modifiers.values()) {
            scale = modifier.modifyScale(scale);
        }
        return scale;
    }
}
