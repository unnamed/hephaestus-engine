package team.unnamed.hephaestus.serialize;

import com.google.gson.*;
import team.unnamed.hephaestus.struct.Vector2Int;
import team.unnamed.hephaestus.util.Serialization;

import java.lang.reflect.Type;

public class Vector2IntCodec implements JsonSerializer<Vector2Int>, JsonDeserializer<Vector2Int> {

    @Override
    public Vector2Int deserialize(
            JsonElement element,
            Type type,
            JsonDeserializationContext context
    ) throws JsonParseException {
        if (!element.isJsonArray()) {
            throw new JsonParseException("Expected an array containing the coordinates. Given: " + element);
        }
        JsonArray array = element.getAsJsonArray();
        if (array.size() != 2) {
            throw new JsonParseException("Invalid array size, expected 2, given: " + array.size());
        }
        return Serialization.getVector2IntFromJson(array);
    }

    @Override
    public JsonElement serialize(
            Vector2Int vector,
            Type type,
            JsonSerializationContext context
    ) {
        JsonArray array = new JsonArray();
        array.add(vector.getX());
        array.add(vector.getY());
        return array;
    }
}
