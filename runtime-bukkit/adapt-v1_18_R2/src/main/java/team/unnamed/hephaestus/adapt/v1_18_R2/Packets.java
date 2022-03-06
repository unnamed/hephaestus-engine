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
package team.unnamed.hephaestus.adapt.v1_18_R2;

import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Utility class for packets, specific
 * for v1_18_R1 minecraft server version
 */
final class Packets {

    private Packets() {
    }

    /**
     * Sends the given {@code packets} to the
     * specified {@code player}
     */
    static void send(Player player, Packet<?>... packets) {
        var connection = ((CraftPlayer) player).getHandle().connection;
        for (var packet : packets) {
            connection.send(packet);
        }
    }

    /**
     * Sends the given {@code packets} to all the
     * specified {@code players}
     */
    static void send(Iterable<? extends Player> players, Packet<?>... packets) {
        for (var player : players) {
            var connection = ((CraftPlayer) player).getHandle().connection;
            for (var packet : packets) {
                connection.send(packet);
            }
        }
    }

}