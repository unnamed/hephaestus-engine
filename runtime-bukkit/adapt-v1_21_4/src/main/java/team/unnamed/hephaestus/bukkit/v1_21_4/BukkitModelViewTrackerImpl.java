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
package team.unnamed.hephaestus.bukkit.v1_21_4;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.bukkit.track.BukkitModelViewTracker;
import team.unnamed.hephaestus.bukkit.ModelView;
import team.unnamed.hephaestus.view.AbstractModelView;
import team.unnamed.hephaestus.view.track.ModelViewTrackingRule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

final class BukkitModelViewTrackerImpl implements BukkitModelViewTracker {
    private static final Access.FieldReflect<ServerEntity> SERVER_ENTITY_FIELD = Access.findFieldByType(ChunkMap.TrackedEntity.class, ServerEntity.class);

    static final BukkitModelViewTrackerImpl INSTANCE = new BukkitModelViewTrackerImpl();

    private final Map<UUID, ModelViewImpl> trackedViews = new HashMap<>();

    private BukkitModelViewTrackerImpl() {
    }

    @Override
    public boolean stopTracking(final @NotNull AbstractModelView<Player> abstractView) {
        final var view = ensureThisModuleModelView(abstractView);
        final var base = view.base();

        if (base == null) {
            // We are not tracking this view
            return false;
        }

        final var baseHandle = ((CraftEntity) base).getHandle();
        final var world = ((CraftWorld) base.getWorld()).getHandle();
        final var chunkMap = world.getChunkSource().chunkMap;

        var tracker = chunkMap.entityMap.get(base.getEntityId());

        // Remove the view from all players
        final var previouslySeenBy = new HashSet<>(tracker.seenBy);
        for (final var seenBy : previouslySeenBy) {
            tracker.removePlayer(seenBy.getPlayer());
        }

        // If we replaced the tracker, restore it
        if (tracker instanceof ModelTrackedEntity replacementTracker) {
            if (replacementTracker.seenBySelf()) {
                // Hide the view from the player
                tracker.removePlayer((ServerPlayer) baseHandle);
            }

            final var old = replacementTracker.replaced();
            chunkMap.entityMap.put(base.getEntityId(), old);
            baseHandle.tracker = old;
            tracker = old;
        }

        // If we replaced the server entity, restore it
        final var serverEntity = tracker.serverEntity;
        if (serverEntity instanceof ModelServerEntity modelServerEntity) {
            SERVER_ENTITY_FIELD.set(tracker, modelServerEntity.replaced());
        }

        // Show the view to all players
        for (final var seenBy : previouslySeenBy) {
            tracker.updatePlayer(seenBy.getPlayer());
        }

        trackedViews.remove(base.getUniqueId());
        return true;
    }

    @Override
    public boolean startGlobalTrackingOn(final @NotNull ModelView view, final @NotNull Entity base) {
        return startTrackingOn(view, base, ModelViewTrackingRule.all());
    }

    @Override
    public boolean startGlobalTracking(final @NotNull AbstractModelView<Player> abstractView) {
        final var view = ensureThisModuleModelView(abstractView);
        return startGlobalTrackingOn(view, createSyntheticBaseEntityWhenNoProvided(view));
    }

    @Override
    public @Nullable ModelView getViewOnBase(final @NotNull Entity base) {
        return trackedViews.get(base.getUniqueId());
    }

    @Override
    public boolean startTrackingOn(final @NotNull AbstractModelView<Player> abstractView, final @NotNull Entity base, final @NotNull ModelViewTrackingRule<Player> trackingRule) {
        final var view = ensureThisModuleModelView(abstractView);
        final var baseHandle = ((CraftEntity) base).getHandle();
        final var world = ((CraftWorld) base.getWorld()).getHandle();

        final var chunkMap = world.getChunkSource().chunkMap;
        final ChunkMap.TrackedEntity entityTracker;
        final ServerEntity replacedServerEntity;

        // Override base entity's entity tracker that will show our view
        // todo: note that, if a plugin (like Citizens) replaces the entity tracker, this will break
        final Consumer<Packet<?>> broadcastChangesFunction;

        if (base instanceof Player) {
            final var replaced = chunkMap.entityMap.get(base.getEntityId());

            if (replaced == null) {
                throw new IllegalStateException("The entity tracker for the player is null, this should not happen.");
            }

            // Take replaced server entity from the old tracker
            replacedServerEntity = SERVER_ENTITY_FIELD.get(replaced);

            // For players, we have to completely replace the entity tracker,
            // since it checks for "self-packet-sending", avoiding it
            entityTracker = new ModelTrackedEntity(
                    replaced,
                    chunkMap,
                    view,
                    trackingRule,
                    baseHandle,
                    baseHandle.getType().clientTrackingRange() * 16,
                    baseHandle.getType().updateInterval(),
                    baseHandle.getType().trackDeltas()
            );
            chunkMap.entityMap.put(base.getEntityId(), entityTracker);
            baseHandle.tracker = entityTracker;
            broadcastChangesFunction = packet -> {
                ((ServerPlayer) baseHandle).connection.send(packet);
                entityTracker.broadcast(packet);
            };
        } else {
            // This won't replace the entire entity tracker, but we replace the
            // ServerEntity anyway so keep that
            entityTracker = chunkMap.entityMap.get(base.getEntityId());
            replacedServerEntity = SERVER_ENTITY_FIELD.get(entityTracker);
            broadcastChangesFunction = entityTracker::broadcast;
        }

        SERVER_ENTITY_FIELD.set(entityTracker, new ModelServerEntity(
                replacedServerEntity,
                world,
                baseHandle,
                view,
                entityTracker::broadcast,
                broadcastChangesFunction,
                trackingRule,
                entityTracker.seenBy
        ));
        view.base(base);

        // Refresh the entity tracker to show the view
        for (final var seenBy : entityTracker.seenBy) {
            final var player = seenBy.getPlayer();
            entityTracker.removePlayer(player);
            entityTracker.updatePlayer(player);
        }

        trackedViews.put(base.getUniqueId(), view);
        return true;
    }

    @Override
    public boolean startTracking(final @NotNull AbstractModelView<Player> abstractView, final @NotNull ModelViewTrackingRule<Player> trackingRule) {
        final var view = ensureThisModuleModelView(abstractView);
        return startTrackingOn(view, createSyntheticBaseEntityWhenNoProvided(view), trackingRule);
    }

    private @NotNull Entity createSyntheticBaseEntityWhenNoProvided(final @NotNull ModelViewImpl view) {
        final var location = view.location();
        final var world = location.getWorld();

        if (world == null) {
            throw new IllegalStateException("The world of the provided ModelView location is null.");
        }

        final var entity = world.spawn(location, Interaction.class, CreatureSpawnEvent.SpawnReason.CUSTOM);
        final var boundingBox = view.model().boundingBox();
        entity.setInteractionHeight(boundingBox.y());
        entity.setInteractionWidth(boundingBox.x());
        return entity;
    }

    private @NotNull ModelViewImpl ensureThisModuleModelView(final AbstractModelView<Player> view) {
        if (view instanceof ModelViewImpl impl) {
            return impl;
        } else if (view == null) {
            throw new NullPointerException("The provided model view is null.");
        } else {
            throw new IllegalArgumentException("The provided model view is not a Bukkit ModelView, or is not made for this version.");
        }
    }
}
