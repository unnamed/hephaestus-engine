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
package team.unnamed.hephaestus.bukkit;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.AnimationController;
import team.unnamed.hephaestus.animation.ModelAnimation;
import team.unnamed.hephaestus.util.Vectors;
import team.unnamed.hephaestus.view.BaseModelView;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class ModelView implements BaseModelView {

    private final Model model;

    private final Map<String, BoneView> bones = new ConcurrentHashMap<>();

    private final ModelViewController controller;
    private final AnimationController animationController;
    private ModelInteractListener interactListener = ModelInteractListener.NOP;

    private final Collection<Player> viewers = new HashSet<>();
    private Location location;

    @ApiStatus.Internal
    public ModelView(
            ModelViewController controller,
            Model model,
            Location location
    ) {
        this.controller = controller;
        this.animationController = AnimationController.create(this);

        this.model = model;
        this.location = location;
        this.summonBones();
    }

    private void summonBones() {
        // create the bone entities
        double yawRadians = Math.toRadians(location.getYaw());

        for (Bone bone : model.bones()) {
            summonBone(yawRadians, bone, Vector3Float.ZERO);
        }
    }

    private void summonBone(
            double yawRadians,
            Bone bone,
            Vector3Float parentPosition
    ) {
        Vector3Float position = bone.position().add(parentPosition);

        BoneView entity = controller.createBone(this, bone);
        entity.position(Vectors.rotateAroundY(position, yawRadians));
        bones.put(bone.name(), entity);

        for (Bone child : bone.children()) {
            summonBone(yawRadians, child, position);
        }
    }

    public void interactListener(ModelInteractListener interactListener) {
        this.interactListener = requireNonNull(interactListener, "interactListener");
    }

    public ModelInteractListener interactListener() {
        return interactListener;
    }

    public Collection<BoneView> bones() {
        return bones.values();
    }

    @Override
    public AnimationController animationController() {
        return animationController;
    }

    @Override
    public Model model() {
        return model;
    }

    @Override
    public void colorize(int r, int g, int b) {
        for (BoneView view : bones.values()) {
            view.colorize(r, g, b);
        }
    }

    @Override
    public void colorize(int rgb) {
        colorize(Color.fromRGB(rgb));
    }

    public void colorize(Color color) {
        for (BoneView view : bones.values()) {
            view.colorize(color);
        }
    }

    @Override
    public @Nullable BoneView bone(String name) {
        return bones.get(name);
    }

    @Override
    public void playAnimation(String name, int transitionTicks) {
        ModelAnimation animation = model.animations().get(name);
        animationController.queue(animation, transitionTicks);
    }

    @Override
    public void tickAnimations() {
        animationController.tick(Math.toRadians(location.getYaw()));
    }

    public Collection<Player> viewers() {
        return viewers;
    }

    public Location location() {
        return location;
    }

    /**
     * Adds the given {@link Player} as viewer if they
     * are not already viewers, and if they were not,
     * shows the view ({@link ModelViewController#show})
     *
     * @param player The added viewer
     * @return True if the player was added as viewer,
     * false otherwise (may because they are already
     * viewers)
     * @since 1.0.0
     */
    public boolean addViewer(Player player) {
        if (viewers.add(player)) {
            controller.show(this, player);
            return true;
        }
        return false;
    }

    /**
     * Removes the given {@link Player} as viewer if they
     * are viewers, and if they were, the view is hidden
     * ({@link ModelViewController#hide})
     *
     * @param player The removed viewer
     * @return True if the player was correctly removed
     * as viewers, false otherwise (may because they
     * were not viewers)
     * @since 1.0.0
     */
    public boolean removeViewer(Player player) {
        if (viewers.remove(player)) {
            controller.hide(this, player);
            return true;
        }
        return false;
    }

    private void teleportBoneAndChildren(
            double yawRadians,
            Bone bone,
            Vector3Float parentPosition
    ) {
        // location computing
        var position = bone.position().add(parentPosition);
        var rotatedPosition = Vectors.rotateAroundY(position, yawRadians);

        var entity = bones.get(bone.name());
        entity.position(rotatedPosition);

        for (var child : bone.children()) {
            teleportBoneAndChildren(
                    yawRadians,
                    child,
                    position
            );
        }
    }

    public void teleport(Location newLocation) {
        this.location = newLocation.clone();
        double yawRadians = Math.toRadians(newLocation.getYaw());
        for (Bone bone : model.bones()) {
            teleportBoneAndChildren(yawRadians, bone, Vector3Float.ZERO);
        }
    }

}