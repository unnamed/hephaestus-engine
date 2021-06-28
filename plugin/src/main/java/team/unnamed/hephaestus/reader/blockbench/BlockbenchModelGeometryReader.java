package team.unnamed.hephaestus.reader.blockbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import team.unnamed.hephaestus.model.*;
import team.unnamed.hephaestus.model.texture.bound.FacedTextureBound;
import team.unnamed.hephaestus.model.texture.bound.TextureFace;
import team.unnamed.hephaestus.reader.ModelGeometryReader;
import team.unnamed.hephaestus.struct.Vector2Int;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Implementation of {@link ModelGeometryReader} that
 * parses the inputs to JSON (Format used by the
 * Blockbench modelling tool) and then reads the values.
 *
 * <p>The Blockbench format is explicitly supported
 *  by some modelling tools like Blockbench</p>
 */
public class BlockbenchModelGeometryReader implements ModelGeometryReader {

    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "3.6"
    );

    @Override
    public ModelGeometry read(Reader reader) throws IOException {
        JsonObject json = JSON_PARSER.parse(reader).getAsJsonObject();

        JsonElement formatVersionElement = json.get("meta").getAsJsonObject().get("format_version");

        if (
                formatVersionElement == null
                        || !SUPPORTED_FORMATS.contains(formatVersionElement.getAsString())
        ) {
            throw new IOException("Provided JSON doesn't have a valid format version");
        }

        JsonObject resolutionJson = json.getAsJsonObject("resolution");
        ModelDescription description = new ModelDescription(
                resolutionJson.get("width").getAsInt(),
                resolutionJson.get("height").getAsInt()
        );

        Map<String, ModelCube> cubeIdMap = new HashMap<>();
        JsonArray cubesArray = json.getAsJsonArray("elements");
        for (JsonElement cubeElement : cubesArray) {
            JsonObject cubeJson = cubeElement.getAsJsonObject();

            Vector3Float pivot = Vectors.getVector3FloatFromJson(cubeJson.get("origin")).multiply(-1, 1, 1);
            Vector3Float to = Vectors.getVector3FloatFromJson(cubeJson.get("to"));
            Vector3Float from = Vectors.getVector3FloatFromJson(cubeJson.get("from"));

            Vector3Float origin = new Vector3Float(
                    -to.getX(),
                    from.getY(),
                    from.getZ()
            );
            Vector3Float size = new Vector3Float(
                    round(to.getX() - from.getX()),
                    round(to.getY() - from.getY()),
                    round(to.getZ() - from.getZ())
            );

            JsonElement rotationElement = cubeJson.get("rotation");
            Vector3Float rotation = rotationElement != null && rotationElement.isJsonArray()
                    ? Vectors.getVector3FloatFromJson(rotationElement).multiply(-1, 1, 1)
                    : Vector3Float.zero();

            FacedTextureBound[] textureBounds = new FacedTextureBound[TextureFace.values().length];
            cubeJson.get("faces").getAsJsonObject().entrySet().forEach(faceEntry -> {
                TextureFace face = TextureFace.valueOf(faceEntry.getKey().toUpperCase());
                JsonArray uvJson = faceEntry.getValue().getAsJsonObject().get("uv").getAsJsonArray();

                Vector2Int uvBounds = new Vector2Int(
                        uvJson.get(0).getAsInt(),
                        uvJson.get(1).getAsInt()
                );

                Vector2Int uvSize = new Vector2Int(
                        uvJson.get(2).getAsInt() - uvJson.get(0).getAsInt(),
                        uvJson.get(3).getAsInt() - uvJson.get(1).getAsInt()
                );

                if (face == TextureFace.UP || face == TextureFace.DOWN) {
                    uvBounds = new Vector2Int(
                            uvJson.get(2).getAsInt(),
                            uvJson.get(3).getAsInt()
                    );

                    uvSize = new Vector2Int(
                            uvJson.get(0).getAsInt() - uvJson.get(2).getAsInt(),
                            uvJson.get(1).getAsInt() - uvJson.get(3).getAsInt()
                    );
                }

                textureBounds[face.ordinal()] = new FacedTextureBound(
                        uvBounds,
                        uvSize
                );
            });

            cubeIdMap.put(cubeJson.get("uuid").getAsString(), new ModelCube(
                    origin,
                    pivot,
                    rotation,
                    size,
                    textureBounds
            ));
        }

        List<ModelBone> bones = new ArrayList<>();
        json.get("outliner").getAsJsonArray().forEach(boneElement -> {
            if (boneElement.isJsonObject()) {
                bones.add(createBone(cubeIdMap, boneElement.getAsJsonObject()));
            }
        });

        return new ModelGeometry(description, bones);
    }

    private float round(float number) {
        return (float) (Math.round(number * 100.0) / 100.0);
    }

    private ModelBone createBone(Map<String, ModelCube> cubeIdMap, JsonObject json) {
        String name = json.get("name").getAsString();
        Vector3Float pivot = Vectors.getVector3FloatFromJson(json.get("origin")).multiply(-1, 1, 1);

        List<ModelComponent> components = new ArrayList<>();
        json.get("children").getAsJsonArray().forEach(componentElement -> {
            if (componentElement.isJsonObject()) {
                components.add(createBone(cubeIdMap, componentElement.getAsJsonObject()));
            } else {
                components.add(cubeIdMap.get(componentElement.getAsString()));
            }
        });

        return new ModelBone(name, pivot, components);
    }
}