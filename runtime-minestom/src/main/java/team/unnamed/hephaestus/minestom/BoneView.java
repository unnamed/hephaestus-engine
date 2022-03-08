/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
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

import net.kyori.adventure.text.Component;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.view.BaseBoneView;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a {@link Bone} holder entity,
 * it is an armor stand with a LEATHER_HORSE_ARMOR
 * item as helmet using a custom model data to
 * apply the bone model
 *
 * @since 1.0.0
 */
public final class BoneView
        extends LivingEntity
        implements BaseBoneView {

    private static final float SMALL_OFFSET = 0.726F;
    private static final float LARGE_OFFSET = 1.452F; // todo: this must be tested

    private static final ItemStack BASE_HELMET =
            ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
                    .meta(new LeatherArmorMeta.Builder()
                            .color(new Color(0xFFFFFF))
                            .build())
                    .build();

    private final ModelView view;
    private final Bone bone;

    // cached height offset, either SMALL_OFFSET
    // or LARGE_OFFSET
    private final float offset;

    public BoneView(
            ModelView view,
            Bone bone
    ) {
        super(EntityType.ARMOR_STAND);
        this.view = view;
        this.bone = bone;
        this.offset = bone.small() ? SMALL_OFFSET : LARGE_OFFSET;
        initialize();
    }

    private void initialize() {
        ArmorStandMeta meta = (ArmorStandMeta) getEntityMeta();
        meta.setSilent(true);
        meta.setHasNoGravity(true);
        meta.setSmall(bone.small());
        meta.setInvisible(true);

        // set helmet with custom model data from our bone
        setHelmet(BASE_HELMET.withMeta(itemMeta ->
                itemMeta.customModelData(bone.customModelData())));
    }

    /**
     * Returns the holder view
     *
     * @return The view for this bone entity
     */
    public ModelView view() {
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
    public void colorize(Color color) {
        setHelmet(getHelmet().withMeta((LeatherArmorMeta.Builder meta) -> meta.color(color)));
    }

    @Override
    public void colorize(int r, int g, int b) {
        colorize(new Color(r, g, b));
    }

    @Override
    public void colorize(int rgb) {
        colorize(new Color(rgb));
    }

    @Override
    public void position(Vector3Float position) {
        teleport(view.getPosition().add(
                position.x(),
                position.y() - offset,
                position.z()
        ));
    }

    @Override
    public void rotation(Vector3Float rotation) {
        ArmorStandMeta meta = (ArmorStandMeta) getEntityMeta();
        meta.setHeadRotation(new Vec(
                Math.toDegrees(rotation.x()),
                Math.toDegrees(rotation.y()),
                Math.toDegrees(rotation.z())
        ));
    }

    @Override
    public @NotNull CompletableFuture<Void> teleport(@NotNull Pos position, long @Nullable [] chunks) {
        return super.teleport(position.sub(0, offset, 0), chunks);
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos pos) {
        return super.setInstance(instance, pos.sub(0, offset, 0));
    }

}
