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

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.instance.Instance;
import team.unnamed.hephaestus.view.ActionType;

public final class ModelClickListener {

    private ModelClickListener() {
    }

    private static void onArmAnimaion(PlayerHandAnimationEvent event) {
        Player player = event.getPlayer();
        System.out.println("Ray casting...");

        Pos position = player.getPosition();
        Vec direction = position.direction();
        double eyeHeight = player.getEyeHeight();

        double directionX = direction.x();
        double directionY = direction.y();
        double directionZ = direction.z();

        double originX = position.x();
        double originY = position.y() + eyeHeight;
        double originZ = position.z();

        double distance = player.getGameMode() == GameMode.CREATIVE ? 5D : 3D;

        double lenX = (distance - (Math.abs(originX) % distance)) / Math.abs(directionX);
        double lenY = (distance - (Math.abs(originY) % distance)) / Math.abs(directionY);
        double lenZ = (distance - (Math.abs(originZ) % distance)) / Math.abs(directionZ);

        double minLen = Math.min(lenX, Math.min(lenY, lenZ));

        double targetX = originX + (directionX * minLen);
        double targetY = originY + (directionY * minLen);
        double targetZ = originZ + (directionZ * minLen);

        System.out.println("Target: " + targetX + " " + targetY + " " + targetZ);
        System.out.println("Checking for nearby entities");

        Instance instance = player.getInstance();
        for (Entity entity : instance.getNearbyEntities(position, distance)) {
            if (entity == player) {
                continue;
            }
            BoundingBox boundingBox = entity.getBoundingBox();
            if (boundingBox.intersect(originX, originY, originZ, targetX, targetY, targetZ)) {
                System.out.println("Intersect " + entity);
            }
        }
    }

    private static void onAttack(EntityAttackEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        if (entity instanceof Player player
                && target instanceof BoneEntity bone) {
            bone.view()
                    .interactListener()
                    .onInteract(bone.view(), player, ActionType.LEFT_CLICK);
        }
    }

    private static void onInteract(PlayerEntityInteractEvent event) {
        if (event.getTarget() instanceof BoneEntity bone) {
            bone.view()
                    .interactListener()
                    .onInteract(bone.view(), event.getPlayer(), ActionType.RIGHT_CLICK);
        }
    }

    public static void register(EventNode<Event> node) {
        node.addListener(PlayerHandAnimationEvent.class, ModelClickListener::onArmAnimaion);
        node.addListener(EntityAttackEvent.class, ModelClickListener::onAttack);
        node.addListener(PlayerEntityInteractEvent.class, ModelClickListener::onInteract);
    }

}
