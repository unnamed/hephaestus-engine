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

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.view.track.ModelViewTrackingRule;

final class ModelTrackedEntity extends ChunkMap.TrackedEntity {
    // Keep the old TrackedEntity to be able to restore it later
    private final ChunkMap.TrackedEntity replaced;

    private final ModelViewImpl view;
    private final Entity base;
    private final ModelViewTrackingRule<Player> trackingRule;
    private boolean seenBySelf = false;

    public ModelTrackedEntity(
            final @NotNull ChunkMap.TrackedEntity replaced,
            final @NotNull ChunkMap chunkMap,
            final @NotNull ModelViewImpl view,
            final @NotNull ModelViewTrackingRule<Player> trackingRule,
            final @NotNull Entity base,
            final int trackingRange,
            final int updateInterval,
            final boolean trackDelta
    ) {
        chunkMap.super(base, trackingRange, updateInterval, trackDelta);
        this.replaced = replaced;
        this.view = view;
        this.trackingRule = trackingRule;
        this.base = base;
    }

    public @NotNull ChunkMap.TrackedEntity replaced() {
        return replaced;
    }

    @Override
    public void updatePlayer(final @NotNull ServerPlayer player) {
        org.spigotmc.AsyncCatcher.catchOp("player tracker update"); // respect Spigot behavior

        if (player == base) {
            if (!seenBySelf && trackingRule.shouldView(view, player.getBukkitEntity())) {
                // Show model view
                view.show(player.connection::send);
                seenBySelf = true;
            }
            return;
        }

        super.updatePlayer(player);
    }

    @Override
    public void removePlayer(ServerPlayer player) {
        org.spigotmc.AsyncCatcher.catchOp("player tracker clear"); // Spigot
        if (this.seenBy.remove(player.connection)) {
            this.serverEntity.removePairing(player);
        } else if (seenBySelf) {
            // Hide model view
            view.remove(player.connection::send);
            seenBySelf = false;
        }
    }

    public boolean seenBySelf() {
        return seenBySelf;
    }
}
