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
        float rX = Math.abs(this.rotation.getX());
        float rY = Math.abs(this.rotation.getY());
        float rZ = Math.abs(this.rotation.getZ());

        if ((((rX != 0.0F) ? 1 : 0) ^ ((rY != 0.0F) ? 1 : 0) ^ ((rZ != 0.0F) ? 1 : 0)) == 0 && (rX != 0.0F || rY != 0.0F || rZ != 0.0F))
            throw new IllegalStateException("Illegal cube detected. Cube rotated in multiple axis. [" + rX + ", " + rY + ", " + rZ + "]");
        if (rX > rY) {
            if (rX > rZ)
                return "x";
            return "z";
        }
        if (rY > rZ)
            return "y";
        return "z";
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