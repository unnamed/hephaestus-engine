package team.unnamed.hephaestus.struct;

import java.util.Objects;

public class Quaternion {

    private final double x;
    private final double y;
    private final double z;
    private final double w;

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Returns this quaternion represented as an
     * Euler Angle (in ZXY order) in radians.
     *
     * <p>See https://www.euclideanspace.com/maths/
     * geometry/rotations/conversions/quaternionToEuler
     * /indexLocal.htm</p>
     */
    public Vector3Double toEuler() {

        double test = x * z + y * w;

        // singularity at north pole
        if (test > 0.499) {
            return new Vector3Double(
                    Math.atan2(x, w),
                    Math.PI / 2,
                    0
            );
        }

        // singularity at south pole
        if (test < -0.499) {
            return new Vector3Double(
                    -Math.atan2(x, w),
                    -Math.PI / 2,
                    0
            );
        }

        double sqx = x * x;
        double sqy = y * y;
        double sqz = z * z;

        double x2 = x + x;
        double y2 = y + y;
        double z2 = z + z;

        return new Vector3Double(
                Math.atan2(w * x2 - y * z2, 1 - 2 * (sqx + sqy)),
                -Math.asin(2 * test),
                Math.atan2(w * z2 - x * y2, 1 - 2 * (sqz + sqy))
        );
    }

    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
                x * other.w + w * other.x + y * other.z - z * other.y,
                y * other.w + w * other.y + z * other.x - x * other.z,
                z * other.w + w * other.z + x * other.y - y * other.x,
                w * other.w - x * other.x - y * other.y - z * other.z
        );
    }

    public static Quaternion fromEuler(Vector3Double euler) {
        double cosX = Math.cos(euler.getX() / 2D);
        double cosY = Math.cos(euler.getY() / -2D);
        double cosZ = Math.cos(euler.getZ() / 2D);
        double sinX = Math.sin(euler.getX() / 2D);
        double sinY = Math.sin(euler.getY() / -2D);
        double sinZ = Math.sin(euler.getZ() / 2D);
        return new Quaternion(
                sinX * cosY * cosZ + cosX * sinY * sinZ,
                cosX * sinY * cosZ - sinX * cosY * sinZ,
                cosX * cosY * sinZ + sinX * sinY * cosZ,
                cosX * cosY * cosZ - sinX * sinY * sinZ
        );
    }

    public static Vector3Double combine(Vector3Double origin, Vector3Double delta) {
        return fromEuler(origin)
                .multiply(fromEuler(delta))
                .toEuler();
    }

    public static Vector3Double lerp(Vector3Double a, Vector3Double b, double t) {
        return new Vector3Double(
                (b.getX() - a.getX()) * t + a.getX(),
                (b.getY() - a.getY()) * t + a.getY(),
                (b.getZ() - a.getZ()) * t + a.getZ()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quaternion that = (Quaternion) o;
        return Double.compare(that.x, x) == 0
                && Double.compare(that.y, y) == 0
                && Double.compare(that.z, z) == 0
                && Double.compare(that.w, w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return "Quaternion{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }

}