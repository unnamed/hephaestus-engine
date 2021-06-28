package team.unnamed.hephaestus.model.texture.bound;

import team.unnamed.hephaestus.struct.Vector2Int;

import java.util.Objects;

/**
 * Class that holds the bounds of a
 * texture, it's used to take a section
 * of a texture to use it in a cube.
 */
public class FacedTextureBound {

    /** The origin coordinates */
    private final Vector2Int bounds;
    /** The size (origin + size = end coordinates) */
    private final Vector2Int size;

    /** Texture id used when exporting */
    private final int textureId;

    public FacedTextureBound(Vector2Int bounds, Vector2Int size, int textureId) {
        this.bounds = bounds;
        this.size = size;
        this.textureId = textureId;
    }

    public Vector2Int getBounds() {
        return bounds;
    }

    public Vector2Int getSize() {
        return size;
    }

    public int getTextureId() {
        return textureId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacedTextureBound that = (FacedTextureBound) o;
        return bounds.equals(that.bounds)
                && size.equals(that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bounds, size);
    }

    @Override
    public String toString() {
        return "FacedTextureBound{" +
                "bounds=" + bounds +
                ", size=" + size +
                '}';
    }
}