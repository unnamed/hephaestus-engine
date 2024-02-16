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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
final class ModelServerEntity extends ServerEntity {
    private final ModelViewImpl view;
    private final Entity base;
    private final Consumer<Packet<?>> broadcast;

    public ModelServerEntity(
            ServerLevel level,
            Entity base,
            ModelViewImpl view,
            Consumer<Packet<?>> broadcast,
            Set<ServerPlayerConnection> trackedPlayers
    ) {
        super(
                level,
                base,
                base.getType().updateInterval(),
                base.getType().trackDeltas(),
                broadcast,
                trackedPlayers
        );
        this.view = view;
        this.base = base;
        this.broadcast = broadcast;
    }

    @Override
    public void sendChanges() {
        // Send base entity changes
        super.sendChanges();

        // Send bone changes
        for (var bone : view.bones()) {
            // check metadata changes
            // (rotation, position, color, etc...)
            bone.sendDirtyData(this.broadcast);
        }
    }

    @Override
    public void removePairing(ServerPlayer player) {
        // Remove base entity
        super.removePairing(player);

        // Remove bones
        var bones = view.bones();
        int[] ids = new int[bones.size()];
        int i = 0;
        for (var bone : bones) {
            ids[i++] = bone.getId();
        }
        player.connection.send(new ClientboundRemoveEntitiesPacket(ids));
    }

    @Override
    public void sendPairingData(ServerPlayer player, Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
        super.sendPairingData(player, packetConsumer);

        if (!this.base.isRemoved()) {
            final var bones = view.bones();
            final var ids = new int[bones.size()];
            int i = 0;
            for (final var bone : bones) {
                ids[i++] = bone.entityId();
                bone.show(packetConsumer);
            }

            // add passengers to base entity
            packetConsumer.accept(new ClientboundSetPassengersPacket(new FriendlyByteBuf(null) {
                @Override
                public int readVarInt() {
                    return base.getId();
                }

                @Override
                public int[] readVarIntArray() {
                    return ids;
                }
            }));
        }
    }
}
