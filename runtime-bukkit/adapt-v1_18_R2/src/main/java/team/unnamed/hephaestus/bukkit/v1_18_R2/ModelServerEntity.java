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

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;
import java.util.function.Consumer;

import static net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.entityToPacket;
import static net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.packetToEntity;

@ParametersAreNonnullByDefault
final class ModelServerEntity extends ServerEntity {

    private final MinecraftModelEntity entity;
    private final Consumer<Packet<?>> broadcast;

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

            // check position changes
            Vec3 position = bone.position();
            long lastPx = bone.lastPx;
            long lastPy = bone.lastPy;
            long lastPz = bone.lastPz;

            double lastX = packetToEntity(lastPx);
            double lastY = packetToEntity(lastPy);
            double lastZ = packetToEntity(lastPz);

            double dx = position.x - lastX;
            double dy = position.y - lastY;
            double dz = position.z - lastZ;

            if (dx != 0 || dy != 0 || dz != 0) {
                long pdx = entityToPacket(dx);
                long pdy = entityToPacket(dy);
                long pdz = entityToPacket(dz);

                bone.lastPx = entityToPacket(position.x);
                bone.lastPy = entityToPacket(position.y);
                bone.lastPz = entityToPacket(position.z);

                boolean big = pdx < Short.MIN_VALUE || pdx > Short.MAX_VALUE
                        || pdy < Short.MIN_VALUE || pdy > Short.MAX_VALUE
                        || pdz < Short.MIN_VALUE || pdz > Short.MAX_VALUE;

                if (big) {
                    broadcast.accept(new ClientboundTeleportEntityPacket(bone));
                } else {
                    broadcast.accept(new ClientboundMoveEntityPacket.Pos(
                            bone.getId(),
                            (short) pdx,
                            (short) pdy,
                            (short) pdz,
                            entity.isOnGround()
                    ));
                }
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
    public void sendPairingData(Consumer<Packet<?>> packetConsumer, ServerPlayer player) {
        if (this.entity.isRemoved()) {
            return;
        }

        for (var bone : entity.bones().values()) {
            bone.show(packetConsumer);
        }
    }

}
