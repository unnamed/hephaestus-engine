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

import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.sound.Sound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.controller.AnimationPlayer;
import team.unnamed.hephaestus.bukkit.ModelView;
import team.unnamed.hephaestus.util.Quaternion;
import team.unnamed.hephaestus.util.Vectors;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class ModelViewImpl implements ModelView {
    // We need the plugin instance for some operations
    private final Plugin plugin;

    private final Model model;
    private final Location location;
    private final float scale;

    private final AnimationPlayer animationPlayer;
    private final ImmutableMap<String, BoneEntity> bones;

    private final Collection<Player> viewers = new HashSet<>();

    // Invariable:
    // - If 'base' is set, 'baseEntityId' is set and 'viewers' is unused
    // - If 'base' is null, 'baseEntityId' is set and 'viewers' is used
    private Entity base = null;
    private int baseEntityId = -1;

    protected ModelViewImpl(final @NotNull Plugin plugin, final @NotNull Model model, final @NotNull Location location, final float scale) {
        this.plugin = requireNonNull(plugin, "plugin");
        this.model = requireNonNull(model, "model");
        this.location = requireNonNull(location, "location");
        this.scale = scale;
        this.animationPlayer = AnimationPlayer.create(this);
        this.bones = instantiateBones();
    }

    public void show(final @NotNull Consumer<? super Packet<?>> packetConsumer) {
        final var ids = new int[bones.size()];
        int i = 0;
        for (final var bone : bones.values()) {
            ids[i++] = bone.entityId();
            bone.show(packetConsumer);
        }

        // add passengers to base entity
        //noinspection DataFlowIssue
        packetConsumer.accept(new ClientboundSetPassengersPacket(new FriendlyByteBuf(null) {
            @Override
            public int readVarInt() {
                return baseEntityId;
            }

            @Override
            public int @NotNull [] readVarIntArray() {
                return ids;
            }
        }));
    }

    public void sendChanges(final @NotNull Consumer<? super Packet<?>> packetConsumer) {
        // Send bone changes
        for (var bone : bones.values()) {
            // check metadata changes
            // (rotation, position, color, etc...)
            bone.sendDirtyData(packetConsumer);
        }
    }

    public void remove(final @NotNull Consumer<? super Packet<?>> packetConsumer) {
        // Remove bones
        int[] ids = new int[bones.size()];
        int i = 0;
        for (var bone : bones.values()) {
            ids[i++] = bone.getId();
        }
        packetConsumer.accept(new ClientboundRemoveEntitiesPacket(ids));
    }

    protected void base(final @Nullable Entity base) {
        this.base = base;
        this.baseEntityId = base == null ? -1 : base.getEntityId();
    }

    @Override
    public void baseEntityId(final int baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    private ImmutableMap<String, BoneEntity> instantiateBones() {
        // create the bone entities
        ImmutableMap.Builder<String, BoneEntity> bones = ImmutableMap.builder();
        for (Bone bone : model.bones()) {
            instantiateBone(bone, Vector3Float.ZERO, Quaternion.IDENTITY, bones);
        }
        return bones.build();
    }

    protected void instantiateBone(
            final @NotNull Bone bone,
            final @NotNull Vector3Float parentPosition,
            final @NotNull Quaternion parentRotation,
            final @NotNull ImmutableMap.Builder<String, BoneEntity> into
    ) {
        final var rotation = parentRotation.multiply(Quaternion.fromEulerDegrees(bone.rotation()));
        final var position = parentRotation.transform(bone.position()).add(parentPosition);

        var entity = new BoneEntity(this, bone, position, rotation, scale);
        into.put(bone.name(), entity);

        for (var child : bone.children()) {
            instantiateBone(child, position, rotation, into);
        }
    }

    @Override
    public @NotNull Model model() {
        return model;
    }

    @Override
    public UUID getUniqueId() {
        return base.getUniqueId();
    }

    @Override
    public @Nullable Entity base() {
        return base;
    }

    @Override
    public @NotNull Location location() {
        return location; // should we clone it?
    }

    @Override
    public @NotNull Collection<Player> viewers() {
        if (base != null) {
            return base.getTrackedBy();
        } else {
            return viewers;
        }
    }

    @Override
    public boolean addViewer(final @NotNull Player player) {
        requireNonNull(player, "player");
        if (base != null) {
            player.showEntity(plugin, base);
        } else if (viewers.add(player)) {
            final var connection = ((CraftPlayer) player).getHandle().connection;
            show(connection::send);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeViewer(final @NotNull Player player) {
        requireNonNull(player, "player");
        if (base != null) {
            player.hideEntity(plugin, base);
        } else if (viewers.remove(player)) {
            final var connection = ((CraftPlayer) player).getHandle().connection;
            remove(connection::send);
            return true;
        }
        return false;
    }

    @Override
    public void emitSound(final @NotNull Sound sound) {
        final var location = location();
        final var x = location.x();
        final var y = location.y();
        final var z = location.z();
        for (final var viewer : viewers()) {
            viewer.playSound(sound, x, y, z);
        }
    }

    @Override
    public Collection<BoneEntity> bones() {
        return bones.values();
    }

    @Override
    public @Nullable BoneEntity bone(final @NotNull String name) {
        return bones.get(name);
    }

    @Override
    public @NotNull AnimationPlayer animationPlayer() {
        return animationPlayer;
    }

    @Override
    public void tickAnimations() {
        if (base != null) {
            if (base instanceof LivingEntity livingBase) {
                animationPlayer.tick(livingBase.getYaw(), -livingBase.getPitch());
            } else {
                animationPlayer.tick(base.getYaw(), base.getPitch());
            }
        } else {
            animationPlayer.tick();
        }
    }
}
