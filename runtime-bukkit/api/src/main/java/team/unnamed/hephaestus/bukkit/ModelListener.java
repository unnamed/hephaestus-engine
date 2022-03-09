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

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import team.unnamed.hephaestus.view.ActionType;

public class ModelListener implements Listener {

    private static final String IS_DROPPING_ITEM = "hephaestus_is_dropping";

    private final Plugin plugin;

    public ModelListener(Plugin plugin) {
        this.plugin = plugin;
    }

    private static void checkInteraction(Player player, ActionType action) {
        Location location = player.getLocation();
        Vector direction = location.getDirection();

        double directionX = direction.getX();
        double directionY = direction.getY();
        double directionZ = direction.getZ();

        double originX = location.getX();
        double originY = location.getY() + player.getEyeHeight();
        double originZ = location.getZ();

        double range = player.getGameMode() == GameMode.CREATIVE ? 5D : 3D;
        double rangeSquared = range * range;

        double lenX = (range - (Math.abs(originX) % range)) / Math.abs(directionX);
        double lenY = (range - (Math.abs(originY) % range)) / Math.abs(directionY);
        double lenZ = (range - (Math.abs(originZ) % range)) / Math.abs(directionZ);

        double minLen = Math.min(lenX, Math.min(lenY, lenZ));

        double targetX = originX + (directionX * minLen);
        double targetY = originY + (directionY * minLen);
        double targetZ = originZ + (directionZ * minLen);

        World world = player.getWorld();
        for (Entity entity : world.getNearbyEntities(location, rangeSquared, rangeSquared, rangeSquared)) {
            if (!(entity instanceof ModelView modelView)) {
                continue;
            }
            BoundingBox boundingBox = entity.getBoundingBox();
            boundingBox.contains()
            if (boundingBox.intersect(originX, originY, originZ, targetX, targetY, targetZ)) {
                modelView.interactListener()
                        .onInteract(modelView, player, action);
                break;
            }
        }

    }

    @EventHandler
    public void onArmAnimation(PlayerAnimationEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata(IS_DROPPING_ITEM)) {
            player.removeMetadata(IS_DROPPING_ITEM, plugin);;
            return;
        }

        checkInteraction(player, ActionType.LEFT_CLICK);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.PHYSICAL) {
            return;
        }

        checkInteraction(
                player,
                action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK
                        ? ActionType.LEFT_CLICK
                        : ActionType.RIGHT_CLICK
        );
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.getPlayer().setMetadata(IS_DROPPING_ITEM, new FixedMetadataValue(plugin, 1));
    }

    // private static final Tag<Byte> IS_DROPPING_ITEM = Tag.Byte("hephaestus:dropping_flag");
    //    private static void onAttack(EntityAttackEvent event) {
    //        Entity entity = event.getEntity();
    //        Entity target = event.getTarget();
    //
    //        if (entity instanceof Player player
    //                && target instanceof BoneView bone) {
    //            bone.view()
    //                    .interactListener()
    //                    .onInteract(bone.view(), player, ActionType.LEFT_CLICK);
    //        }
    //    }
    //
    //    private static void onInteract(PlayerEntityInteractEvent event) {
    //        if (event.getTarget() instanceof BoneView bone) {c
    //            bone.view()
    //                    .interactListener()
    //                    .onInteract(bone.view(), event.getPlayer(), ActionType.RIGHT_CLICK);
    //        }
    //    }
    //
    //    private static void onItemDrop(ItemDropEvent event) {
    //        event.getPlayer().setTag(IS_DROPPING_ITEM, (byte) 1);
    //    }
    //
    //    private static void onItemUse(PlayerUseItemEvent event) {
    //        checkInteraction(event.getPlayer(), ActionType.RIGHT_CLICK);
    //    }

}
