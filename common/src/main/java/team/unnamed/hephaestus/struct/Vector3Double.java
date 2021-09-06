package team.unnamed.hephaestus.struct;

import java.util.Objects;

/**
 * Represents a simple vector using 64-bit
 * double to represent the coordinates
 *
 * <p>Note that this class is immutable, some
 * operations create a new instance</p>
 */
public class Vector3Double {

    public static Vector3Double ZERO = new Vector3Double(0, 0, 0);
    public static Vector3Double ONE = new Vector3Double(1, 1, 1);

    private final double x;
    private final double y;
    private final double z;

    public Vector3Double(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3Double add(double x, double y, double z) {
        return new Vector3Double(this.x + x, this.y + y, this.z + z);
    }

    public Vector3Double subtract(double x, double y, double z) {
        return new Vector3Double(
                this.x - x,
                this.y - y,
                this.z - z
        );
    }

    public Vector3Double divide(double value) {
        return new Vector3Double(
                this.x / value,
                this.y / value,
                this.z / value
        );
    }

    public Vector3Double divide(double x, double y, double z) {
        return new Vector3Double(
                this.x / x,
                this.y / y,
                this.z / z
        );
    }

    public double dot(Vector3Double vector) {
        return this.x * vector.getX() + this.y * vector.getY() + this.z * vector.getZ();
    }

    public Vector3Double crossProduct(Vector3Double o) {
        double newX = y * o.z - o.y * z;
        double newY = z * o.x - o.z * x;
        double newZ = x * o.y - o.x * y;

        return new Vector3Double(
                newX,
                newY,
                newZ
        );
    }

    public Vector3Double multiply(double value) {
        return new Vector3Double(
                this.x * value,
                this.y * value,
                this.z * value
        );
    }

    public Vector3Double multiply(double x, double y, double z) {
        return new Vector3Double(
                this.x * x,
                this.y * y,
                this.z * z
        );
    }

    public Vector3Double add(Vector3Double vector) {
        return this.add(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vector3Double subtract(Vector3Double vector) {
        return this.subtract(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vector3Double multiply(Vector3Double vector) {
        return this.multiply(vector.getX(), vector.getY(), vector.getZ());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getInAxis(int axis) {
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

    public Vector3Double withX(double x) {
        return new Vector3Double(
                x,
                this.y,
                this.z
        );
    }

    public Vector3Double withY(double y) {
        return new Vector3Double(
                this.x,
                y,
                this.z
        );
    }

    public Vector3Double withZ(float z) {
        return new Vector3Double(
                this.x,
                this.y,
                z
        );
    }

    @Override
    public Vector3Double clone() {
        return new Vector3Double(
                x,
                y,
                z
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3Double that = (Vector3Double) o;
        return Double.compare(that.x, x) == 0
                && Double.compare(that.y, y) == 0
                && Double.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Vector3Double {" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

}