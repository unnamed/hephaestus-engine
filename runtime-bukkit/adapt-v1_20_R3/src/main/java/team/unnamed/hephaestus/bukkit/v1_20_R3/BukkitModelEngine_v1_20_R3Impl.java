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

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import io.papermc.paper.chunk.system.entity.EntityLookup;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.util.WeakCollection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.spigotmc.AsyncCatcher;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.bukkit.ModelEntity;
import team.unnamed.hephaestus.view.track.ModelViewTracker;

import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

final class BukkitModelEngine_v1_20_R3Impl implements BukkitModelEngine_v1_20_R3 {

    private static final Access.FieldReflect<ServerEntity> SERVER_ENTITY_FIELD
            = Access.findFieldByType(ChunkMap.TrackedEntity.class, ServerEntity.class);

    private static final Access.FieldReflect<LevelCallback<?>> CALLBACKS_FIELD
            = Access.findFieldByType(EntityLookup.class, LevelCallback.class);

    private final EntityFactory entityFactory;

    private final WeakCollection<ServerLevel> usedWorlds = new WeakCollection<>();

    BukkitModelEngine_v1_20_R3Impl(Plugin plugin, EntityFactory entityFactory) {
        requireNonNull(plugin, "plugin");
        this.entityFactory = entityFactory;
        Bukkit.getPluginManager().registerEvents(new ModelInteractListener(plugin), plugin);
    }

    public ModelEntity createView(Model model, Location location, float scale) {
        ModelEntity modelEntity = entityFactory.create(((CraftWorld) location.getWorld()).getHandle(), model, scale).getBukkitEntity();
        modelEntity.teleport(location);
        return modelEntity;
    }

    @Override
    public ModelEntity createViewAndTrack(Model model, Location location, CreatureSpawnEvent.SpawnReason reason) {
        return createViewAndTrack(model, location);
    }

    @Override
    public ModelEntity createViewAndTrack(Model model, Location location) {
        return createViewAndTrack(model, location, 1);
    }

    public ModelEntity createView(Model model, Location location) {
        return createView(model, location, 1);
    }

    public ModelEntity createViewAndTrack(Model model, Location location, float scale) {
        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
        if (!usedWorlds.contains(level)) {
            InjectedLevelCallback.injectAt(level);
            usedWorlds.add(level);
        }
        ModelEntity view = createView(model, location, scale);
        level.getEntityLookup().addNewEntity(((CraftModelEntity) view).getHandle());
        return view;
    }

    @Override
    public ModelViewTracker<Player> tracker() {
        throw new UnsupportedOperationException();
    }

    @ParametersAreNonnullByDefault
    private record InjectedLevelCallback(
            LevelCallback<Entity> delegate,
            ServerLevel level
    ) implements LevelCallback<Entity> {

        @Override
        public void onTrackingStart(Entity entity) {
            if (entity instanceof MinecraftModelEntity modelEntity) {
                AsyncCatcher.catchOp("entity register");

                entity.valid = true;
                ChunkMap chunkMap = level.chunkSource.chunkMap;
                ChunkMap.TrackedEntity trackedEntity = chunkMap.new TrackedEntity(entity, 40, 40, false);
                SERVER_ENTITY_FIELD.set(trackedEntity, new ModelServerEntity(level, modelEntity, trackedEntity::broadcast, trackedEntity.seenBy));
                chunkMap.entityMap.put(entity.getId(), trackedEntity);

                entity.updateDynamicGameEventListener(DynamicGameEventListener::add);
                entity.inWorld = true;
                entity.valid = true;
                if (entity.getOriginVector() == null) {
                    entity.setOrigin(entity.getBukkitEntity().getLocation());
                }
                if (entity.getOriginWorld() == null) {
                    entity.setOrigin(entity.getOriginVector().toLocation(level.getWorld()));
                }

                new EntityAddToWorldEvent(entity.getBukkitEntity(), entity.getBukkitEntity().getWorld()).callEvent();
            } else {
                delegate.onTrackingStart(entity);
            }
        }

        @Override
        public void onTrackingEnd(Entity entity) {
            delegate.onTrackingEnd(entity);
        }

        @Override
        public void onSectionChange(Entity entity) {
            delegate.onSectionChange(entity);
        }

        @Override
        public void onCreated(Entity entity) {
            delegate.onCreated(entity);
        }

        @Override
        public void onDestroyed(Entity entity) {
            delegate.onDestroyed(entity);
        }

        @Override
        public void onTickingStart(Entity entity) {
            delegate.onTickingStart(entity);
        }

        @Override
        public void onTickingEnd(Entity entity) {
            delegate.onTickingEnd(entity);
        }

        @SuppressWarnings("unchecked")
        public static void injectAt(ServerLevel level) {
            var entityLookup = level.getEntityLookup();
            var currentCallbacks = (LevelCallback<Entity>) CALLBACKS_FIELD.get(entityLookup);

            if (!(currentCallbacks instanceof InjectedLevelCallback)) {
                CALLBACKS_FIELD.set(entityLookup, new InjectedLevelCallback(currentCallbacks, level));
            }
        }

    }

}
