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

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.bukkit.ModelView;
import team.unnamed.hephaestus.bukkit.ModelViewController;
import team.unnamed.hephaestus.bukkit.ModelViewOptions;
import team.unnamed.hephaestus.bukkit.ModelViewRenderer;

import java.lang.reflect.Field;

public class ModelViewRenderer_v1_18_R2 implements ModelViewRenderer {

    private static final Field SERVER_ENTITY_FIELD;

    static {
        Field serverEntityField = null;
        for (Field field : ChunkMap.TrackedEntity.class.getDeclaredFields()) {
            if (field.getType() == ServerEntity.class) {
                serverEntityField = field;
                serverEntityField.setAccessible(true);
            }
        }
        if (serverEntityField == null) {
            throw new IllegalStateException("Server entity field not found");
        }
        SERVER_ENTITY_FIELD = serverEntityField;
    }

    private final ModelViewController controller = new ModelViewController_v1_18_R2();

    @Override
    public ModelView render(Model model, Location location, ModelViewOptions options) {
        ModelView view = new ModelView(controller, model, location);
        if (options.autoViewable()) {
            ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
            ChunkMap chunkMap = level.chunkSource.chunkMap;
            ModelEntity_v1_18_R2 entity = new ModelEntity_v1_18_R2(EntityType.ARMOR_STAND, level, view);
            ChunkMap.TrackedEntity trackedEntity = chunkMap.new TrackedEntity(entity, 40, 40, false);
            try {
                SERVER_ENTITY_FIELD.set(
                        trackedEntity,
                        new ModelServerEntity_v1_18_R2(level, entity, trackedEntity::broadcast, trackedEntity.seenBy)
                );
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to set own server entity to tracker");
            }
            chunkMap.entityMap.put(entity.getId(), trackedEntity);
        }
        return view;
    }

}