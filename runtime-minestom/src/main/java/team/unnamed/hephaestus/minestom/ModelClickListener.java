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

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.tag.Tag;
import team.unnamed.hephaestus.Minecraft;

import java.util.function.Function;

public final class ModelClickListener {

    private static final Tag<Byte> IS_DROPPING_ITEM = Tag.Byte("hephaestus:dropping_flag");

    private final EventNode<Event> node;

    private ModelClickListener(EventNode<Event> node) {
        this.node = node;
    }

    private void checkInteraction(Player player, Function<ModelEntity, Event> eventFactory) {
        double range = player.getGameMode() == GameMode.CREATIVE
                ? Minecraft.PLAYER_CREATIVE_PICK_RANGE
                : Minecraft.PLAYER_DEFAULT_PICK_RANGE;

        Entity found = player.getLineOfSightEntity(range, entity -> entity != player);

        if (found instanceof BoneEntity boneEntity) {
            found = boneEntity.view();
        }
        if (found instanceof ModelEntity modelEntity) {
            node.call(eventFactory.apply(modelEntity));
        }
    }

    private void onArmAnimation(PlayerHandAnimationEvent event) {
        Player player = event.getPlayer();

        if (player.hasTag(IS_DROPPING_ITEM)) {
            // player has "is dropping item" tag flag, so this event
            // was caused by an item drop, ignore
            player.removeTag(IS_DROPPING_ITEM);
            return;
        }

        checkInteraction(player, model -> new EntityAttackEvent(player, model));
    }

    private void onAttack(EntityAttackEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        if (entity instanceof Player player && target instanceof BoneEntity bone) {
            // re-call using the full entity
            node.call(new EntityAttackEvent(player, bone.view()));
        }
    }

    private void onInteract(PlayerEntityInteractEvent event) {
        if (event.getTarget() instanceof BoneEntity bone) {
            node.call(new PlayerEntityInteractEvent(
                    event.getPlayer(),
                    bone.view(),
                    event.getHand(),
                    event.getInteractPosition()
            ));
        }
    }
    
    private void onItemDrop(ItemDropEvent event) {
        event.getPlayer().setTag(IS_DROPPING_ITEM, (byte) 1);
    }

    private void onItemUse(PlayerUseItemEvent event) {
        Player player = event.getPlayer();
        checkInteraction(player, model -> new PlayerEntityInteractEvent(player, model, event.getHand(), Pos.ZERO));
    }

    public static void register(EventNode<Event> node) {
        ModelClickListener listener = new ModelClickListener(node);
        node.addListener(PlayerUseItemEvent.class, listener::onItemUse);
        node.addListener(ItemDropEvent.class, listener::onItemDrop);
        node.addListener(PlayerHandAnimationEvent.class, listener::onArmAnimation);
        node.addListener(EntityAttackEvent.class, listener::onAttack);
        node.addListener(PlayerEntityInteractEvent.class, listener::onInteract);
    }

}
