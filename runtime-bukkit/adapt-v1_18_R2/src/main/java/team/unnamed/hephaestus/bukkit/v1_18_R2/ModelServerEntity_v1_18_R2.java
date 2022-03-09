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

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;

import java.util.Set;
import java.util.function.Consumer;

public class ModelServerEntity_v1_18_R2 extends ServerEntity {

    private final ModelEntity_v1_18_R2 entity;

    public ModelServerEntity_v1_18_R2(
            ServerLevel level,
            ModelEntity_v1_18_R2 entity,
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
    }

    @Override
    public void sendChanges() {
        // Changes are instantly sent to model viewers
        // TODO: Should they be done here?
    }

    @Override
    public void removePairing(ServerPlayer player) {
        this.entity.stopSeenByPlayer(player);
        var bones = entity.bones.values();
        int[] ids = new int[bones.size()];
        int i = 0;
        for (BoneViewImpl bone : bones) {
            ids[i++] = bone.getId();
        }
        player.connection.send(new ClientboundRemoveEntitiesPacket(ids));
    }

    @Override
    public void sendPairingData(Consumer<Packet<?>> packetConsumer, ServerPlayer player) {

        if (this.entity.isRemoved()) {
            return;
        }

        for (BoneViewImpl bone : entity.bones.values()) {
            bone.show(packetConsumer);
        }
    }

}
