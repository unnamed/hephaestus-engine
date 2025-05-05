/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2025 Unnamed Team
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
package team.unnamed.hephaestus.minestom;

import net.kyori.adventure.key.Key;
import net.minestom.server.color.Color;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.CustomModelData;
import net.minestom.server.item.component.HeadProfile;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Hephaestus;
import team.unnamed.hephaestus.Minecraft;
import team.unnamed.hephaestus.util.Quaternion;
import team.unnamed.hephaestus.view.modifier.BoneModifierMap;
import team.unnamed.hephaestus.view.modifier.BoneModifierType;
import team.unnamed.hephaestus.view.modifier.player.rig.PlayerBoneType;
import team.unnamed.hephaestus.view.modifier.player.skin.Skin;

import java.util.List;
import java.util.Objects;

public class BoneEntity extends GenericBoneEntity implements BoneModifierMap.Forwarding {

    private static final ItemStack BASE_HELMET = ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
            .set(DataComponents.DYED_COLOR, new Color(0xFFFFFF))
            .build();

    protected final ModelEntity view;
    protected final Bone bone;
    private final BoneModifierMap modifiers = BoneModifierMap.create(this);
    protected final float modelScale;

    private Vector3Float lastPosition = Vector3Float.ZERO;
    private Quaternion lastRotation = Quaternion.IDENTITY;
    private Vector3Float lastScale = Vector3Float.ONE;

    private int color = 0xFFFFFF;

    public BoneEntity(
            ModelEntity view,
            Bone bone,
            Vector3Float initialPosition,
            Quaternion initialRotation,
            float modelScale
    ) {
        super(EntityType.ITEM_DISPLAY);
        this.view = view;
        this.bone = bone;
        this.modelScale = modelScale;
        initialize(initialPosition, initialRotation);
    }

    @Override
    public void setInvisible(boolean invisible) {
        super.setInvisible(invisible);

        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        if (bone.parentOnly() || invisible) {
            meta.setItemStack(ItemStack.AIR);
        } else {
            meta.setItemStack(itemWithCustomData(
                    BASE_HELMET,
                    bone.customModelData()
            ));
        }
    }

    protected void initialize(Vector3Float initialPosition, Quaternion initialRotation) {
        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.THIRDPERSON_LEFT_HAND);
        meta.setTransformationInterpolationDuration(3);
        meta.setViewRange(1000);
        meta.setHasNoGravity(true);
        meta.setSilent(true);

        update(initialPosition, initialRotation, Vector3Float.ONE);
        updateItem();
    }

    @Override
    public void update(@NotNull Vector3Float position, @NotNull Quaternion rotation, @NotNull Vector3Float scale) {
        final var playerBoneModifier = modifiers.getModifier(BoneModifierType.PLAYER_PART);
        if (playerBoneModifier != null) {
            // position however, requires an offset
            final var playerBoneType = playerBoneModifier.type();
            if (playerBoneType != null) {
                position = position.add(0, playerBoneType.offset() / bone.scale(), 0);
            }
            // rotation and scale are not modified
        }

        if (position.equals(lastPosition) && rotation.equals(lastRotation) && scale.equals(lastScale)) {
            // Don't update if everything is the same (avoids marking the data as dirty)
            // todo: we can separate this!
            return;
        }

        lastPosition = position;
        lastRotation = rotation;
        lastScale = scale;

        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setTransformationInterpolationStartDelta(0);
        meta.setTranslation(new Pos(position.x(), position.y(), position.z()).mul(modelScale * bone.scale()));
        meta.setRightRotation(rotation.toFloatArray());
        meta.setScale(new Vec(
                modelScale * bone.scale() * scale.x(),
                modelScale * bone.scale() * scale.y(),
                modelScale * bone.scale() * scale.z()
        ));

        meta.setNotifyAboutChanges(true);
    }

    /**
     * Returns the holder view
     *
     * @return The view for this bone entity
     */
    public ModelEntity view() {
        return view;
    }

    @Override
    public @NotNull Bone bone() {
        return bone;
    }

    /**
     * Colorizes this bone entity using
     * the specified color
     *
     * @param color The new bone color
     */
    @Override
    public void colorize(Color color) {
        final var encoded = color.asRGB();
        if (this.color == encoded) {
            return;
        }
        this.color = encoded;
        updateItem();
    }

    @Override
    public void colorize(final int red, int green, int blue) {
        colorize(new Color(red, green, blue));
    }

    @Override
    public void updateItem() {
        final var playerBoneModifier = modifiers.getModifier(BoneModifierType.PLAYER_PART);
        final Skin skin;
        final PlayerBoneType playerBoneType;
        ItemStack itemStack;
        if (playerBoneModifier != null
                && (skin = playerBoneModifier.skin()) != null
                && (playerBoneType = playerBoneModifier.type()) != null) {
            itemStack = createItemStack(Minecraft.PLAYER_HEAD_ITEM_KEY);

            itemStack = itemStack.with(DataComponents.PROFILE, new HeadProfile(
                    null, // name, no name
                    null, // uuid,
                    List.of(new HeadProfile.Property("textures", skin.value(), skin.signature()))
            ));

            // set the player bone type custom model data
            itemStack = itemWithCustomData(itemStack, skin.type() == Skin.Type.SLIM ? playerBoneType.slimModelData() : playerBoneType.modelData());
        } else {
            itemStack = createItemStack(Hephaestus.BONE_ITEM_KEY);

            // use the bone custom model data
            itemStack = itemWithCustomData(itemStack, bone.customModelData());
        }


        itemStack = itemStack.with(DataComponents.DYED_COLOR, new Color(color));
        ((ItemDisplayMeta) getEntityMeta()).setItemStack(itemStack);
    }

    private static @NotNull ItemStack createItemStack(final @NotNull Key type) {
        return ItemStack.builder(Objects.requireNonNull(Material.fromKey(type))).build();
    }

    @Override
    public @NotNull BoneModifierMap modifiers() {
        return modifiers;
    }

    private ItemStack itemWithCustomData(final @NotNull ItemStack item, final int customModelData) {
        return item.with(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(
                List.of((float) customModelData),
                List.of(),
                List.of(),
                List.of()
        ));
    }
}