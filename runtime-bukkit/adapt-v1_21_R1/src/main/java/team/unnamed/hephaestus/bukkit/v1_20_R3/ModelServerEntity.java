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
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.view.track.ModelViewTrackingRule;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
final class ModelServerEntity extends ServerEntity {
    // Keep the old (replaced) ServerEntity to be able to restore it later.
    private final ServerEntity replaced;

    private final ModelViewImpl view;
    private final Entity base;
    private final Consumer<Packet<?>> broadcastChanges;
    private final ModelViewTrackingRule<Player> trackingRule;

    public ModelServerEntity(
            final @NotNull ServerEntity replaced,
            ServerLevel level,
            Entity base,
            ModelViewImpl view,
            Consumer<Packet<?>> broadcast,
            Consumer<Packet<?>> broadcastChanges,
            ModelViewTrackingRule<Player> trackingRule,
            Set<ServerPlayerConnection> trackedPlayers
    ) {
        super(level, base, base.getType().updateInterval(), base.getType().trackDeltas(), broadcast, trackedPlayers);
        this.replaced = replaced;
        this.view = view;
        this.base = base;
        this.broadcastChanges = broadcastChanges;
        this.trackingRule = trackingRule;
    }

    public @NotNull ServerEntity replaced() {
        return replaced;
    }

    @Override
    public void sendChanges() {
        // Send base entity changes
        super.sendChanges();

        // Send model view changes
        view.sendChanges(this.broadcastChanges);
    }

    @Override
    public void removePairing(ServerPlayer player) {
        // Remove base entity
        super.removePairing(player);

        // Remove model view
        if (trackingRule.shouldView(view, player.getBukkitEntity())) {
            view.remove(player.connection::send);
        }
    }

    @Override
    public void sendPairingData(ServerPlayer player, Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
        super.sendPairingData(player, packetConsumer);

        // Send model view
        if (!base.isRemoved() && trackingRule.shouldView(view, player.getBukkitEntity())) {
            //noinspection unchecked,rawtypes
            view.show((Consumer) packetConsumer);
        }
    }
}
