package team.unnamed.hephaestus.util;

import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.struct.Vector3Float;

public class Offset {

    public static Vector3Float rotateYaw(Vector3Float vec, float yaw) {
        float sin = (float) Math.sin(yaw);
        float cos = (float) Math.cos(yaw);
        float x = vec.getX() * cos - vec.getZ() * sin;
        float z = vec.getX() * sin + vec.getZ() * cos;
        return vec.setX(x).setZ(z);
    }

    public static Vector3Float getRelativeLocation(EulerAngle angle, Vector3Float offset) {
        offset = rotatePitch(offset, angle.getX());
        offset = rotateYaw(offset, angle.getY());
        offset = rotateRoll(offset, angle.getZ());
        return offset;
    }

    public static Vector3Float rotateRoll(Vector3Float vec, double roll) {
        float sin = (float) Math.sin(roll);
        float cos = (float) Math.cos(roll);
        float x = vec.getX() * cos + vec.getY() * sin;
        float y = -vec.getX() * sin + vec.getY() * cos;
        return vec.setX(x).setY(y);
    }

    public static Vector3Float rotatePitch(Vector3Float vec, double pitch) {
        float sin = (float) Math.sin(pitch);
        float cos = (float) Math.cos(pitch);
        float y = vec.getY() * cos - vec.getZ() * sin;
        float z = vec.getY() * sin + vec.getZ() * cos;
        return vec.setY(y).setZ(z);
    }

    public static Vector3Float rotateYaw(Vector3Float vec, double yaw) {
        float sin = (float) Math.sin(yaw);
        float cos = (float) Math.cos(yaw);
        float x = vec.getX() * cos - vec.getZ() * sin;
        float z = vec.getX() * sin + vec.getZ() * cos;
        return vec.setX(x).setZ(z);
    }

}