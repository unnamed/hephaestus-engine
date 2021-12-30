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