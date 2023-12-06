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
package team.unnamed.hephaestus.minestomce.playermodel;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.minestomce.BoneEntity;
import team.unnamed.hephaestus.minestomce.GenericBoneEntity;
import team.unnamed.hephaestus.minestomce.ModelEntity;
import team.unnamed.hephaestus.player.PlayerBoneType;
import team.unnamed.hephaestus.player.PlayerModel;
import team.unnamed.hephaestus.util.Quaternion;

import java.util.concurrent.CompletableFuture;

public class PlayerModelEntity extends ModelEntity {

    public PlayerModelEntity(EntityType type, PlayerModel model, float scale) {
        super(type, model, scale);
    }

    @Override
    public PlayerModel model() {
        return (PlayerModel) super.model();
    }

    @Override
    public void tickAnimations() {
        animationController.tick(position.yaw(), position.pitch());
    }

    @Override
    protected void createBone(Bone bone, Vector3Float parentPosition, Quaternion parentRotation) {
        Vector3Float position = bone.position().add(parentPosition);
        Quaternion rotation = parentRotation.multiply(Quaternion.fromEulerDegrees(bone.rotation()));

        PlayerBoneType boneType = PlayerBoneType.matchFor(model().skin(), bone.name());
        GenericBoneEntity boneEntity;

        if (boneType != null) {
            boneEntity = new PlayerBoneEntity(this, bone, position, rotation, scale);
        } else {
            boneEntity = new BoneEntity(this, bone, position, rotation, scale);
        }

        bones.put(bone.name(), boneEntity);

        for (Bone child : bone.children()) {
            createBone(child, position, rotation);
        }
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {

        return super.setInstance(instance, spawnPosition)
                .thenAccept(ignored -> {
                    for (GenericBoneEntity bone : bones()) {
                        bone.setInstance(instance, position.withView(0, 0));
                    }
                });
    }

    @Override
    public @NotNull CompletableFuture<Void> teleport(@NotNull Pos position) {
        return super.teleport(position).thenAccept(ignored -> {
            for (GenericBoneEntity bone : bones()) {
                bone.teleport(position.withView(0, 0));
            }
        });
    }

    @Override
    public void setView(float yaw, float pitch) {
        super.setView(yaw, pitch);

        for (GenericBoneEntity bone : bones()) {
            bone.setView(0, 0);
        }
    }
}
