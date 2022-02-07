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
package team.unnamed.hephaestus.adapt.v1_18_R1;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Rotations;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.util.Vectors;
import team.unnamed.hephaestus.view.BukkitModelView;
import team.unnamed.hephaestus.view.ModelViewController;

import java.util.List;

public class ModelViewController_v1_18_R1
        implements ModelViewController {

    private static class BoneArmorStand extends ArmorStand {

        public BoneArmorStand(Level level) {
            super(EntityType.ARMOR_STAND, level);
        }

        @Override
        public void setRot(float yRot, float xRot) { // makes setRot accessible
            super.setRot(yRot, xRot);
        }

    }

    private void summonBone(
            double yawRadians,
            BukkitModelView view,
            Location location,
            Bone bone,
            Vector3Float offset
    ) {
        // location computing
        var position = bone.offset().add(offset);
        var relativePos = Vectors.rotateAroundY(position, yawRadians);

        // spawning the bone armorstand
        // noinspection ConstantConditions
        var level = ((CraftWorld) location.getWorld()).getHandle();
        var entity = new BoneArmorStand(level);

        entity.setPos(
                location.getX() + relativePos.x(),
                location.getY() + relativePos.y(),
                location.getZ() + relativePos.z()
        );
        entity.setRot(location.getYaw(), location.getPitch());

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

        Packets.send(
                view.getViewers(),
                new ClientboundAddMobPacket(entity),
                new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData(), true),
                new ClientboundSetEquipmentPacket(entity.getId(), List.of(new Pair<>(
                        EquipmentSlot.HEAD,
                        nmsItem
                )))
        );

        view.getEntities().put(bone.name(), entity);

        for (var child : bone.children()) {
            summonBone(
                    yawRadians,
                    view,
                    location,
                    child,
                    position
            );
        }
    }

    @Override
    public void show(BukkitModelView view) {
        var location = view.getLocation();
        var yawRadians = Math.toRadians(location.getYaw());
        for (var bone : view.model().bones()) {
            summonBone(yawRadians, view, location, bone, Vector3Float.ZERO);
        }
    }

    private void teleportBonesRecursively(
            double yawRadians,
            BukkitModelView view,
            Location location,
            Bone bone,
            Vector3Float offset
    ) {
        // location computing
        var position = bone.offset().add(offset);
        var relativePos = Vectors.rotateAroundY(position, yawRadians);

        var entity = (BoneArmorStand) view.getEntities().get(bone.name());
        entity.setPos(
                location.getX() + relativePos.x(),
                location.getY() + relativePos.y(),
                location.getZ() + relativePos.z()
        );
        entity.setRot(
                location.getYaw(),
                location.getPitch()
        );

        Packets.send(
                view.getViewers(),
                new ClientboundTeleportEntityPacket(entity)
        );

        for (var child : bone.children()) {
            teleportBonesRecursively(
                    yawRadians,
                    view,
                    location,
                    child,
                    position
            );
        }
    }

    @Override
    public void teleport(BukkitModelView view, Location location) {
        var yaw = Math.toRadians(location.getYaw());
        for (var bone : view.model().bones()) {
            teleportBonesRecursively(yaw, view, location, bone, Vector3Float.ZERO);
        }
    }

    private void hideBone(BukkitModelView view, Bone bone) {
        var entity = (BoneArmorStand) view.getEntities().get(bone.name());
        Packets.send(view.getViewers(), new ClientboundRemoveEntitiesPacket(entity.getId()));
        for (var child : bone.children()) {
            hideBone(view, child);
        }
    }

    @Override
    public void hide(BukkitModelView view) {
        for (var bone : view.model().bones()) {
            hideBone(view, bone);
        }
    }

    private void colorizeBoneAndChildren(BukkitModelView view, Bone bone, Color color) {
        colorizeBone(view, bone.name(), color);
        for (var child : bone.children()) {
            colorizeBoneAndChildren(view, child, color);
        }
    }

    @Override
    public void colorizeBone(BukkitModelView view, String boneName, Color color) {
        var entity = (BoneArmorStand) view.getEntities().get(boneName);
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
                view.getViewers(),
                new ClientboundSetEquipmentPacket(entity.getId(), List.of(new Pair<>(
                        EquipmentSlot.HEAD,
                        nmsItem
                )))
        );
    }

    @Override
    public void colorize(BukkitModelView view, Color color) {
        for (var bone : view.model().bones()) {
            colorizeBoneAndChildren(view, bone, color);
        }
    }

    @Override
    public void teleportBone(BukkitModelView view, String boneName, Location location) {
        var entity = (BoneArmorStand) view.getEntities().get(boneName);
        entity.setPos(
                location.getX(),
                location.getY(),
                location.getZ()
        );
        entity.setRot(
                location.getYaw(),
                location.getPitch()
        );
        Packets.send(view.getViewers(), new ClientboundTeleportEntityPacket(entity));
    }

    @Override
    public void updateBoneModelData(BukkitModelView view, Bone bone, int modelData) {

        var entity = (BoneArmorStand) view.getEntities().get(bone.name());
        var nmsItem = entity.getItemBySlot(EquipmentSlot.HEAD);

        var item = nmsItem == null
                ? new ItemStack(Material.LEATHER_HORSE_ARMOR)
                : CraftItemStack.asBukkitCopy(nmsItem);
        var meta = (LeatherArmorMeta) item.getItemMeta();

        // noinspection ConstantConditions
        meta.setCustomModelData(modelData);
        if (nmsItem == null) {
            meta.setColor(Color.WHITE);
        }
        item.setItemMeta(meta);

        nmsItem = CraftItemStack.asNMSCopy(item);
        entity.setItemSlot(EquipmentSlot.HEAD, nmsItem, true);

        Packets.send(
                view.getViewers(),
                new ClientboundSetEquipmentPacket(entity.getId(), List.of(new Pair<>(
                        EquipmentSlot.HEAD,
                        nmsItem
                )))
        );
    }

    @Override
    public void setBonePose(BukkitModelView view, String boneName, Vector3Float angle) {
        var entity = (BoneArmorStand) view.getEntities().get(boneName);
        var watcher = new SynchedEntityData(entity);
        watcher.define(
                EntityDataSerializers.ROTATIONS.createAccessor(16),
                new Rotations(angle.x(), angle.y(), angle.z())
        );
        Packets.send(view.getViewers(), new ClientboundSetEntityDataPacket(entity.getId(), watcher, true));
    }

    private void showBoneIndividually(
            BukkitModelView view,
            Bone bone,
            Player player
    ) {
        var entity = (BoneArmorStand) view.getEntities().get(bone.name());

        Packets.send(
                player,
                new ClientboundAddMobPacket(entity),
                new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData(), true),
                new ClientboundSetEquipmentPacket(entity.getId(), List.of(new Pair<>(
                        EquipmentSlot.HEAD,
                        entity.getItemBySlot(EquipmentSlot.HEAD)
                )))
        );

        for (var child : bone.children()) {
            showBoneIndividually(view, child, player);
        }
    }

    private void hideBoneIndividually(
            BukkitModelView view,
            Bone bone,
            Player player
    ) {
        var entity = (BoneArmorStand) view.getEntities().get(bone.name());
        Packets.send(player, new ClientboundRemoveEntitiesPacket(entity.getId()));

        for (var child : bone.children()) {
            hideBoneIndividually(view, child, player);
        }
    }

    @Override
    public void showIndividually(BukkitModelView view, Player player) {
        for (var bone : view.model().bones()) {
            showBoneIndividually(view, bone, player);
        }
    }

    @Override
    public void hideIndividually(BukkitModelView view, Player player) {
        for (var bone : view.model().bones()) {
            hideBoneIndividually(view, bone, player);
        }
    }

}