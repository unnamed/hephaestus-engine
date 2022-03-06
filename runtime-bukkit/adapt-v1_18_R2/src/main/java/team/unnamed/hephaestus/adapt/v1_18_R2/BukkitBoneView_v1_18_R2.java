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
package team.unnamed.hephaestus.adapt.v1_18_R2;

import com.mojang.datafixers.util.Pair;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.minecraft.core.Rotations;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.view.BukkitBoneView;
import team.unnamed.hephaestus.view.BukkitModelView;

import java.util.List;

final class BukkitBoneView_v1_18_R2 implements BukkitBoneView {

    private final BukkitModelView view;
    private final Bone bone;

    private final BoneArmorStand entity;

    BukkitBoneView_v1_18_R2(BukkitModelView view, Bone bone) {
        this.view = view;
        this.bone = bone;
        this.entity = new BoneArmorStand(((CraftWorld) view.location().getWorld()).getHandle());
        this.initialize();
    }

    private void initialize() {

        Location rootLocation = view.location();
        entity.setRot(rootLocation.getYaw(), rootLocation.getPitch());

        entity.setSilent(true);
        entity.setNoGravity(true);
        entity.setSmall(true);
        entity.setInvisible(true);

        var item = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        var meta = (LeatherArmorMeta) item.getItemMeta();

        // noinspection ConstantConditions
        meta.setColor(Color.WHITE);
        meta.setCustomModelData(bone.customModelData());
        item.setItemMeta(meta);

        var nmsItem = CraftItemStack.asNMSCopy(item);

        entity.setItemSlot(EquipmentSlot.HEAD, nmsItem, true);
    }

    void show(Player player) {
        Packets.send(
                player,
                new ClientboundAddMobPacket(entity),
                new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData(), true),
                new ClientboundSetEquipmentPacket(entity.getId(), List.of(new Pair<>(
                        EquipmentSlot.HEAD,
                        entity.getItemBySlot(EquipmentSlot.HEAD)
                )))
        );
    }

    void hide(Player player) {
        Packets.send(player, new ClientboundRemoveEntitiesPacket(entity.getId()));
    }

    @Override
    public Bone bone() {
        return bone;
    }

    @Override
    public Component customName() {
        return fromMinecraft(entity.getCustomName());
    }

    @Override
    public void customName(Component customName) {
        entity.setCustomName(toMinecraft(customName));
    }

    @Override
    public void customNameVisible(boolean visible) {
        entity.setCustomNameVisible(visible);
    }

    @Override
    public boolean customNameVisible() {
        return entity.isCustomNameVisible();
    }

    @Override
    public void colorize(Color color) {
        // todo: we could avoid bukkit<->nms item conversions
        var nmsItem = entity.getItemBySlot(EquipmentSlot.HEAD);

        var item = nmsItem == null
                ? new ItemStack(Material.LEATHER_HORSE_ARMOR)
                : CraftItemStack.asBukkitCopy(nmsItem);
        var meta = (LeatherArmorMeta) item.getItemMeta();

        //noinspection ConstantConditions
        meta.setColor(color);
        item.setItemMeta(meta);

        nmsItem = CraftItemStack.asNMSCopy(item);

        entity.setItemSlot(EquipmentSlot.HEAD, nmsItem);

        Packets.send(
                view.viewers(),
                new ClientboundSetEquipmentPacket(
                        entity.getId(),
                        List.of(new Pair<>(
                                EquipmentSlot.HEAD,
                                nmsItem
                        ))
                )
        );
    }

    @Override
    public void position(Vector3Float position) {
        Location rootLocation = view.location();
        entity.setPos(
                rootLocation.getX() + position.x(),
                rootLocation.getY() + position.y(),
                rootLocation.getZ() + position.z()
        );
        Packets.send(view.viewers(), new ClientboundTeleportEntityPacket(entity));
    }

    @Override
    public void rotation(Vector3Float rotation) {
        var watcher = new SynchedEntityData(entity);
        watcher.define(
                EntityDataSerializers.ROTATIONS.createAccessor(16),
                new Rotations(
                        (float) Math.toDegrees(rotation.x()),
                        (float) Math.toDegrees(rotation.y()),
                        (float) Math.toDegrees(rotation.z())
                )
        );
        Packets.send(view.viewers(), new ClientboundSetEntityDataPacket(entity.getId(), watcher, true));
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
