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

import com.google.common.collect.ImmutableMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.controller.AnimationPlayer;
import team.unnamed.hephaestus.util.Quaternion;

import java.util.Collections;
import java.util.Objects;

// TODO: Extend Interaction hitbox from BBModel hitbox
@MethodsReturnNonnullByDefault
public class MinecraftModelEntity extends Interaction {

    private static final Access.FieldReflect<EntityDimensions> DIMENSIONS_FIELD = Access.findFieldByType(Entity.class, EntityDimensions.class);

    protected final Model model;
    private final ImmutableMap<String, team.unnamed.hephaestus.bukkit.v1_20_R3.BoneEntity> bones;
    protected final AnimationPlayer animationController;

    private final team.unnamed.hephaestus.bukkit.v1_20_R3.CraftModelEntity bukkitEntity;
    private final EntityDimensions modelDimensions;

    protected final float scale;

    public MinecraftModelEntity(Model model, Location initial, float scale) {
        super(EntityType.INTERACTION, ((CraftWorld) initial.getWorld()).getHandle());
        this.model = model;
        this.scale = scale;
        this.bones = instantiateBones();
        this.bukkitEntity = new CraftModelEntity(level().getCraftServer(), this);
        this.animationController = AnimationPlayer.create(bukkitEntity);

        Vector2Float bb = model.boundingBox();
        this.modelDimensions = EntityDimensions.scalable(bb.x(), bb.y());

        // set our model dimensions
        DIMENSIONS_FIELD.set(this, modelDimensions);

        // update bounding box
        setPos(initial.getX(), initial.getY(), initial.getZ());
        setYRot(initial.getYaw());
        setXRot(initial.getPitch());
        teleportTo(initial.getX(), initial.getY(), initial.getZ());
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return modelDimensions;
    }

    @Override
    public void tick() {
        super.tick();
        animationTick();
    }

    protected void animationTick() {
        this.animationController.tick();
    }

    private ImmutableMap<String, team.unnamed.hephaestus.bukkit.v1_20_R3.BoneEntity> instantiateBones() {
        // create the bone entities
        ImmutableMap.Builder<String, team.unnamed.hephaestus.bukkit.v1_20_R3.BoneEntity> bones = ImmutableMap.builder();
        for (Bone bone : model.bones()) {
            instantiateBone(bone, Vector3Float.ZERO, bones);
        }
        return bones.build();
    }

    protected void instantiateBone(
            Bone bone,
            Vector3Float parentPosition,
            ImmutableMap.Builder<String, team.unnamed.hephaestus.bukkit.v1_20_R3.BoneEntity> into
    ) {
        var position = bone.position().add(parentPosition);
        var entity = new team.unnamed.hephaestus.bukkit.v1_20_R3.BoneEntity(this, bone, position, Quaternion.IDENTITY.multiply(Quaternion.fromEulerDegrees(bone.rotation())), scale);
        into.put(bone.name(), entity);

        for (var child : bone.children()) {
            instantiateBone(child, position, into);
        }
    }

    public Model model() {
        return model;
    }

    public ImmutableMap<String, team.unnamed.hephaestus.bukkit.v1_20_R3.BoneEntity> bones() {
        return bones;
    }

    public AnimationPlayer animationController() {
        return animationController;
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (model != null) { // model is null when setPos is called by the Entity constructor
            for (Bone bone : model.bones()) {
                var entity = bones.get(bone.name());
                Objects.requireNonNull(entity, "Unknown bone");
                entity.setPos(x, y, z);
            }
        }
    }

    @Override
    public CraftModelEntity getBukkitEntity() {
        return bukkitEntity;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections::emptyIterator;
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return null;
    }

    @Override
    public String toString() {
        return "ModelEntity";
    }

}
