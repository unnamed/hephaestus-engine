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

    /** The size of the cube */
    private final Vector3Float size;

    /**
     * The component texture bounds indexed
     * by the {@link TextureFace} ordinals
     */
    private final FacedTextureBound[] textureBounds;

    public ModelCube(
            Vector3Float origin,
            Vector3Float size,
            FacedTextureBound[] textureBounds
    ) {
        this.origin = origin;
        this.size = size;
        this.textureBounds = textureBounds;
    }

    public Vector3Float getOrigin() {
        return origin;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelCube that = (ModelCube) o;
        return origin.equals(that.origin)
                && size.equals(that.size)
                && Arrays.equals(textureBounds, that.textureBounds);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(origin, size);
        result = 31 * result + Arrays.hashCode(textureBounds);
        return result;
    }

    @Override
    public String toString() {
        return "ModelComponent{" +
                "origin=" + origin +
                ", size=" + size +
                ", textureBounds=" + Arrays.toString(textureBounds) +
                '}';
    }
}
