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
package team.unnamed.hephaestus.bukkit.v1_20_R3;

import com.mojang.math.Transformation;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.bukkit.BoneView;
import team.unnamed.hephaestus.modifier.BoneModifier;
import team.unnamed.hephaestus.util.Quaternion;

import java.util.List;
import java.util.function.Consumer;

class BoneEntity extends Display.ItemDisplay implements BoneView {
    protected final ModelViewImpl view;
    protected final Bone bone;

    private BoneModifier modifier = null;

    private final float modelScale;
    protected List<SynchedEntityData.DataValue<?>> initialData;

    private Vector3Float lastPosition;
    private Quaternion lastRotation;
    private Vector3Float lastScale;

    public BoneEntity(ModelViewImpl view, Bone bone, Vector3Float initialPosition, Quaternion initialRotation, float modelScale) {
        //noinspection DataFlowIssue
        super(EntityType.ITEM_DISPLAY, null);
        this.view = view;
        this.bone = bone;
        this.modelScale = modelScale;
        this.initialize(initialPosition, initialRotation);
    }

    protected void initialize(Vector3Float initialPosition, Quaternion initialRotation) {
        setItemTransform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        setTransformationInterpolationDuration(3);
        setViewRange(1000);
        setNoGravity(false);

        update(initialPosition, initialRotation, Vector3Float.ONE);

        var item = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        var meta = (LeatherArmorMeta) item.getItemMeta();

        meta.setColor(Color.WHITE);
        meta.setCustomModelData(bone.customModelData());
        item.setItemMeta(meta);

        var nmsItem = CraftItemStack.asNMSCopy(item);

        setItemStack(nmsItem);
        initialData = super.getEntityData().packDirty();
    }

    protected void show(Consumer<? super Packet<? extends PacketListener>> packetConsumer) {
        final var viewLocation = view.location();
        packetConsumer.accept(new ClientboundAddEntityPacket(
                entityId(),
                getUUID(),
                viewLocation.x(), // Location is the same as view
                viewLocation.y(), // Location is the same as view
                viewLocation.z(), // Location is the same as view
                0, // pitch: We use display rotation instead of entity rotation
                0, // yaw: We use display rotation instead of entity rotation
                EntityType.ITEM_DISPLAY, // item display
                0, // entity data: unused
                Vec3.ZERO, // velocity: unused
                0 // head yaw: We don't use this
        ));
        ClientboundSetEntityDataPacket t = new ClientboundSetEntityDataPacket(super.getId(), initialData);
        packetConsumer.accept(t);
    }

    /**
     * Send the dirty data of this entity to the given packet consumer,
     * if there's any dirty data to send.
     *
     * @param packetConsumer The packet consumer to send the dirty data to
     * @return Whether there was any dirty data to send
     */
    public boolean sendDirtyData(final @NotNull Consumer<? super Packet<?>> packetConsumer) {
        final var dirtyData = getEntityData().packDirty();
        if (dirtyData != null) {
            packetConsumer.accept(new ClientboundSetEntityDataPacket(getId(), dirtyData));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int entityId() {
        return this.getId();
    }

    @Override
    public Bone bone() {
        return bone;
    }

    @Override
    public @Nullable BoneModifier modifier() {
        return modifier;
    }

    @Override
    public void modifier(final @Nullable BoneModifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public void update(@NotNull Vector3Float position, @NotNull Quaternion rotation, @NotNull Vector3Float scale) {
        if (modifier != null) {
            position = modifier.modifyPosition(position);
            rotation = modifier.modifyRotation(rotation);
            scale = modifier.modifyScale(scale);
        }

        if (position.equals(lastPosition) && rotation.equals(lastRotation) && scale.equals(lastScale)) {
            // Don't update if everything is the same (avoids marking the data as dirty)
            // todo: we can separate this!
            return;
        }

        lastPosition = position;
        lastRotation = rotation;
        lastScale = scale;

        // Changes are not immediate, packets are sent by the base entity tracker
        setTransformation(new Transformation(
                modifyTranslation(new Vector3f(position.x(), position.y(), position.z()).mul(modelScale * bone.scale())),
                null,
                new Vector3f(
                        modelScale * bone.scale() * scale.x(),
                        modelScale * bone.scale() * scale.y(),
                        modelScale * bone.scale() * scale.z()
                ),
                new Quaternionf(
                        rotation.x(),
                        rotation.y(),
                        rotation.z(),
                        rotation.w()
                )
        ));
        setTransformationInterpolationDelay(0);
    }

    protected Vector3f modifyTranslation(Vector3f translation) {
        return translation;
    }

    @Override
    public void colorize(final @NotNull Color color) {
        // todo: we could avoid bukkit<->nms item conversions
        var nmsItem = getItemStack();

        var item = CraftItemStack.asBukkitCopy(nmsItem);
        var meta = (LeatherArmorMeta) item.getItemMeta();

        Color previous = meta.getColor();
        if (!color.equals(previous)) {
            meta.setColor(color);
            item.setItemMeta(meta);

            nmsItem = CraftItemStack.asNMSCopy(item);

            setItemStack(nmsItem);
        }
    }
}
