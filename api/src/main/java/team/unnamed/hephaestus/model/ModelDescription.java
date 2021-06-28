package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.struct.Vector3Float;

/**
 * Class that holds descriptive
 * data about a specific {@link ModelGeometry}
 */
public class ModelDescription {

    private final int textureWidth;
    private final int textureHeight;

    public ModelDescription(
            int textureWidth,
            int textureHeight
    ) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

}