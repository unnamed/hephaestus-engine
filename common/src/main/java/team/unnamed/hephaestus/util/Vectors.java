package team.unnamed.hephaestus.util;

import team.unnamed.hephaestus.struct.Vector3Double;
import team.unnamed.hephaestus.struct.Vector3Float;

/**
 * Utility class for working with
 * vectors
 */
public final class Vectors {

    private Vectors() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static Vector3Double toRadians(Vector3Float vector) {
        return new Vector3Double(
                Math.toRadians(vector.getX()),
                Math.toRadians(vector.getY()),
                Math.toRadians(vector.getZ())
        );
    }

    public static Vector3Float rotateAroundY(Vector3Float vector, double angle) {
        double sin =  Math.sin(angle);
        double cos = Math.cos(angle);

        return new Vector3Float(
                (float) (vector.getX() * cos - vector.getZ() * sin),
                vector.getY(),
                (float) (vector.getX() * sin + vector.getZ() * cos)
        );
    }

    public static Vector3Float rotate(Vector3Float vector, Vector3Double rotation) {

        double cosX = Math.cos(rotation.getX()), sinX = Math.sin(rotation.getX());
        double cosY = Math.cos(rotation.getY()), sinY = Math.sin(rotation.getY());
        double cosZ = Math.cos(rotation.getZ()), sinZ = Math.sin(rotation.getZ());

        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        // rotate around X axis
        double xy = y * cosX - z * sinX;
        double xz = y * sinX + z * cosX;

        // rotate around Y axis
        double yx = x * cosY - xz * sinY;
        double yz = x * sinY + xz * cosY;

        // rotate around Z axis
        double zx = yx * cosZ + xy * sinZ;
        double zy = -yx * sinZ + xy * cosZ;

        return new Vector3Float((float) zx, (float) zy, (float) yz);
    }

    public static Vector3Float lerp(Vector3Float start, Vector3Float end, float percent) {
        return start.add(end.subtract(start).multiply(percent));
    }
}