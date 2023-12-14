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

import com.mojang.datafixers.util.Pair;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.minecraft.core.Rotations;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Minecraft;
import team.unnamed.hephaestus.bukkit.BoneView;

import java.util.List;
import java.util.function.Consumer;

final class BoneEntity
        extends ArmorStand
        implements BoneView {

    private final MinecraftModelEntity view;
    private final Bone bone;

    // synchronization data
    public long lastPx, lastPy, lastPz;
    public boolean dirtyColor;

    BoneEntity(MinecraftModelEntity view, Bone bone) {
        super(EntityType.ARMOR_STAND, view.level());
        this.view = view;
        this.bone = bone;
        this.initialize();
    }

    private void initialize() {

        super.setSilent(true);
        super.setNoGravity(true);
        super.setSmall(true);
        super.setInvisible(true);

        var item = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        var meta = (LeatherArmorMeta) item.getItemMeta();

        meta.setColor(Color.WHITE);
        meta.setCustomModelData(bone.customModelData());
        item.setItemMeta(meta);

        var nmsItem = CraftItemStack.asNMSCopy(item);

        setItemSlot(EquipmentSlot.HEAD, nmsItem, true);
    }

    void show(Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
        packetConsumer.accept(new ClientboundAddEntityPacket(this));
        packetConsumer.accept(new ClientboundSetEntityDataPacket(super.getId(), super.getEntityData().packDirty()));
        packetConsumer.accept(new ClientboundSetEquipmentPacket(super.getId(), List.of(new Pair<>(
                EquipmentSlot.HEAD,
                super.getItemBySlot(EquipmentSlot.HEAD)
        ))));
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
        var nmsItem = super.getItemBySlot(EquipmentSlot.HEAD);

        var item = CraftItemStack.asBukkitCopy(nmsItem);
        var meta = (LeatherArmorMeta) item.getItemMeta();

        Color previous = meta.getColor();
        if (!color.equals(previous)) {
            meta.setColor(color);
            item.setItemMeta(meta);

            nmsItem = CraftItemStack.asNMSCopy(item);

            super.setItemSlot(EquipmentSlot.HEAD, nmsItem);
            this.dirtyColor = true;
        }
    }

    @Override
    public void position(Vector3Float position) {
        Vec3 root = view.position();
        super.setPos(
                root.x + position.x(),
                root.y + position.y(),
                root.z + position.z()
        );
    }

    @Override
    public void rotation(Vector3Float rotation) {
        super.setHeadPose(new Rotations(
                rotation.x(),
                rotation.y(),
                rotation.z()
        ));
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
