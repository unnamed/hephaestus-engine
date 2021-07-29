package team.unnamed.hephaestus.reader.blockbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.ModelCube;
import team.unnamed.hephaestus.model.ModelDescription;
import team.unnamed.hephaestus.model.ModelGeometry;
import team.unnamed.hephaestus.model.texture.bound.FacedTextureBound;
import team.unnamed.hephaestus.model.texture.bound.TextureFace;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts JSON to {@link ModelGeometry}
 * <p>The Blockbench format is explicitly supported
 *  by the Blockbench model editor</p>
 */
public class BlockbenchModelGeometryReader {

    public ModelGeometry read(JsonObject json) throws IOException {

        JsonObject meta = json.get("meta").getAsJsonObject();
        JsonElement boxUv = meta.get("box_uv");

        if (
                boxUv == null
                        || boxUv.getAsBoolean()
        ) {
            throw new IOException("Box UV not supported, please turn it off");
        }

        JsonObject resolutionJson = json.getAsJsonObject("resolution");
        ModelDescription description = new ModelDescription(
                resolutionJson.get("width").getAsInt(),
                resolutionJson.get("height").getAsInt()
        );

        Map<Integer, String> textureMap = new HashMap<>();
        JsonArray textureArray = json.get("textures").getAsJsonArray();
        for (int i = 0; i < textureArray.size(); i++) {
            textureMap.put(i, textureArray.get(i).getAsJsonObject().get("name").getAsString().split("\\.")[0]);
        }

        Map<String, ModelCube> cubeIdMap = new HashMap<>();
        JsonArray cubesArray = json.getAsJsonArray("elements");
        for (JsonElement cubeElement : cubesArray) {
            JsonObject cubeJson = cubeElement.getAsJsonObject();

            Vector3Float pivot = Serialization.getVector3FloatFromJson(cubeJson.get("origin")).multiply(-1, 1, 1);
            Vector3Float to = Serialization.getVector3FloatFromJson(cubeJson.get("to"));
            Vector3Float from = Serialization.getVector3FloatFromJson(cubeJson.get("from"));

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
                    ? Serialization.getVector3FloatFromJson(rotationElement).multiply(-1, -1, 1)
                    : Vector3Float.ZERO;

            FacedTextureBound[] textureBounds = new FacedTextureBound[TextureFace.values().length];
            cubeJson.get("faces").getAsJsonObject().entrySet().forEach(faceEntry -> {
                TextureFace face = TextureFace.valueOf(faceEntry.getKey().toUpperCase());
                JsonObject faceJson = faceEntry.getValue().getAsJsonObject();

                JsonElement textureElement = faceJson.get("texture");
                int textureId = textureElement.isJsonNull() ? -1 : textureElement.getAsInt();

                JsonArray uvJson = faceJson.get("uv").getAsJsonArray();
                float[] bounds = {
                        uvJson.get(0).getAsFloat(),
                        uvJson.get(1).getAsFloat(),
                        uvJson.get(2).getAsFloat(),
                        uvJson.get(3).getAsFloat()
                };

                if (bounds[0] != 0 || bounds[1] != 0 || bounds[2] != 0 || bounds[3] != 0) {
                    textureBounds[face.ordinal()] = new FacedTextureBound(
                            bounds,
                            textureId
                    );
                }
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

        return new ModelGeometry(description, bones, textureMap);
    }

    private float round(float number) {
        return (float) (Math.round(number * 100.0) / 100.0);
    }

    private ModelBone createBone(Map<String, ModelCube> cubeIdMap, JsonObject json) {
        String name = json.get("name").getAsString();
        Vector3Float pivot = Serialization.getVector3FloatFromJson(json.get("origin")).multiply(-1, 1, 1);
        Vector3Float rotation = json.get("rotation") == null
                ? Vector3Float.ZERO
                : Serialization.getVector3FloatFromJson(json.get("rotation"));

        List<ModelCube> cubes = new ArrayList<>();
        List<ModelBone> bones = new ArrayList<>();

        json.get("children").getAsJsonArray().forEach(componentElement -> {
            if (componentElement.isJsonObject()) {
                bones.add(createBone(cubeIdMap, componentElement.getAsJsonObject()));
            } else {
                cubes.add(cubeIdMap.get(componentElement.getAsString()));
            }
        });

        return new ModelBone(name, pivot, rotation, bones, cubes);
    }
}