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

    public static Vector3Float ZERO = new Vector3Float(0, 0, 0);
    public static Vector3Float ONE = new Vector3Float(1, 1, 1);

    private final float x;
    private final float y;
    private final float z;

    public Vector3Float(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3Float add(float x, float y, float z) {
        return new Vector3Float(this.x + x, this.y + y, this.z + z);
    }

    public Vector3Float subtract(float x, float y, float z) {
        return new Vector3Float(
                this.x - x,
                this.y - y,
                this.z - z
        );
    }

    public Vector3Float divide(float value) {
        return new Vector3Float(
                this.x / value,
                this.y / value,
                this.z / value
        );
    }

    public Vector3Float divide(float x, float y, float z) {
        return new Vector3Float(
                this.x / x,
                this.y / y,
                this.z / z
        );
    }

    public float dot(Vector3Float vector) {
        return this.x * vector.getX() + this.y * vector.getY() + this.z * vector.getZ();
    }

    public Vector3Float crossProduct(Vector3Float o) {
        float newX = y * o.z - o.y * z;
        float newY = z * o.x - o.z * x;
        float newZ = x * o.y - o.x * y;

        return new Vector3Float(
                newX,
                newY,
                newZ
        );
    }

    public Vector3Float multiply(float value) {
        return new Vector3Float(
                this.x * value,
                this.y * value,
                this.z * value
        );
    }

    public Vector3Float multiply(float x, float y, float z) {
        return new Vector3Float(
                this.x * x,
                this.y * y,
                this.z * z
        );
    }

    public Vector3Float add(Vector3Float vector) {
        return this.add(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vector3Float subtract(Vector3Float vector) {
        return this.subtract(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vector3Float multiply(Vector3Float vector) {
        return this.multiply(vector.getX(), vector.getY(), vector.getZ());
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

    public float getInAxis(int axis) {
        switch (axis) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

    public Vector3Float withX(float x) {
        return new Vector3Float(
                x,
                this.y,
                this.z
        );
    }

    public Vector3Float withY(float y) {
        return new Vector3Float(
                this.x,
                y,
                this.z
        );
    }

    public Vector3Float withZ(float z) {
        return new Vector3Float(
                this.x,
                this.y,
                z
        );
    }

    @Override
    public Vector3Float clone() {
        return new Vector3Float(
                x,
                y,
                z
        );
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