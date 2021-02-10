package team.unnamed.hephaestus.model.animation;

import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.Objects;

/**
 * It's an animation keyframe,
 * contains the key-frame point and
 * the current pose (it can be a
 * position, a rotation or something
 * else accepted by {@link ModelBoneAnimation})
 */
public class KeyFrame {

    private final float position;
    private final Vector3Float value;

    public KeyFrame(float position, Vector3Float value) {
        this.position = position;
        this.value = value;
    }

    public float getPosition() {
        return position;
    }

    public Vector3Float getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyFrame keyFrame = (KeyFrame) o;
        return Float.compare(keyFrame.position, position) == 0
                && value.equals(keyFrame.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, value);
    }

    @Override
    public String toString() {
        return "KeyFrame{" +
                "position=" + position +
                ", value=" + value +
                '}';
    }
}
