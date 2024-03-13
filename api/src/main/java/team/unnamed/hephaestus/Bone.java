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
package team.unnamed.hephaestus;

import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.view.AbstractBoneView;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Represents a {@link Model} movable part.
 *
 * <p>Note that this is just a specification of how the model
 * should be rendered, it doesn't contain any rendering logic,
 * it's just a data structure. For rendering/view logic, check
 * the {@link AbstractBoneView} interface</p>
 *
 * @since 1.0.0
 */
public final class Bone implements Examinable {
    private final String name;

    private final Vector3Float position;
    private final Vector3Float rotation;

    private final Map<String, Bone> children;

    private final int customModelData;
    private final float scale;

    private final boolean parentOnly;

    public Bone(
            final @NotNull String name,
            final @NotNull Vector3Float position,
            final @NotNull Vector3Float rotation,
            final @NotNull Map<String, Bone> children,
            final int customModelData,
            final float scale,
            final boolean parentOnly
    ) {
        this.name = requireNonNull(name, "name");
        this.position = requireNonNull(position, "position");
        this.rotation = requireNonNull(rotation, "rotation");
        this.children = requireNonNull(children, "children");
        this.customModelData = customModelData;
        this.scale = scale;
        this.parentOnly = parentOnly;
    }

    /**
     * Returns this bone unique name, bone names
     * are unique in the {@link Model} scope
     *
     * @return The bone name
     * @since 1.0.0
     */
    public @NotNull String name() {
        return name;
    }

    /**
     * Returns the bone position relative to its
     * parent bone position, in Minecraft blocks
     *
     * @return The bone position
     * @since 1.0.0
     */
    public @NotNull Vector3Float position() {
        return position;
    }

    /**
     * Returns this bone initial rotation,
     * in euler angles (degrees).
     *
     * @return The bone initial rotation
     * @since 1.0.0
     */
    public @NotNull Vector3Float rotation() {
        return rotation;
    }

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
     * @since 1.0.0
     */
    public int customModelData() {
        return customModelData;
    }

    /**
     * Returns this bone children bones.
     *
     * @return The child bones
     * @since 1.0.0
     */
    public @NotNull Collection<Bone> children() {
        return children.values();
    }

    /**
     * Returns a map of this bone children
     * bones, keys are bone names.
     *
     * @return The child bone map
     * @since 1.0.0
     */
    @ApiStatus.Internal // I don't really like having 2 children methods
    public @NotNull Map<String, Bone> childrenMap() {
        return children;
    }

    /**
     * Returns the child bone with the given name,
     * or null if not found.
     *
     * @param name The child bone name
     * @return The child bone, or null
     * @since 1.0.0
     */
    public @Nullable Bone child(final @NotNull String name) {
        return children.get(name);
    }

    /**
     * Returns this bone initial scale, which is just
     * a compensation to make big models show with
     * their correct size, since resource-packs are
     * limited.
     *
     * <p>If the model already fits, scale is one.</p>
     *
     * <p>The returned scale is always one, or greater.</p>
     *
     * @return The bone initial scale
     * @since 1.0.0
     */
    public float scale() {
        return scale;
    }

    /**
     * Returns whether this bone is just a parent-only
     * bone, which means that it doesn't have any cubes,
     * which makes it invisible.
     *
     * @return Whether this bone is parent-only
     * @since 1.0.0
     */
    public boolean parentOnly() {
        return parentOnly;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("name", name),
                ExaminableProperty.of("position", position),
                ExaminableProperty.of("rotation", rotation),
                ExaminableProperty.of("children", children),
                ExaminableProperty.of("customModelData", customModelData),
                ExaminableProperty.of("scale", scale),
                ExaminableProperty.of("parentOnly", parentOnly)
        );
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var bone = (Bone) o;
        return customModelData == bone.customModelData
                && Float.compare(scale, bone.scale) == 0
                && parentOnly == bone.parentOnly
                && name.equals(bone.name)
                && position.equals(bone.position)
                && rotation.equals(bone.rotation)
                && children.equals(bone.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, position, rotation, children, customModelData, scale, parentOnly);
    }
}