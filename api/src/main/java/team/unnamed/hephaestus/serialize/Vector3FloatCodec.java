package team.unnamed.hephaestus.serialize;

import com.google.gson.*;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Serialization;

import java.lang.reflect.Type;

public class Vector3FloatCodec implements JsonSerializer<Vector3Float>, JsonDeserializer<Vector3Float> {

    @Override
    public Vector3Float deserialize(
            JsonElement element,
            Type type,
            JsonDeserializationContext context
    ) throws JsonParseException {
        if (!element.isJsonArray()) {
            throw new JsonParseException("Expected an array containing the coordinates. Given: " + element);
        }
        JsonArray array = element.getAsJsonArray();
        if (array.size() != 3) {
            throw new JsonParseException("Invalid array size, expected 3, given: " + array.size());
        }
        return Serialization.getVector3FloatFromJson(element);
    }

    @Override
    public JsonElement serialize(
            Vector3Float vector,
            Type type,
            JsonSerializationContext context
    ) {
        JsonArray array = new JsonArray();
        array.add(vector.getX());
        array.add(vector.getY());
        array.add(vector.getZ());
        return array;
    }
}
