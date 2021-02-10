package team.unnamed.hephaestus.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import team.unnamed.hephaestus.model.*;
import team.unnamed.hephaestus.model.texture.bound.FacedTextureBound;
import team.unnamed.hephaestus.model.texture.bound.TextureFace;
import team.unnamed.hephaestus.struct.Vector2Int;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.io.Reader;
import java.util.*;

/**
 * Implementation of {@link ModelGeometryReader} that
 * parses the inputs to JSON (Format used by the
 * Bedrock Model Geometry format) and then reads the values.
 *
 * <p>The Bedrock Model Geometry format is supported by
 * some modelling tools like Blockbench</p>
 */
public class BedrockModelGeometryReader implements ModelGeometryReader {

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "1.12.0"
    );

    @Override
    public ModelGeometry load(Reader reader) throws Exception {
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

        if (!json.has("format_version")) {
            throw new IllegalArgumentException("Provided json does not have a clear format version");
        }

        String format = json.get("format_version").getAsString();
        if (!SUPPORTED_FORMATS.contains(format)) {
            throw new IllegalArgumentException("Format " + format + " is not supported by this ModelGeometryReader");
        }

        if (!json.has("minecraft:geometry")) {
            throw new IllegalArgumentException("Provided json does not have a minecraft geometry array");
        }

        JsonArray geometryArray = json.get("minecraft:geometry").getAsJsonArray();
        if (geometryArray.size() <= 0) {
            throw new IllegalArgumentException("Geometry does not have a proper object");
        }

        JsonObject geometryObject = geometryArray.get(0).getAsJsonObject();

        JsonObject descriptionJson = geometryObject.get("description").getAsJsonObject();
        ModelDescription description = new ModelDescription(
                descriptionJson.get("identifier").getAsString()
        );

        Map<ModelBone, String> boneParentMap = new HashMap<>();
        JsonArray bonesArray = geometryObject.get("bones").getAsJsonArray();
        bonesArray.forEach(boneElement -> {

            JsonObject boneJson = boneElement.getAsJsonObject();
            String name = boneJson.get("name").getAsString();

            JsonArray pivotArray = boneJson.get("pivot").getAsJsonArray();
            Vector3Float pivot = new Vector3Float(
                    pivotArray.get(0).getAsFloat(),
                    pivotArray.get(1).getAsFloat(),
                    pivotArray.get(2).getAsFloat()
            );

            List<ModelComponent> cubes = new ArrayList<>();
            JsonArray cubesArray = boneJson.get("cubes").getAsJsonArray();
            cubesArray.forEach(cubeElement -> {

                JsonObject cubeJson = cubeElement.getAsJsonObject();

                JsonArray originArray = cubeJson.get("origin").getAsJsonArray();
                Vector3Float origin = new Vector3Float(
                        originArray.get(0).getAsFloat(),
                        originArray.get(1).getAsFloat(),
                        originArray.get(2).getAsFloat()
                );

                JsonArray sizeArray = cubeJson.get("size").getAsJsonArray();
                Vector3Float size = new Vector3Float(
                        sizeArray.get(0).getAsFloat(),
                        sizeArray.get(1).getAsFloat(),
                        sizeArray.get(2).getAsFloat()
                );

                FacedTextureBound[] textureBounds = new FacedTextureBound[TextureFace.values().length];
                cubeJson.get("uv").getAsJsonObject().entrySet().forEach(uvEntry -> {

                    TextureFace face = TextureFace.valueOf(uvEntry.getKey().toUpperCase());
                    JsonObject uvJson = uvEntry.getValue().getAsJsonObject();

                    JsonArray boundsArray = uvJson.get("uv").getAsJsonArray();
                    Vector2Int uvBounds = new Vector2Int(
                            boundsArray.get(0).getAsInt(),
                            boundsArray.get(1).getAsInt()
                    );

                    JsonArray uvSizeArray = uvJson.get("uv_size").getAsJsonArray();
                    Vector2Int uvSize = new Vector2Int(
                            uvSizeArray.get(0).getAsInt(),
                            uvSizeArray.get(1).getAsInt()
                    );

                    textureBounds[face.ordinal()] = new FacedTextureBound(
                            uvBounds,
                            uvSize
                    );
                });

                cubes.add(new ModelCube(
                        origin,
                        size,
                        textureBounds
                ));
            });

            String parent = boneJson.has("parent") ?
                    boneJson.get("parent").getAsString()
                    :
                    "";

            boneParentMap.put(
                    new ModelBone(
                            name,
                            pivot,
                            cubes
                    ),
                    parent
            );
        });

        List<ModelBone> parentedBones = new ArrayList<>();
        boneParentMap.forEach((bone, parent) -> {
            if (parent.isEmpty()) {
                parentedBones.add(bone);
            } else {
                boneParentMap.keySet()
                        .stream()
                        .filter(mappedBone -> mappedBone.getName().equals(parent))
                        .findFirst()
                        .ifPresent(parentBone -> parentBone.getComponents().add(bone));
            }
        });

        return new ModelGeometry(description, parentedBones);
    }

}
