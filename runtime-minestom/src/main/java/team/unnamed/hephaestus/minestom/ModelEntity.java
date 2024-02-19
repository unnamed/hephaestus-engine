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
package team.unnamed.hephaestus.minestom;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.util.Quaternion;
import team.unnamed.hephaestus.view.AbstractModelView;
import team.unnamed.hephaestus.animation.controller.AnimationPlayer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class ModelEntity extends EntityCreature implements AbstractModelView<Player> {

    protected final Model model;
    protected final float scale;

    protected final Map<String, GenericBoneEntity> bones = new ConcurrentHashMap<>();
    protected final AnimationPlayer animationPlayer;

    private int lastVehicle = -1;

    public ModelEntity(EntityType type, Model model, float scale) {
        super(type);
        this.model = model;
        this.scale = scale;
        this.animationPlayer = AnimationPlayer.create(this);

        // model entity is not auto-viewable by default
        setAutoViewable(false);
        initialize();
    }

    @Override
    public void setAutoViewable(boolean autoViewable) {
        super.setAutoViewable(autoViewable);

        for (GenericBoneEntity bone : bones()) {
            bone.setAutoViewable(autoViewable);
        }
    }

    @Override
    public void setInvisible(boolean invisible) {
        super.setInvisible(invisible);

        for (GenericBoneEntity bone : bones()) {
            bone.setInvisible(invisible);
        }
    }

    private void initialize() {
        Vector2Float boundingBox = model.boundingBox();
        setBoundingBox(boundingBox.x(), boundingBox.y(), boundingBox.x());
        setInvisible(true);
        setNoGravity(true);

        for (Bone bone : model.bones()) {
            createBone(bone, Vector3Float.ZERO, Quaternion.IDENTITY);
        }
    }

    protected void createBone(Bone bone, Vector3Float parentPosition, Quaternion parentRotation) {
        Vector3Float position = bone.position().add(parentPosition);
        Quaternion rotation = parentRotation.multiply(Quaternion.fromEulerDegrees(bone.rotation()));

        BoneEntity boneEntity = new BoneEntity(this, bone, position, rotation, scale);
        bones.put(bone.name(), boneEntity);

        if (bone.isParentOnly()) {
            boneEntity.setInvisible(true);
        }

        for (Bone child : bone.children()) {
            createBone(child, position, rotation);
        }
    }

    @Override
    public Collection<Player> viewers() {
        return super.viewers;
    }

    @Override
    public void emitSound(final @NotNull Sound sound) {
        for (Player viewer : viewers()) {
            viewer.playSound(sound, position);
        }
    }

    @Override
    public @NotNull Model model() {
        return model;
    }

    @Override
    public void colorize(int r, int g, int b) {
        colorize(new Color(r, g, b));
    }

    @Override
    public void colorize(int rgb) {
        colorize(new Color(rgb));
    }

    public void colorize(Color color) {
        for (GenericBoneEntity entity : bones.values()) {
            entity.colorize(color);
        }
    }

    @Override
    public Collection<GenericBoneEntity> bones() {
        return bones.values();
    }

    @Override
    public @Nullable GenericBoneEntity bone(String name) {
        return bones.get(name);
    }

    @Override
    public AnimationPlayer animationController() {
        return animationPlayer;
    }

    @Override
    public void tickAnimations() {
        animationPlayer.tick(position.yaw(), position.pitch());
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        this.tickAnimations();

        if (vehicle != null && vehicle.getEntityId() != lastVehicle) {
            for (GenericBoneEntity bone : bones()) {
                vehicle.addPassenger(bone);
            }

            lastVehicle = vehicle.getEntityId();
        }
    }

    @Override
    public void updateViewableRule(@Nullable Predicate<Player> predicate) {
        super.updateViewableRule(predicate);

        for (GenericBoneEntity boneEntity : bones.values()) {
            boneEntity.updateViewableRule(predicate);
        }
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return super.setInstance(instance, spawnPosition)
                .thenAccept(ignored -> {
                    for (GenericBoneEntity bone : bones()) {
                        bone.setInstance(instance, spawnPosition).join();
                        addPassenger(bone);
                    }
                });
    }

    @Override
    public @NotNull CompletableFuture<Void> teleport(@NotNull Pos position) {
        return super.teleport(position)
                .thenRun(() -> {
                    for (GenericBoneEntity bone : bones()) {
                        bone.teleport(position).join();
                        addPassenger(bone);
                    }
                });
    }

    @Override
    public void remove() {
        super.remove();
        for (GenericBoneEntity bone : bones()) {
            bone.remove();
        }
    }
}