package team.unnamed.hephaestus.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import team.unnamed.hephaestus.struct.Vector2Int;
import team.unnamed.hephaestus.struct.Vector3Float;

/**
 * Utility class for helping with
 * serialization/deserialization
 */
public final class Serialization {

    private Serialization() {
    }

    /**
     * Checks if the given {@code element} is a
     * string, if yes, it replaces the commas (,)
     * by dots and invokes {@link Float#parseFloat}
     * to parse the float, if not, it just calls
     * {@link JsonElement#getAsFloat()}
     */
    public static float parseLenientFloat(JsonElement element) {
        return element.getAsJsonPrimitive().isString()
                ? Float.parseFloat(element.getAsString().replace(',', '.'))
                : element.getAsFloat();
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
