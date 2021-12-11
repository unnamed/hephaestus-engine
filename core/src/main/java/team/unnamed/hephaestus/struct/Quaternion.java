package team.unnamed.hephaestus.struct;

import java.util.Objects;

/**
 * Immutable representation of a 4-element quaternion
 * represented by a 64-bit floating point x, y, z and
 * w coordinates.
 *
 * <p>See <a href="https://en.wikipedia.org/wiki/Quaternion">
 *     Quaternion in Wikipedia</a></p>
 */
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

    /**
     * Multiplies this quaternion rotations with the given
     * {@code other} quaternion rotations
     *
     * <p>This method doesn't modify the current {@code this}
     * quaternion instance, since it's immutable, it returns
     * a new fresh quaternion instance as a result of the
     * operation</p>
     *
     * <p>See 'Multiplication of basis elements' section in
     * <a href="https://en.wikipedia.org/wiki/Quaternion">
     *     Wikipedia</a></p>
     */
    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
                x * other.w + w * other.x + y * other.z - z * other.y,
                y * other.w + w * other.y + z * other.x - x * other.z,
                z * other.w + w * other.z + x * other.y - y * other.x,
                w * other.w - x * other.x - y * other.y - z * other.z
        );
    }

    /**
     * Creates a {@link Quaternion} representation of the given
     * {@code euler} angles (in radians)
     */
    public static Quaternion fromEuler(Vector3Double euler) {

        // based on https://www.javatips.net/api/rotation-vector
        // -compass-master/RotationVectorCompass/src/com/adamrat
        // ana/rotationvectorcompass/math/Quaternion.java

        // common values
        double halfX = euler.getX() / 2D;
        double negHalfY = euler.getY() / -2D;
        double halfZ = euler.getZ() / 2D;

        // compute cos
        double cosX = Math.cos(halfX);
        double cosY = Math.cos(negHalfY);
        double cosZ = Math.cos(halfZ);

        // compute sin
        double sinX = Math.sin(halfX);
        double sinY = Math.sin(negHalfY);
        double sinZ = Math.sin(halfZ);

        // common values
        double sinXCosY = sinX * cosY;
        double cosXSinY = cosX * sinY;
        double cosXCosY = cosX * cosY;
        double sinXSinY = sinX * sinY;

        return new Quaternion(
                sinXCosY * cosZ + cosXSinY * sinZ,
                cosXSinY * cosZ - sinXCosY * sinZ,
                cosXCosY * sinZ + sinXSinY * cosZ,
                cosXCosY * cosZ - sinXSinY * sinZ
        );
    }

    // TODO: I think these methods should not be in this class!
    public static Vector3Double combine(Vector3Double origin, Vector3Double delta) {
        return fromEuler(origin)
                .multiply(fromEuler(delta))
                .toEuler();
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