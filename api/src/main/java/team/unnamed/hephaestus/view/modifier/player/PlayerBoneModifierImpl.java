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
package team.unnamed.hephaestus.view.modifier.player;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Minecraft;
import team.unnamed.hephaestus.view.AbstractBoneView;
import team.unnamed.hephaestus.view.modifier.player.rig.PlayerBoneType;
import team.unnamed.hephaestus.view.modifier.player.skin.Skin;

import static java.util.Objects.requireNonNull;

public final class PlayerBoneModifierImpl implements PlayerBoneModifier {
    private static final Key PLAYER_HEAD_ITEM_KEY = Key.key("minecraft", "player_head");

    private final AbstractBoneView bone;

    private PlayerBoneType type;
    private Skin skin;

    public PlayerBoneModifierImpl(final @NotNull AbstractBoneView bone) {
        this.bone = requireNonNull(bone, "bone");
    }

    @Override
    public @NotNull Key modifyItem(final @NotNull Key original) {
        if (type == null) {
            return original;
        }

        // We want player_head item
        return PLAYER_HEAD_ITEM_KEY;
    }

    @Override
    public @NotNull CompoundBinaryTag modifyItemTag(final @NotNull CompoundBinaryTag original) {
        if (type == null) {
            return original;
        }

        final var modelData = skin != null && skin.type() == Skin.Type.SLIM
                ? type.slimModelData()
                : type.modelData();
        final var color = original.getCompound(Minecraft.DISPLAY_TAG).getInt(Minecraft.COLOR_TAG);
        final var builder = CompoundBinaryTag.builder()
                .putInt(Minecraft.CUSTOM_MODEL_DATA_TAG, modelData)
                .put(Minecraft.DISPLAY_TAG, CompoundBinaryTag.builder()
                        .putInt(Minecraft.COLOR_TAG, color)
                        .build());

        if (skin != null) {
            builder.put("SkullOwner", CompoundBinaryTag.builder()
                    .put("Properties", CompoundBinaryTag.builder()
                            .put("textures", ListBinaryTag.builder()
                                    .add(skin.asNBT())
                                    .build())
                            .build())
                    .build());
        }
        return builder.build();
    }

    @Override
    public @NotNull Vector3Float modifyPosition(final @NotNull Vector3Float original) {
        if (type == null) {
            return original;
        } else {
            return original.add(0, type.offset() / bone.bone().scale(), 0);
        }
    }

    @Override
    public void type(final @NotNull PlayerBoneType type) {
        this.type = requireNonNull(type, "type");
        bone.updateTransformation();
        bone.updateItem();
    }

    @Override
    public @Nullable PlayerBoneType type() {
        return type;
    }

    @Override
    public void skin(final @NotNull Skin skin) {
        this.skin = requireNonNull(skin, "skin");
        bone.updateItem();
    }

    @Override
    public @Nullable Skin skin() {
        return skin;
    }
}
