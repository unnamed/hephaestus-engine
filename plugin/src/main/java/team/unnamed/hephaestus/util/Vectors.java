package team.unnamed.hephaestus.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import team.unnamed.hephaestus.struct.Vector2Int;
import team.unnamed.hephaestus.struct.Vector3Float;

/**
 * Utility class for working with
 * vectors
 */
public final class Vectors {

    private Vectors() {
    }

    public static Vector3Float rotate(Vector3Float vector, float rotation) {
        double cos = Math.cos(rotation);
        double sin = Math.sin(rotation);

        return new Vector3Float(
                (float) (vector.getX() * cos + vector.getZ() * sin),
                vector.getY(),
                (float) (vector.getX() * -sin + vector.getZ() * cos)
        );
    }

    public static Vector3Float lerp(Vector3Float start, Vector3Float end, float percent) {
        return start.add(end.subtract(start).multiply(percent));
    }
    /**
     * Constructs a {@link Vector3Float} from
     * a {@link JsonElement} (must be a
     * {@link JsonArray}) by checking its elements
     * [x, y, z]
     */
    public static Vector3Float getVector3FloatFromJson(JsonElement element) {
        JsonArray array = element.getAsJsonArray();
        return new Vector3Float(
                array.get(0).getAsFloat(),
                array.get(1).getAsFloat(),
                array.get(2).getAsFloat()
        );
    }

    /**
     * Constructs a {@link Vector2Int} from
     * a {@link JsonElement} (must be a
     * {@link JsonArray}) by checking its
     * elements [x, y]
     */
    public static Vector2Int getVector2IntFromJson(JsonElement element) {
        JsonArray array = element.getAsJsonArray();
        return new Vector2Int(
                array.get(0).getAsInt(),
                array.get(1).getAsInt()
        );
    }

}