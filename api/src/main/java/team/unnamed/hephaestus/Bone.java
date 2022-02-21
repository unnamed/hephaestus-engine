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
package team.unnamed.hephaestus;

import net.kyori.examination.Examinable;
import org.jetbrains.annotations.Contract;
import team.unnamed.creative.base.Vector3Float;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a {@link Model} movable part, in the
 * game, it has its own Creative's {@link team.unnamed.creative.model.Model}
 * and its own armor_stand entity
 *
 * <p>Abstraction for all bone types such as normal
 * bone, head bone (special movement), name-tag bones,
 * seat bones, hand bones (can hold items), etc</p>
 *
 * @since 1.0.0
 */
public interface Bone extends Examinable {

    /**
     * Returns this bone unique name, bone names
     * are unique in the {@link Model} scope
     *
     * @return The bone name
     */
    @Contract(pure = true)
    String name();

    /**
     * Returns this bone offset, which is, in
     * other words, the position of this bone
     * (relative to parent's position)
     *
     * @return The bone offset
     */
    Vector3Float offset();

    /**
     * Returns this bone custom model data,
     * which must be applied to the creative
     * Model representing this bone
     *
     * <p>Consider this number as a "handle"
     * or "connection" to the resource-pack
     * model information</p>
     *
     * <strong>This number must be unique in
     * the resource-pack</strong>
     *
     * @return The bone custom model data
     */
    int customModelData();

    /**
     * Returns this bone initial rotation
     *
     * @return The bone initial rotation
     */
    Vector3Float rotation();

    /**
     * Determines whether to use small armor stands
     * for this bone
     *
     * @return True to use small armor stands
     */
    boolean small();

    /**
     * Returns this bone child bones
     *
     * @return The child bones
     */
    Collection<Bone> children();

    /**
     * Returns a map of this bone children
     * bones, keys are bone names
     *
     * @return The child bone map
     */
    Map<String, Bone> childrenMap();

    /**
     * Creates a new, default {@link Bone} implementation
     *
     * @param name The bone name
     * @param offset The bone initial relative offset
     * @param rotation The bone initial rotation
     * @param small True if the bone armor stand should be small
     * @param customModelData The bone item custom model data
     * @param children The children bones
     * @return The recently created bone
     */
    static Bone bone(
            String name,
            Vector3Float offset,
            Vector3Float rotation,
            boolean small,
            int customModelData,
            Map<String, Bone> children
    ) {
        return new BoneImpl(name, offset, rotation, small, customModelData, children);
    }

}