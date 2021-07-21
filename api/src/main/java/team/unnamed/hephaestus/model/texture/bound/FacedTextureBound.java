package team.unnamed.hephaestus.model.texture.bound;

import java.util.Arrays;
import java.util.Objects;

/**
 * Class that holds the bounds of a
 * texture, it's used to take a section
 * of a texture to use it in a cube.
 */
public class FacedTextureBound {

    /** The origin coordinates */
    private final float[] bounds;

    /** Texture id used when exporting */
    private final int textureId;

    public FacedTextureBound(float[] bounds, int textureId) {
        this.bounds = bounds;
        this.textureId = textureId;
    }

    public float[] getBounds() {
        return bounds;
    }

    public int getTextureId() {
        return textureId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacedTextureBound that = (FacedTextureBound) o;
        return textureId == that.textureId && Arrays.equals(bounds, that.bounds);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(textureId);
        result = 31 * result + Arrays.hashCode(bounds);
        return result;
    }

    @Override
    public String toString() {
        return "FacedTextureBound{" +
                "bounds=" + Arrays.toString(bounds) +
                ", textureId=" + textureId +
                '}';
    }
}