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
package team.unnamed.hephaestus.minestom;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Hephaestus;
import team.unnamed.hephaestus.Minecraft;
import team.unnamed.hephaestus.util.Quaternion;
import team.unnamed.hephaestus.view.modifier.BoneModifierMap;

public class BoneEntity extends GenericBoneEntity implements BoneModifierMap.Forwarding {

    protected final ModelEntity view;
    protected final Bone bone;
    private final BoneModifierMap modifiers = BoneModifierMap.create(this);
    protected final float modelScale;

    protected Quaternion localRotation = Quaternion.IDENTITY;

    private int color = 0xFFFFFF;

    public BoneEntity(ModelEntity view, Bone bone, Vector3Float initialPosition, Quaternion initialRotation, float modelScale) {
        super(EntityType.ITEM_DISPLAY);
        this.view = view;
        this.bone = bone;
        this.modelScale = modelScale;
        initialize(initialPosition, initialRotation);
    }

    @Override
    public Quaternion localRotation() {
        return localRotation;
    }

    @Override
    public void rotate(Quaternion rotation) {
        this.localRotation = rotation;
    }

    protected void initialize(Vector3Float initialPosition, Quaternion initialRotation) {
        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.THIRD_PERSON_LEFT_HAND);
        meta.setTransformationInterpolationDuration(3);
        meta.setViewRange(1000);
        meta.setHasNoGravity(true);
        meta.setSilent(true);

        update(initialPosition, initialRotation, Vector3Float.ONE);
        updateItem();
    }

    @Override
    public void update(@NotNull Vector3Float position, @NotNull Quaternion rotation, @NotNull Vector3Float scale) {
        position = modifiers.modifyPosition(position);
        rotation = modifiers.modifyRotation(rotation);
        scale = modifiers.modifyScale(scale);

        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setTransformationInterpolationStartDelta(0);
        meta.setTranslation(new Pos(position.x(), position.y(), position.z()).mul(modelScale * bone.scale()));
        meta.setRightRotation(rotation.toFloatArray());
        meta.setScale(new Vec(modelScale * bone.scale() * scale.x(), modelScale * bone.scale() * scale.y(), modelScale * bone.scale() * scale.z()));

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
        if (isInvisible()) {
            ((ItemDisplayMeta) getEntityMeta()).setItemStack(ItemStack.AIR);
        } else {
            final var itemKey = modifiers.modifyItem(Hephaestus.BONE_ITEM_KEY);
            final var components = modifiers.modifyItemTag(CompoundBinaryTag.builder()
                    .putInt(Minecraft.CUSTOM_MODEL_DATA_TAG, bone.customModelData())
                    .putInt(Minecraft.COLOR_TAG, color)
                    .build()
            );

            final var itemNbt = CompoundBinaryTag.builder()
                    .putString("id", itemKey.asString())
                    .putInt("count", 1)
                    .put("components", components)
                    .build();

            ((ItemDisplayMeta) getEntityMeta()).setItemStack(ItemStack.fromItemNBT(itemNbt));
        }
    }

    @Override
    public @NotNull BoneModifierMap modifiers() {
        return modifiers;
    }
}