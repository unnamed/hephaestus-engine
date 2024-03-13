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
package team.unnamed.hephaestus.view.modifier.player.rig;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Writable;

import java.util.Collection;

/**
 * Represents a player rig, which is a set of bones
 * that can be modified by animations.
 *
 * @since 1.0.0
 */
public interface PlayerRig {
    /**
     * Returns the bone type with the given name, if
     * it exists in this rig, or null if it doesn't.
     *
     * @param name The name of the bone type
     * @return The bone type with the given name, or null
     * @since 1.0.0
     */
    @Nullable PlayerBoneType get(final @NotNull String name);

    /**
     * Returns a collection with all the possible bone
     * types in this rig.
     *
     * @return The bone types in this rig
     * @since 1.0.0
     */
    @NotNull Collection<PlayerBoneType> types();

    /**
     * Returns the vertex shader used by this rig.
     *
     * @return The vertex shader used by this rig
     * @since 1.0.0
     */
    @NotNull Writable vertexShader();

    /**
     * Returns the fragment shader used by this rig.
     *
     * @return The fragment shader used by this rig
     * @since 1.0.0
     */
    @NotNull Writable fragmentShader();

    /**
     * Returns the vanilla player rig, which is the default
     * rig used by Minecraft.
     *
     * <p>With this rig, animations will only be able to
     * modify the player bones in the same way as the
     * default Minecraft player model.</p>
     *
     * <p>This rig has 6 bones: head, torso, right_arm,
     * left_arm, right_leg and left_leg.</p>
     *
     * @return The vanilla player rig
     * @since 1.0.0
     */
    static @NotNull PlayerRig vanilla() {
        return VanillaPlayerRig.INSTANCE;
    }

    /**
     * Returns the detailed player rig, which is a more
     * complex rig that allows to modify more bones
     * than the vanilla rig.
     *
     * <p>With this rig, animations will be able to
     * modify the player bones in a more detailed way
     * than the default Minecraft player model.</p>
     *
     * <p>This rig has 12 bones: head, right_arm, left_arm,
     * right_forearm, left_forearm, hip, waist, chest,
     * right_leg, left_leg, right_foreleg, left_foreleg</p>
     *
     * @return The detailed player rig
     * @since 1.0.0
     */
    static @NotNull PlayerRig detailed() {
        return DetailedPlayerRig.INSTANCE;
    }
}
