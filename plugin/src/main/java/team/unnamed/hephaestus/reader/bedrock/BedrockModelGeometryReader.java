package team.unnamed.hephaestus.reader.bedrock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import team.unnamed.hephaestus.model.*;
import team.unnamed.hephaestus.model.texture.bound.FacedTextureBound;
import team.unnamed.hephaestus.model.texture.bound.TextureFace;
import team.unnamed.hephaestus.reader.blockbench.BlockbenchModelGeometryReader;
import team.unnamed.hephaestus.struct.Vector2Int;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Serialization;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Old implementation of ModelGeometryReader that
 * parses the inputs to JSON (Format used by the
 * Bedrock Model Geometry format) and then reads the values.
 * It is recommended to use {@link BlockbenchModelGeometryReader}
 * instead.
 * <p>The Bedrock Model Geometry format is supported by
 * some modelling tools like Blockbench</p>
 */
@Deprecated
public class BedrockModelGeometryReader {

    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "1.12.0"
    );

    public ModelGeometry read(Reader reader) throws IOException {
        JsonObject json = JSON_PARSER.parse(reader).getAsJsonObject();
        JsonElement formatVersionElement = json.get("format_version");

        if (
                formatVersionElement == null
                || !SUPPORTED_FORMATS.contains(formatVersionElement.getAsString())
        ) {
            throw new IOException("Provided JSON doesn't have a valid format version");
        }

        JsonElement geometryArrayElement = json.get("minecraft:geometry");
        JsonArray geometryArray;

        if (
                geometryArrayElement == null
                || (geometryArray = geometryArrayElement.getAsJsonArray()).size() < 1
        ) {
            throw new IOException("Provided json does not have a minecraft geometry array");
        }

        JsonObject geometryObject = geometryArray.get(0).getAsJsonObject();
        JsonObject descriptionJson = geometryObject.getAsJsonObject("description");

        ModelDescription description = new ModelDescription(
                descriptionJson.get("texture_width").getAsInt(),
                descriptionJson.get("texture_height").getAsInt()
        );

        Map<ModelBone, String> parentsByBone = new LinkedHashMap<>();

        JsonArray bonesArray = geometryObject.getAsJsonArray("bones");

        for (JsonElement boneElement : bonesArray) {

            JsonObject boneJson = boneElement.getAsJsonObject();
            String name = boneJson.get("name").getAsString();

            Vector3Float pivot = Serialization.getVector3FloatFromJson(boneJson.get("pivot"));

            List<ModelComponent> cubes = new ArrayList<>();
            JsonArray cubesArray = boneJson.get("cubes").getAsJsonArray();
            for (JsonElement cubeElement : cubesArray) {

                JsonObject cubeJson = cubeElement.getAsJsonObject();

                JsonElement cubePivotElement = cubeJson.get("pivot");
                Vector3Float cubePivot = cubePivotElement == null
                        ? Vector3Float.zero()
                        : Serialization.getVector3FloatFromJson(cubePivotElement);

                Vector3Float origin = Serialization.getVector3FloatFromJson(cubeJson.get("origin"));
                Vector3Float size = Serialization.getVector3FloatFromJson(cubeJson.get("size"));

                JsonElement rotationElement = cubeJson.get("rotation");
                Vector3Float rotation = rotationElement != null && rotationElement.isJsonArray()
                        ? Serialization.getVector3FloatFromJson(rotationElement)
                        : Vector3Float.zero();

                if (cubeJson.get("uv").isJsonArray()) {
                    throw new IOException("Box UV not supported, please turn it off");
                }

                FacedTextureBound[] textureBounds = new FacedTextureBound[TextureFace.values().length];
                cubeJson.get("uv").getAsJsonObject().entrySet().forEach(uvEntry -> {

                    TextureFace face = TextureFace.valueOf(uvEntry.getKey().toUpperCase());
                    JsonObject uvJson = uvEntry.getValue().getAsJsonObject();
                    Vector2Int uvBounds = Serialization.getVector2IntFromJson(uvJson.get("uv"));
                    Vector2Int uvSize = Serialization.getVector2IntFromJson(uvJson.get("uv_size"));

                    textureBounds[face.ordinal()] = new FacedTextureBound(
                            uvBounds,
                            uvSize,
                            777 //THIS DOES NOT WORK
                    );
                });

                cubes.add(new ModelCube(
                        origin,
                        cubePivot,
                        rotation,
                        size,
                        textureBounds
                ));
            }

            ModelBone bone = new ModelBone(
                    name,
                    pivot,
                    cubes
            );

            String parentName = boneJson.has("parent")
                    ? boneJson.get("parent").getAsString()
                    : null;

            parentsByBone.put(bone, parentName);
        }

        List<ModelBone> parentedBones = new ArrayList<>();
        parentsByBone.forEach((bone, parent) -> {
            if (parent == null) {
                parentedBones.add(bone);
            } else {
                for (ModelBone mappedBone : parentsByBone.keySet()) {
                    if (mappedBone.getName().equals(parent)) {
                        mappedBone.getComponents().add(bone);
                    }
                }
            }
        });

        return new ModelGeometry(description, parentedBones, new HashMap<>());
    }

}