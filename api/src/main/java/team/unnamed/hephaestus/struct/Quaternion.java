package team.unnamed.hephaestus.struct;

import org.bukkit.util.EulerAngle;

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

    public EulerAngle toEuler() {
        double x2 = x + x;
        double y2 = y + y;
        double z2 = z + z;
        double xx = x * x2;
        double xy = x * y2;
        double xz = x * z2;
        double yy = y * y2;
        double yz = y * z2;
        double zz = z * z2;
        double wx = w * x2;
        double wy = w * y2;
        double wz = w * z2;
        double m11 = 1.0 - (yy + zz);
        double m12 = xy - wz;
        double m13 = xz + wy;
        double m14 = 1.0 - (xx + zz);
        double m15 = yz - wx;
        double m16 = yz + wx;
        double m17 = 1.0 - (xx + yy);
        double ey = Math.asin(Math.min(Math.max(m13, -1), 1));
        double ex;
        double ez;
        if (Math.abs(m13) < 0.99999) {
            ex = Math.atan2(-m15, m17);
            ez = Math.atan2(-m12, m11);
        } else {
            ex = Math.atan2(m16, m14);
            ez = 0.0;
        }
        return new EulerAngle(ex, -ey, ez);
    }

    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
                x * other.w + w * other.x + y * other.z - z * other.y,
                y * other.w + w * other.y + z * other.x - x * other.z,
                z * other.w + w * other.z + x * other.y - y * other.x,
                w * other.w - x * other.x - y * other.y - z * other.z
        );
    }

    public static Quaternion fromEuler(EulerAngle euler) {
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

    public static EulerAngle combine(EulerAngle origin, EulerAngle delta) {
        return fromEuler(origin)
                .multiply(fromEuler(delta))
                .toEuler();
    }

    public static EulerAngle lerp(EulerAngle a, EulerAngle b, double t) {
        return new EulerAngle(
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