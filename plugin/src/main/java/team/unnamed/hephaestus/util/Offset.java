package team.unnamed.hephaestus.util;

import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.struct.Vector3Float;

public class Offset {

    public static Vector3Float getRelativeLocation(Vector3Float origin, EulerAngle angle, double oX, double oY, double oZ) {
        Vector3Float offset = new Vector3Float((float) oX, (float) oY, (float) oZ);
        offset = rotatePitch(offset, angle.getX());
        offset = rotateYaw(offset, angle.getY());
        offset = rotateRoll(offset, angle.getZ());
        return origin.add(offset);
    }

    public static Vector3Float getRelativeLocation(EulerAngle angle, double oX, double oY, double oZ) {
        Vector3Float offset = new Vector3Float((float) oX, (float) oY, (float) oZ);
        offset = rotatePitch(offset, angle.getX());
        offset = rotateYaw(offset, angle.getY());
        offset = rotateRoll(offset, angle.getZ());
        return offset;
    }

    public static Vector3Float getRelativeLocation(EulerAngle angle, Vector3Float offset) {
        Vector3Float rotatedPitch = rotatePitch(offset, angle.getX());
        Vector3Float rotatedYaw = rotateYaw(rotatedPitch, angle.getY());
        return rotateRoll(rotatedYaw, angle.getZ());
    }

    public static Vector3Float rotateRoll(Vector3Float vec, double roll) {
        double sin = Math.sin(roll);
        double cos = Math.cos(roll);
        double x = vec.getX() * cos + vec.getY() * sin;
        double y = -vec.getX() * sin + vec.getY() * cos;

        return new Vector3Float((float) x, (float) y, vec.getZ());
    }

    public static Vector3Float rotatePitch(Vector3Float vec, double pitch) {
        double sin = Math.sin(pitch);
        double cos = Math.cos(pitch);
        double y = vec.getY() * cos - vec.getZ() * sin;
        double z = vec.getY() * sin + vec.getZ() * cos;

        return new Vector3Float(vec.getX(), (float) y, (float) z);
    }

    public static Vector3Float rotateYaw(Vector3Float vec, double yaw) {
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double x = vec.getX() * cos - vec.getZ() * sin;
        double z = vec.getX() * sin + vec.getZ() * cos;

        return new Vector3Float((float) x, vec.getY(), (float) z);
    }

    public static Vector3Float lerp(Vector3Float a, Vector3Float b, double ratio) {
        return b.subtract(a).multiply((float) ratio).add(a);
    }
}
