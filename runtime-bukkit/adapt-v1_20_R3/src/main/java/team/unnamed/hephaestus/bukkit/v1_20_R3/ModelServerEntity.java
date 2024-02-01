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

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
final class ModelServerEntity extends ServerEntity {

    private final MinecraftModelEntity entity;
    private final Consumer<Packet<?>> broadcast;
    private final VecDeltaCodec codec = new VecDeltaCodec();

    public ModelServerEntity(
            ServerLevel level,
            MinecraftModelEntity entity,
            Consumer<Packet<?>> broadcast,
            Set<ServerPlayerConnection> trackedPlayers
    ) {
        super(
                level,
                entity,
                0, // updateInterval, unused because we overwrote methods
                false, // trackDelta, unused because we overwrote methods
                broadcast,
                trackedPlayers
        );
        this.entity = entity;
        this.broadcast = broadcast;
    }

    @Override
    public void sendChanges() {
        for (var bone : entity.bones().values()) {

            // check metadata changes
            // (rotation, ...)
            sendDirtyEntityData(bone);

            // check color changes (equipment)
            if (bone.dirtyColor) {
                ItemStack item = bone.getItemStack();
                this.broadcast.accept(new ClientboundSetEntityDataPacket(
                        bone.getId(),
                        List.of(new SynchedEntityData.DataValue<>(23, EntityDataSerializers.ITEM_STACK, item))
                ));
                bone.dirtyColor = false;
            }
            // check position changes
            Vec3 position = bone.position();

            Vec3 delta = codec.delta(position);

            if (delta.lengthSqr() >= 7.62939453125E-6D) {
                long k = codec.encodeX(position);
                long l = codec.encodeY(position);
                long i1 = codec.encodeZ(position);

                if (k < -32768L || k > 32767L || l < -32768L || l > 32767L || i1 < -32768L || i1 > 32767L) {
                    new ClientboundTeleportEntityPacket(bone);
                } else {
                    new ClientboundMoveEntityPacket.Pos(this.entity.getId(), (short) ((int) k), (short) ((int) l), (short) ((int) i1), bone.onGround());
                }
                codec.setBase(bone.position());
            }
        }
    }

    @Override
    public void removePairing(ServerPlayer player) {
        this.entity.stopSeenByPlayer(player);
        var bones = entity.bones().values();
        int[] ids = new int[bones.size()];
        int i = 0;
        for (var bone : bones) {
            ids[i++] = bone.getId();
        }
        player.connection.send(new ClientboundRemoveEntitiesPacket(ids));
    }

    @Override
    public void sendPairingData(ServerPlayer player, Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
        if (this.entity.isRemoved()) {
            return;
        }
        //super.sendPairingData(player, packetConsumer);

        for (var bone : entity.bones().values()) {
            bone.show(packetConsumer);
        }
    }

    private void sendDirtyEntityData(team.unnamed.hephaestus.bukkit.v1_20_R3.BoneEntity bone) {
        SynchedEntityData data = bone.getEntityData();
        if (data.isDirty()) {
            this.broadcast.accept(new ClientboundSetEntityDataPacket(bone.getId(), data.packDirty()));
        }
    }

}
