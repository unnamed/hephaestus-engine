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

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import team.unnamed.hephaestus.Minecraft;
import team.unnamed.hephaestus.bukkit.ModelEntity;

import java.util.Objects;
import java.util.function.Consumer;

// TODO: Replace with Interaction Entity interactions
final class ModelInteractListener implements Listener {

    private static final String TAG_IS_DROP = "hephaestus:is_drop";

    private final Plugin plugin;

    public ModelInteractListener(Plugin plugin) {
        this.plugin = plugin;
    }

    // TODO: Spectating model entities, leashing, hooking, etc

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.getPlayer().setMetadata(TAG_IS_DROP, new FixedMetadataValue(plugin, Boolean.TRUE));
    }

    @EventHandler
    public void onArmSwing(PlayerArmSwingEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata(TAG_IS_DROP)) {
            // if arm swing is caused by an item drop, do
            // not do anything
            player.removeMetadata(TAG_IS_DROP, plugin);
            return;
        }

        checkInteraction(player, modelEntity ->
                ((CraftPlayer) player).getHandle().attack(((team.unnamed.hephaestus.bukkit.v1_20_R3.CraftModelEntity) modelEntity).getHandle()));
    }

    // handle the horrible interact event
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            EquipmentSlot hand = event.getHand();
            Objects.requireNonNull(hand, "hand"); // should never be null, since action is never PHYSICAL
            checkInteraction(player, modelEntity ->
                    Bukkit.getPluginManager().callEvent(new PlayerInteractEntityEvent(player, modelEntity, hand)));
        }
    }


    private boolean checkInteraction(Player bukkitPlayer, Consumer<ModelEntity> callback) {
        ServerPlayer player = ((CraftPlayer) bukkitPlayer).getHandle();

        boolean creative = player.gameMode.getGameModeForPlayer().isCreative();
        double pickRange = creative ? Minecraft.PLAYER_CREATIVE_PICK_RANGE : Minecraft.PLAYER_DEFAULT_PICK_RANGE;
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

            if (distance <= 9.0D && distance < rangeSqr && entity instanceof team.unnamed.hephaestus.bukkit.v1_20_R3.MinecraftModelEntity modelEntity) {
                callback.accept(modelEntity.getBukkitEntity());
                return true;
            }
        }

        return false;
    }

}
