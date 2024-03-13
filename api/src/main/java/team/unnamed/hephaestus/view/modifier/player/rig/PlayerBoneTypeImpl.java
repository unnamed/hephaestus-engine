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

import net.kyori.adventure.key.KeyPattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;

final class PlayerBoneTypeImpl implements PlayerBoneType {
    private static final Vector3Float DEFAULT_ROTATION = new Vector3Float(0, 180, 0);

    @Subst("head")
    private final String name;
    private final float offset;
    private final int modelData;
    private final int slimModelData;
    private final Vector3Float rotation;
    private final Vector3Float scale;
    private final Vector3Float slimScale;
    private final Vector3Float translation;
    private final Vector3Float slimTranslation;

    PlayerBoneTypeImpl(final @NotNull @KeyPattern.Value String name, final float offset, final int modelData, final int slimModelData, final Vector3Float rotation, final Vector3Float scale, final Vector3Float slimScale, final Vector3Float translation, final Vector3Float slimTranslation) {
        this.name = name;
        this.offset = offset;
        this.modelData = modelData;
        this.slimModelData = slimModelData;
        this.rotation = rotation;
        this.scale = scale;
        this.slimScale = slimScale;
        this.translation = translation;
        this.slimTranslation = slimTranslation;
    }

    @Override
    @KeyPattern.Value
    public @NotNull String boneName() {
        return name;
    }

    @Override
    public float offset() {
        return offset;
    }

    @Override
    public int modelData() {
        return modelData;
    }

    @Override
    public int slimModelData() {
        return slimModelData;
    }

    @Override
    public Vector3Float rotation() {
        return rotation;
    }

    @Override
    public Vector3Float scale() {
        return scale;
    }

    @Override
    public @NotNull Vector3Float slimScale() {
        return slimScale;
    }

    @Override
    public Vector3Float translation() {
        return translation;
    }

    @Override
    public @NotNull Vector3Float slimTranslation() {
        return slimTranslation;
    }

    public static @NotNull Builder builder(final @NotNull @KeyPattern.Value String name) {
        return new Builder(name);
    }

    static class Builder {
        @Subst("head")
        private final String name;
        private float offset;
        private int modelData;
        private int slimModelData;
        private Vector3Float rotation = DEFAULT_ROTATION;
        private Vector3Float scale;
        private Vector3Float slimScale;
        private Vector3Float translation;
        private Vector3Float slimTranslation;

        Builder(final @NotNull String name) {
            this.name = name;
        }

        public @NotNull Builder offset(final float offset) {
            this.offset = offset;
            return this;
        }

        public @NotNull Builder modelData(final int modelData) {
            this.modelData = modelData;
            return this;
        }

        public @NotNull Builder slimModelData(final int slimModelData) {
            this.slimModelData = slimModelData;
            return this;
        }

        public @NotNull Builder rotation(final @NotNull Vector3Float rotation) {
            this.rotation = rotation;
            return this;
        }

        public @NotNull Builder scale(final float x, final float y, final float z) {
            this.scale = new Vector3Float(x, y, z);
            return this;
        }

        public @NotNull Builder slimScale(final float x, final float y, final float z) {
            this.slimScale = new Vector3Float(x, y, z);
            return this;
        }

        public @NotNull Builder translation(final float x, final float y, final float z) {
            this.translation = new Vector3Float(x, y, z);
            return this;
        }

        public @NotNull Builder slimTranslation(final float x, final float y, final float z) {
            this.slimTranslation = new Vector3Float(x, y, z);
            return this;
        }

        public @NotNull PlayerBoneType build() {
            if (slimScale == null) {
                slimScale = scale;
            }
            if (slimTranslation == null) {
                slimTranslation = translation;
            }
            if (slimModelData == 0) {
                slimModelData = modelData;
            }
            return new PlayerBoneTypeImpl(name, offset, modelData, slimModelData, rotation, scale, slimScale, translation, slimTranslation);
        }
    }
}
