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
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.animation.AnimationController;
import team.unnamed.hephaestus.animation.ModelAnimation;
import team.unnamed.hephaestus.util.Vectors;
import team.unnamed.hephaestus.view.ModelInteractListener;
import team.unnamed.hephaestus.view.ModelView;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class MinestomModelView
        extends EntityCreature
        implements ModelView<Player> {

    private final Model model;

    private final Map<String, MinestomBoneView> bones = new ConcurrentHashMap<>();
    private final Collection<MinestomBoneView> seats = new HashSet<>();

    private final AnimationController animationController;
    private ModelInteractListener<Player> interactListener = ModelInteractListener.nop();

    public MinestomModelView(
            EntityType type,
            Model model
    ) {
        super(type);
        this.model = model;
        this.animationController = AnimationController.create(this);

        Vector2Float boundingBox = model.boundingBox();
        setBoundingBox(boundingBox.x(), boundingBox.y(), boundingBox.x());
        setInvisible(true);
        setNoGravity(true);
    }

    @Override
    public Model model() {
        return model;
    }

    @Override
    public Collection<MinestomBoneView> seats() {
        return seats;
    }

    @Override
    public void interactListener(ModelInteractListener<Player> interactListener) {
        this.interactListener = requireNonNull(interactListener, "interactListener");
    }

    ModelInteractListener<Player> interactListener() {
        return interactListener;
    }

    @Override
    public void colorize(int r, int g, int b) {
        Color color = new Color(r, g, b);
        for (MinestomBoneView entity : bones.values()) {
            entity.colorize(color);
        }
    }

    @Override
    public @Nullable MinestomBoneView bone(String name) {
        return bones.get(name);
    }

    @Override
    public AnimationController animationController() {
        return animationController;
    }

    @Override
    public void playAnimation(String animationName, int transitionTicks) {
        ModelAnimation animation = model.animations().get(animationName);
        animationController.queue(animation, transitionTicks);
    }

    @Override
    public void tickAnimations() {
        animationController.tick(Math.toRadians(getPosition().yaw()));
    }

    private void summonBone(double yawRadians, Pos pos, Bone bone, Vector3Float parentOffset) {

        Vector3Float offset = bone.offset().add(parentOffset);
        Vector3Float relativePos = Vectors.rotateAroundY(offset, yawRadians);

        MinestomBoneView entity = new MinestomBoneView(this, bone);
        entity.setInstance(instance, pos.add(
                relativePos.x(),
                relativePos.y(),
                relativePos.z()
        )).join();

        bones.put(bone.name(), entity);

        for (Bone child : bone.children()) {
            summonBone(yawRadians, pos, child, offset);
        }
    }

    private void teleportBone(
            double yawRadians,
            Pos pos,
            Bone bone,
            Vector3Float parentOffset
    ) {
        Vector3Float offset = bone.offset().add(parentOffset);
        Vector3Float relativePosition = Vectors.rotateAroundY(offset, yawRadians);
        Entity entity = bones.get(bone.name());

        if (entity != null) {
            entity.teleport(pos.add(
                    relativePosition.x(),
                    relativePosition.y(),
                    relativePosition.z()
            ));
        }
        for (Bone child : bone.children()) {
            this.teleportBone(yawRadians, pos, child, offset);
        }
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return super.setInstance(instance, spawnPosition)
                .thenAccept(ignored -> {
                    // create the bone entities
                    double yawRadians = Math.toRadians(spawnPosition.yaw());

                    for (Bone bone : model.bones()) {
                        summonBone(yawRadians, spawnPosition, bone, Vector3Float.ZERO);
                    }

                    // after spawning bones, process seats
                    for (Bone seatBone : model.seats()) {
                        MinestomBoneView seatView = bone(seatBone.name());
                        if (seatView != null) {
                            seats.add(seatView);
                        }
                    }
                });
    }

    @Override
    public @NotNull CompletableFuture<Void> teleport(@NotNull Pos position) {
        return super.teleport(position)
                .thenRun(() -> {
                    double yawRadians = Math.toRadians(position.yaw());

                    for (Bone bone : model.bones()) {
                        teleportBone(yawRadians, position, bone, Vector3Float.ZERO);
                    }
                });
    }

}