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
package team.unnamed.hephaestus.bukkit.v1_20_R2;

import com.mojang.math.Transformation;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.bukkit.BoneView;
import team.unnamed.hephaestus.util.Quaternion;

import java.util.function.Consumer;

final class BoneEntity
        extends Display.ItemDisplay
        implements BoneView {

    private final MinecraftModelEntity view;
    private final Bone bone;

    // synchronization data
    public long lastPx, lastPy, lastPz;
    public boolean dirtyColor;

    BoneEntity(MinecraftModelEntity view, Bone bone) {
        super(EntityType.ITEM_DISPLAY, view.level());
        this.view = view;
        this.bone = bone;
        this.initialize();
    }

    private void initialize() {
        setItemTransform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        setTransformationInterpolationDuration(3);
        setViewRange(1000);
        setNoGravity(true);

        final var item = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        final var meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.WHITE);
        meta.setCustomModelData(bone.customModelData());
        setItemStack(CraftItemStack.asNMSCopy(item));

        update(Vector3Float.ZERO, Quaternion.IDENTITY, Vector3Float.ONE);
    }

    @Override
    public void update(Vector3Float position, Quaternion rotation, Vector3Float scale) {
        final var translation = position.multiply(bone.scale());

        setTransformationInterpolationDelay(0);
        setTransformation(new Transformation(
                new Vector3f(translation.x(), translation.y(), translation.z()),
                null,
                new Vector3f(
                        bone.scale() * scale.x(),
                        bone.scale() * scale.y(),
                        bone.scale() * scale.z()
                ),
                new Quaternionf(
                        rotation.x(),
                        rotation.y(),
                        rotation.z(),
                        rotation.w()
                )
        ));
    }

    void show(Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
        packetConsumer.accept(new ClientboundAddEntityPacket(this));
        packetConsumer.accept(new ClientboundSetEntityDataPacket(super.getId(), super.getEntityData().packDirty()));
    }

    @Override
    public Bone bone() {
        return bone;
    }

    @Override
    public Component customName() {
        return fromMinecraft(super.getCustomName());
    }

    @Override
    public void customName(Component customName) {
        super.setCustomName(toMinecraft(customName));
    }

    @Override
    public void customNameVisible(boolean visible) {
        super.setCustomNameVisible(visible);
    }

    @Override
    public boolean customNameVisible() {
        return super.isCustomNameVisible();
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

    @SuppressWarnings("UnstableApiUsage")
    private static net.minecraft.network.chat.Component toMinecraft(Component component) {
        return (net.minecraft.network.chat.Component) MinecraftComponentSerializer.get().serialize(component);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static Component fromMinecraft(net.minecraft.network.chat.Component component) {
        return MinecraftComponentSerializer.get().deserialize(component);
    }

}
