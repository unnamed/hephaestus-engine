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

import com.google.common.collect.ImmutableMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.controller.AnimationController;

import java.util.Collections;

@MethodsReturnNonnullByDefault
public class MinecraftModelEntity extends PathfinderMob {

    private static final Access.FieldReflect<EntityDimensions> DIMENSIONS_FIELD = Access.findFieldByType(Entity.class, EntityDimensions.class);

    private final Model model;
    private final ImmutableMap<String, BoneEntity> bones;
    private final AnimationController animationController;

    private final CraftModelEntity bukkitEntity;
    private final EntityDimensions modelDimensions;

    public MinecraftModelEntity(EntityType<? extends PathfinderMob> type, Level world, Model model) {
        super(type, world);
        this.model = model;
        this.bones = instantiateBones();
        this.bukkitEntity = new CraftModelEntity(super.level().getCraftServer(), this);
        this.animationController = AnimationController.create(bukkitEntity);

        Vector2Float bb = model.boundingBox();
        this.modelDimensions = EntityDimensions.scalable(bb.x(), bb.y());

        // set our model dimensions
        DIMENSIONS_FIELD.set(this, modelDimensions);

        // update bounding box
        setPos(0.0D, 0.0D, 0.0D);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return modelDimensions;
    }

    @Override
    public void tick() {
        super.tick();
        bukkitEntity.tickAnimations();
    }

    private ImmutableMap<String, BoneEntity> instantiateBones() {
        // create the bone entities
        ImmutableMap.Builder<String, BoneEntity> bones = ImmutableMap.builder();
        for (Bone bone : model.bones()) {
            instantiateBone(bone, bones);
        }
        return bones.build();
    }

    private void instantiateBone(
            Bone bone,
            ImmutableMap.Builder<String, BoneEntity> into
    ) {
        var entity = new BoneEntity(this, bone);
        entity.setPos(this.position());
        into.put(bone.name(), entity);

        for (var child : bone.children()) {
            instantiateBone(child, into);
        }
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (bones != null) {
            for (final var bone : bones.values()) {
                bone.setPos(x, y, z);
            }
        }
    }

    public Model model() {
        return model;
    }

    public ImmutableMap<String, BoneEntity> bones() {
        return bones;
    }

    public AnimationController animationController() {
        return animationController;
    }

    @Override
    public CraftModelEntity getBukkitEntity() {
        return bukkitEntity;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // no metadata for this entity, it will not
        // be directly spawned anyways
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections::emptyIterator;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public String toString() {
        return "ModelEntity";
    }

}
