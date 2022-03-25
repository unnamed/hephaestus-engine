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
package team.unnamed.hephaestus.bukkit.v1_18_R2;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.unnamed.hephaestus.bukkit.ModelEntity;
import team.unnamed.hephaestus.bukkit.PlayerInteractAtModelEvent;

final class ModelInteractListener implements Listener {

    private static final double LARGE_RANGE = 5.0D;
    private static final double NORMAL_RANGE = 4.5D;

    @EventHandler
    public void onArmSwing(PlayerArmSwingEvent event) {
        Player bukkitPlayer = event.getPlayer();
        ServerPlayer player = ((CraftPlayer) bukkitPlayer).getHandle();

        boolean creative = player.gameMode.getGameModeForPlayer().isCreative();
        double pickRange = creative ? LARGE_RANGE : NORMAL_RANGE;
        // HitResult result = player.pick(pickRange, 1.0F, false);

        Vec3 eyePosition = player.getEyePosition();
        double rangeSqr = creative ? (pickRange = 6.0D) : pickRange;
        rangeSqr *= rangeSqr;

        // if (result != null) {
        //     rangeSqr = result.getLocation().distanceToSqr(eyePosition);
        // }

        Vec3 viewVector = player.getViewVector(1.0F);
        Vec3 end = eyePosition.add(viewVector.x * pickRange, viewVector.y * pickRange, viewVector.z * pickRange);
        AABB aabb = player.getBoundingBox()
                .expandTowards(viewVector.scale(pickRange))
                .inflate(1.0D, 1.0D, 1.0D);

        EntityHitResult result = ProjectileUtil.getEntityHitResult(player, eyePosition, end, aabb, e -> !e.isSpectator() && e.isPickable(), rangeSqr);
        if (result != null) {
            Entity entity = result.getEntity();
            Vec3 loc = result.getLocation();
            double distance = eyePosition.distanceToSqr(loc);

            if (distance <= 9.0D && distance < rangeSqr && entity instanceof ModelEntity modelEntity) {
                new PlayerInteractAtModelEvent(bukkitPlayer, modelEntity)
                        .callEvent();
            }
        }
    }

}
