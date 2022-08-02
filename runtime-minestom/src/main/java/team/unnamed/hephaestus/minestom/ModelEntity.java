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

import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.animation.AnimationController;
import team.unnamed.hephaestus.util.Vectors;
import team.unnamed.hephaestus.view.BaseModelView;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ModelEntity
        extends EntityCreature
        implements BaseModelView {

    private final Model model;

    private final Map<String, BoneEntity> bones = new ConcurrentHashMap<>();

    private final AnimationController animationController;

    public ModelEntity(
            EntityType type,
            Model model
    ) {
        super(type);
        this.model = model;
        this.animationController = AnimationController.create(this);
        initialize();
    }

    private void initialize() {
        Vector2Float boundingBox = model.boundingBox();
        setBoundingBox(boundingBox.x(), boundingBox.y(), boundingBox.x());
        setInvisible(true);
        setNoGravity(true);

        for (Bone bone : model.bones()) {
            createBone(bone);
        }
    }

    private void createBone(Bone bone) {
        bones.put(bone.name(), new BoneEntity(this, bone));
        for (Bone child : bone.children()) {
            createBone(child);
        }
    }

    @Override
    public Model model() {
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
        for (BoneEntity entity : bones.values()) {
            entity.colorize(color);
        }
    }

    @Override
    public Collection<BoneEntity> bones() {
        return bones.values();
    }

    @Override
    public @Nullable BoneEntity bone(String name) {
        return bones.get(name);
    }

    @Override
    public AnimationController animationController() {
        return animationController;
    }

    @Override
    public void tickAnimations() {
        animationController.tick(Math.toRadians(getPosition().yaw()));
    }

    private void setBoneInstance(
            double yawRadians,
            Bone bone,
            Vector3Float parentPosition
    ) {
        Vector3Float position = bone.position().add(parentPosition);
        Vector3Float rotatedPosition = Vectors.rotateAroundY(position, yawRadians);

        BoneEntity entity = bone(bone.name());
        if (entity != null) {
            entity.setInstance(instance, super.position.add(
                    rotatedPosition.x(),
                    rotatedPosition.y(),
                    rotatedPosition.z()
            )).join();
        }

        for (Bone child : bone.children()) {
            setBoneInstance(yawRadians, child, position);
        }
    }

    private void teleportBone(
            double yawRadians,
            Bone bone,
            Vector3Float parentPosition
    ) {
        Vector3Float position = bone.position().add(parentPosition);
        Vector3Float rotatedPosition = Vectors.rotateAroundY(position, yawRadians);
        Entity entity = bones.get(bone.name());

        if (entity != null) {
            entity.teleport(super.position.add(
                    rotatedPosition.x(),
                    rotatedPosition.y(),
                    rotatedPosition.z()
            )).join();
        }
        for (Bone child : bone.children()) {
            this.teleportBone(yawRadians, child, position);
        }
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos position) {
        return super.setInstance(instance, position)
                .thenAccept(ignored -> {
                    double yawRadians = Math.toRadians(position.yaw());
                    for (Bone bone : model.bones()) {
                        setBoneInstance(yawRadians, bone, Vector3Float.ZERO);
                    }
                });
    }

    @Override
    public @NotNull CompletableFuture<Void> teleport(@NotNull Pos position) {
        return super.teleport(position)
                .thenRun(() -> {
                    double yawRadians = Math.toRadians(position.yaw());
                    for (Bone bone : model.bones()) {
                        teleportBone(yawRadians, bone, Vector3Float.ZERO);
                    }
                });
    }

}