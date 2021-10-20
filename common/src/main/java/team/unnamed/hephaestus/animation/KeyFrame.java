package team.unnamed.hephaestus.animation;

import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.Objects;

/**
 * Represents an animation keyframe, a point in
 * the
 */
public class KeyFrame {

    public static final KeyFrame INITIAL = new KeyFrame(
            Vector3Float.ZERO,
            Vector3Float.ZERO,
            Vector3Float.ONE
    );

    private final Vector3Float position;
    private final Vector3Float rotation;
    private final Vector3Float scale;

    public KeyFrame(
            Vector3Float position,
            Vector3Float rotation,
            Vector3Float scale
    ) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3Float getPosition() {
        return position;
    }

    public Vector3Float getRotation() {
        return rotation;
    }

    public Vector3Float getScale() {
        return scale;
    }

    @Override
    public KeyFrame clone() {
       return new KeyFrame(position, rotation, scale);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyFrame keyFrame = (KeyFrame) o;
        return position.equals(keyFrame.position)
                && rotation.equals(keyFrame.rotation)
                && scale.equals(keyFrame.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, rotation, scale);
    }

    @Override
    public String toString() {
        return "KeyFrame{" +
                "position=" + position +
                ", rotation=" + rotation +
                ", scale=" + scale +
                '}';
    }

}