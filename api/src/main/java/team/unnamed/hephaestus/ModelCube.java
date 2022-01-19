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

import team.unnamed.hephaestus.bound.FacedTextureBound;
import team.unnamed.hephaestus.bound.TextureFace;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents the most basic component
 * of a model, a movable piece of a model
 */
public class ModelCube {

    /** The origin of the cube */
    private final Vector3Float origin;

    /** Rotation of this cube, in a single axis */
    private final ModelCubeRotation rotation;

    /** The size of the cube */
    private final Vector3Float size;

    /**
     * The component texture bounds indexed
     * by the {@link TextureFace} ordinals
     */
    private final FacedTextureBound[] textureBounds;

    public ModelCube(
            Vector3Float origin,
            ModelCubeRotation rotation,
            Vector3Float size,
            FacedTextureBound[] textureBounds
    ) {
        this.origin = origin;
        this.rotation = rotation;
        this.size = size;
        this.textureBounds = textureBounds;
    }

    public Vector3Float getOrigin() {
        return origin;
    }

    public ModelCubeRotation getRotation() {
        return rotation;
    }

    public Vector3Float getSize() {
        return size;
    }

    public FacedTextureBound[] getTextureBounds() {
        return textureBounds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelCube modelCube = (ModelCube) o;
        return Objects.equals(origin, modelCube.origin)
                && Objects.equals(rotation, modelCube.rotation)
                && Objects.equals(size, modelCube.size)
                && Arrays.equals(textureBounds, modelCube.textureBounds
        );
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(origin, rotation, size);
        result = 31 * result + Arrays.hashCode(textureBounds);
        return result;
    }

    @Override
    public String toString() {
        return "ModelCube{" +
                "origin=" + origin +
                ", rotation=" + rotation +
                ", size=" + size +
                ", textureBounds=" + Arrays.toString(textureBounds) +
                '}';
    }
}