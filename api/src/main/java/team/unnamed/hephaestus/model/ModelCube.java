package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.model.texture.bound.FacedTextureBound;
import team.unnamed.hephaestus.model.texture.bound.TextureFace;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents the most basic component
 * of a model, similar to the Bedrock
 * Model Geometry Cube
 */
public class ModelCube implements ModelComponent {

    /** The origin of the cube */
    private final Vector3Float origin;

    /** The pivot of the cube */
    private final Vector3Float pivot;

    /** The initial rotation of the cube, value can be null */
    private final Vector3Float rotation;

    /** The size of the cube */
    private final Vector3Float size;

    /**
     * The component texture bounds indexed
     * by the {@link TextureFace} ordinals
     */
    private final FacedTextureBound[] textureBounds;

    public ModelCube(
            Vector3Float origin,
            Vector3Float pivot,
            Vector3Float rotation,
            Vector3Float size,
            FacedTextureBound[] textureBounds
    ) {
        this.origin = origin;
        this.pivot = pivot;
        this.rotation = rotation;
        this.size = size;
        this.textureBounds = textureBounds;
    }

    public Vector3Float getOrigin() {
        return origin;
    }

    @Override
    public Vector3Float getPivot() {
        return pivot;
    }

    public Vector3Float getRotation() {
        return rotation;
    }

    public Vector3Float getSize() {
        return size;
    }

    public FacedTextureBound getTextureBound(TextureFace face) {
        return textureBounds[face.ordinal()];
    }

    public FacedTextureBound[] getTextureBounds() {
        return textureBounds;
    }

    public String getRotationAxis() {
        if ((((this.rotation.getX() != 0.0F) ? 1 : 0) ^ ((this.rotation.getY() != 0.0F) ? 1 : 0) ^ ((this.rotation.getZ() != 0.0F) ? 1 : 0)) == 0 && (this.rotation.getX() != 0.0F || this.rotation.getY() != 0.0F || this.rotation.getZ() != 0.0F))
            throw new UnsupportedOperationException("Cube rotated in multiple axis");
        if (this.rotation.getX() != 0.0F)
            return "x";
        if (this.rotation.getY() != 0.0F)
            return "y";
        if (this.rotation.getZ() != 0.0F)
            return "z";
        return "x";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelCube modelCube = (ModelCube) o;
        return Objects.equals(origin, modelCube.origin)
                && Objects.equals(pivot, modelCube.pivot)
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
                ", pivot=" + pivot +
                ", rotation=" + rotation +
                ", size=" + size +
                ", textureBounds=" + Arrays.toString(textureBounds) +
                '}';
    }
}