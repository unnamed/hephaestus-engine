package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.struct.Vector3Float;

/**
 * Class that holds descriptive
 * data about a specific {@link ModelGeometry}
 */
public class ModelDescription {

    private final String identifier;
    private final int textureWidth;
    private final int textureHeight;
    private final int visibleBoundsWidth;
    private final int visibleBoundsHeight;
    private final Vector3Float visibleBoundsOffset;

    public ModelDescription(
            String identifier,
            int textureWidth,
            int textureHeight,
            int visibleBoundsWidth,
            int visibleBoundsHeight,
            Vector3Float visibleBoundsOffset
    ) {
        this.identifier = identifier;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.visibleBoundsWidth = visibleBoundsWidth;
        this.visibleBoundsHeight = visibleBoundsHeight;
        this.visibleBoundsOffset = visibleBoundsOffset;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public int getVisibleBoundsWidth() {
        return visibleBoundsWidth;
    }

    public int getVisibleBoundsHeight() {
        return visibleBoundsHeight;
    }

    public Vector3Float getVisibleBoundsOffset() {
        return visibleBoundsOffset;
    }

}