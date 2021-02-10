package team.unnamed.hephaestus.struct;

import java.util.Objects;

/**
 * Represents a simple vector using 32-bit
 * float to represent the coordinates
 *
 * <p>Note that this class is immutable, some
 * operations create a new instance</p>
 */
public class Vector3Float {

    private final float x;
    private final float y;
    private final float z;

    public Vector3Float(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3Float that = (Vector3Float) o;
        return Float.compare(that.x, x) == 0
                && Float.compare(that.y, y) == 0
                && Float.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Vector3Float {" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
