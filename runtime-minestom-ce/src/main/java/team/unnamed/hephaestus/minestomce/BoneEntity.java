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
package team.unnamed.hephaestus.minestomce;

import net.kyori.adventure.text.Component;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.util.Quaternion;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a {@link Bone} holder entity,
 * it is an armor stand with a LEATHER_HORSE_ARMOR
 * item as helmet using a custom model data to
 * apply the bone model
 *
 * @since 1.0.0
 */
public final class BoneEntity extends GenericBoneEntity {

    private static final ItemStack BASE_HELMET = ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
                    .meta(new LeatherArmorMeta.Builder()
                            .color(new Color(0xFFFFFF))
                            .build()
                    )
                    .build();

    private final ModelEntity view;
    private final Bone bone;

    public BoneEntity(
            ModelEntity view,
            Bone bone,
            Vector3Float initialPosition,
            float scale
    ) {
        super(EntityType.ITEM_DISPLAY);
        this.view = view;
        this.bone = bone;
        initialize(initialPosition, scale);
    }

    private void initialize(Vector3Float initialPosition, float scale) {
        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        meta.setScale(new Vec(scale, scale, scale));
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.THIRD_PERSON_LEFT_HAND);
        meta.setInterpolationDuration(3);
        meta.setViewRange(1000);
        meta.setHasNoGravity(true);

        meta.setItemStack(BASE_HELMET.withMeta(itemMeta ->
                itemMeta.customModelData(bone.customModelData())));

        position(initialPosition);
    }

    @Override
    public void position(Vector3Float position) {
        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        meta.setTranslation(new Pos(position.x(), position.y(), position.z()).mul(2).mul(meta.getScale()));
    }

    @Override
    public void rotation(Quaternion rotation) {
        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setInterpolationStartDelta(0);
        meta.setRightRotation(rotation.toFloatArray());
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
    public Bone bone() {
        return bone;
    }

    @Override
    public void customName(Component displayName) {
        super.setCustomName(displayName);
    }

    @Override
    public Component customName() {
        return super.getCustomName();
    }

    @Override
    public void customNameVisible(boolean visible) {
        super.setCustomNameVisible(visible);
    }

    @Override
    public boolean customNameVisible() {
        return super.isCustomNameVisible();
    }

    /**
     * Colorizes this bone entity using
     * the specified color
     *
     * @param color The new bone color
     */
    @Override
    public void colorize(Color color) {
        ItemDisplayMeta displayMeta = (ItemDisplayMeta) getEntityMeta();
        displayMeta.setItemStack(displayMeta.getItemStack().withMeta(LeatherArmorMeta.class, meta -> meta.color(color)));
    }

    @Override
    public void colorize(int r, int g, int b) {
        colorize(new Color(r, g, b));
    }

    @Override
    public void colorize(int rgb) {
        colorize(new Color(rgb));
    }
}
