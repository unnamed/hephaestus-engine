package team.unnamed.hephaestus.resourcepack.java;

import team.unnamed.hephaestus.resourcepack.ModelGeometryTransformer;
import team.unnamed.hephaestus.struct.Vector3Float;

public class JavaDisplay {

    private Vector3Float translation;
    private final Vector3Float rotation;
    private final Vector3Float scale;

    public JavaDisplay(Vector3Float translation, Vector3Float rotation, Vector3Float scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3Float getTranslation() {
        return translation;
    }

    public Vector3Float getRotation() {
        return rotation;
    }

    public Vector3Float getScale() {
        return scale;
    }

    public void moveTranslation(float offsetX, float offsetY, float offsetZ) {
        this.translation = new Vector3Float(
                translation.getX() - offsetX * ModelGeometryTransformer.DISPLAY_SCALE,
                translation.getY() - offsetY * ModelGeometryTransformer.DISPLAY_SCALE,
                translation.getZ() - offsetZ * ModelGeometryTransformer.DISPLAY_SCALE
        );
        if (
                Math.abs(translation.getX()) > 80
                || Math.abs(this.translation.getY()) > 80
                || Math.abs(this.translation.getZ()) > 80
        ) {
            throw new IllegalStateException("Transition value cannot be higher or lower than 80");
        }
    }
}