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
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.bukkit.BoneView;
import team.unnamed.hephaestus.util.Quaternion;

import java.util.List;
import java.util.function.Consumer;

class BoneEntity
        extends Display.ItemDisplay
        implements BoneView {

    protected final MinecraftModelEntity view;
    protected final Bone bone;
    public boolean dirtyColor;

    private final float modelScale;
    protected List<SynchedEntityData.DataValue<?>> initialData;

    private Vector3Float lastPosition;
    private Quaternion lastRotation;
    private Vector3Float lastScale;


    public BoneEntity(MinecraftModelEntity view, Bone bone,
                      Vector3Float initialPosition,
                      Quaternion initialRotation,
                      float modelScale
    ) {
        super(EntityType.ITEM_DISPLAY, view.level());
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

    protected void show(Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
        packetConsumer.accept(new ClientboundAddEntityPacket(getId(), getUUID(),
                position().x,
                position().y,
                position().z,
                getXRot(),
                getYRot(),
                getType(),
                0,
                getDeltaMovement(),
                getYHeadRot()
        ));
        ClientboundSetEntityDataPacket t = new ClientboundSetEntityDataPacket(super.getId(), initialData);
        packetConsumer.accept(t);
    }

    @Override
    public Bone bone() {
        return bone;
    }
    @Override
    public void update(Vector3Float position, Quaternion rotation, Vector3Float scale) {
        if (tickCount % 3 != 0) return;
        if (position.equals(this.lastPosition) && rotation.equals(this.lastRotation) && scale.equals(this.lastScale))
            return;
        lastPosition = position;
        lastRotation = rotation;
        lastScale = scale;
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
    public void colorize(Color color) {
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
            this.dirtyColor = true;
        }
    }

    @Override
    public @NotNull Vec3 position() {
        return view.position();
    }

    @Override
    public float getYRot() {
        return 0;
    }

    @Override
    public float getXRot() {
        return 0;
    }
}
