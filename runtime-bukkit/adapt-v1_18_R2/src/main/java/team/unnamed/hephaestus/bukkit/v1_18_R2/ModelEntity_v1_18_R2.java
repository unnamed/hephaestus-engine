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

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ModelEntity_v1_18_R2 extends Entity {

    final Map<String, BoneViewImpl> bones = new Object2ObjectOpenHashMap<>();

    public ModelEntity_v1_18_R2(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public CraftEntity getBukkitEntity() {
        System.out.println("getBukkitEntity called");
        return new CraftEntity(this.getServer().server, this) {
            @Override
            public org.bukkit.entity.@NotNull EntityType getType() {
                return org.bukkit.entity.EntityType.UNKNOWN;
            }
        };
    }

    @Override
    protected void defineSynchedData() {
        // no metadata for this entity, it will not
        // be directly spawned anyways
    }

    @Override protected void readAdditionalSaveData(CompoundTag nbt) {}
    @Override protected void addAdditionalSaveData(CompoundTag nbt) {}

    @Override
    public Packet<?> getAddEntityPacket() {
        return null;
    }

}
