package team.unnamed.hephaestus.resourcepack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import team.unnamed.hephaestus.model.*;
import team.unnamed.hephaestus.model.texture.bound.FacedTextureBound;
import team.unnamed.hephaestus.model.texture.bound.TextureFace;
import team.unnamed.hephaestus.struct.Vector2Int;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.MoreMath;

import java.util.ArrayList;
import java.util.List;

import static team.unnamed.hephaestus.util.MoreMath.shrink;

public class ModelGeometryTransformer {

    public static final float DISPLAY_SCALE = 3.7333333F;
    public static final float DISPLAY_TRANSLATION_Y = -6.4f;

    private final String namespace;

    public ModelGeometryTransformer(String namespace) {
        this.namespace = namespace;
    }

    public List<ModelBone> getAllBones(ModelGeometry model) {
        List<ModelBone> bones = new ArrayList<>(model.getBones());
        model.getBones().forEach(bone -> bones.addAll(this.getBoneBones(bone)));
        return bones;
    }

    private List<ModelBone> getBoneBones(ModelBone bone) {
        List<ModelBone> bones = new ArrayList<>();
        for (ModelBone component : bone.getBones()) {
            bones.add(component);
            bones.addAll(getBoneBones(component));
        }
        return bones;
    }

    private JsonArray toJsonArray(float... vector) {
        JsonArray array = new JsonArray();
        for (float element : vector) {
            array.add(element);
        }
        return array;
    }

    private JsonArray toJsonArray(int... vector) {
        JsonArray array = new JsonArray();
        for (int element : vector) {
            array.add(element);
        }
        return array;
    }

    public JsonObject toJavaJson(Model model, ModelDescription description, ModelBone bone) {

        JsonArray elements = new JsonArray();
        Vector3Float bonePivot = bone.getPivot();
        float deltaX = bonePivot.getX() - 8F;
        float deltaY = bonePivot.getY() - 8F;
        float deltaZ = bonePivot.getZ() - 8F;

        int index = 0;

        for (ModelCube cube : bone.getCubes()) {

            Vector3Float origin = cube.getOrigin();
            Vector3Float cubePivot = cube.getPivot();
            Vector3Float size = cube.getSize();

            String axis = cube.getRotationAxis();

            float[] rotationOrigin;
            float angle = 0;
            switch (axis) {
                case "x":
                    angle = -cube.getRotation().getX();
                    break;
                case "y":
                    angle = -cube.getRotation().getY();
                    break;
                case "z":
                    angle = cube.getRotation().getZ();
                    break;
            }

            if (angle % 22.5D != 0.0D || angle > 45.0F || angle < -45.0F) {
                throw new IllegalArgumentException("Angle has to be 45 through -45 degrees in 22.5 degree increments");
            }

            if (cubePivot.getX() == 0 && cubePivot.getY() == 0 && cubePivot.getZ() == 0) {
                rotationOrigin = new float[] { 8, 8, 8 };
            } else {
                rotationOrigin = new float[]{
                        shrink(-cubePivot.getX() + bonePivot.getX() + 8),
                        shrink(cubePivot.getY() - bonePivot.getY() + 8),
                        shrink(cubePivot.getZ() - bonePivot.getZ() + 8)
                };
            }

            JsonObject rotation = new JsonObject();
            rotation.addProperty("axis", axis);
            rotation.addProperty("angle", angle);
            rotation.add("origin", toJsonArray(rotationOrigin));

            JsonObject faces = new JsonObject();
            FacedTextureBound[] bounds = cube.getTextureBounds();

            float ratio = 16.0F / description.getTextureWidth();
            for (TextureFace face : TextureFace.values()) {
                FacedTextureBound bound = bounds[face.ordinal()];
                float[] uv;

                if (bound == null) {
                    continue;
                }

                Vector2Int boundFrom = bound.getBounds();
                Vector2Int boundSize = bound.getSize();

                float sX = boundFrom.getX() * ratio;
                float sY = boundFrom.getY() * ratio;

                float eX = (boundFrom.getX() + boundSize.getX()) * ratio;
                float eY = (boundFrom.getY() + boundSize.getY()) * ratio;

                if (face != TextureFace.UP) {
                    if (face != TextureFace.DOWN) {
                        uv = new float[] {sX, sY, eX, eY};
                    } else {
                        uv = new float[] {sX, eY, eX, sY};
                    }
                } else {
                    uv = new float[] {eX, eY, sX, sY};
                }

                JsonObject javaFace = new JsonObject();
                javaFace.add("uv", toJsonArray(uv));
                javaFace.addProperty("texture", "#" + bound.getTextureId());
                javaFace.addProperty("tintindex", 0);

                faces.add(face.name().toLowerCase(), javaFace);
            }

            float[] from = new float[] {
                    16F - origin.getX() + deltaX - size.getX(),
                    origin.getY() - deltaY,
                    origin.getZ() - deltaZ
            };

            JsonObject cubeJson = new JsonObject();
            cubeJson.addProperty("name", bone.getName() + "-cube-" + (index++));
            cubeJson.add("from", toJsonArray(
                    MoreMath.shrink(from[0]),
                    MoreMath.shrink(from[1]),
                    MoreMath.shrink(from[2])
            ));
            cubeJson.add("to", toJsonArray(
                    MoreMath.shrink(from[0] + size.getX()),
                    MoreMath.shrink(from[1] + size.getY()),
                    MoreMath.shrink(from[2] + size.getZ())
            ));
            cubeJson.add("rotation", rotation);
            cubeJson.add("faces", faces);
            elements.add(cubeJson);
        }

        Vector3Float scaledPivot = bone.getPivot().divide(16);
        for (ModelBone child : bone.getBones()) {
            child.computeOffsets(scaledPivot);
        }

        JsonObject textures = new JsonObject();
        model.getGeometry().getTextureMap().forEach((id, name) ->
                textures.addProperty(id.toString(), namespace + ':' + model.getName() + "/" + name));

        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("file_name", bone.getName());
        modelJson.add("texture_size", toJsonArray(
                description.getTextureWidth(),
                description.getTextureHeight()
        ));
        modelJson.add("textures", textures);
        modelJson.add("elements", elements);

        float[] offset = { 0.0F, 0.0F, 0.0F };
        for (JsonElement cube : elements) {
            JsonObject cubeObject = cube.getAsJsonObject();
            JsonArray from = cubeObject.get("from").getAsJsonArray();
            JsonArray to = cubeObject.get("to").getAsJsonArray();
            for (int i = 0; i < 3; i++) {
                computeOffset(offset, i, from);
                computeOffset(offset, i, to);
            }
        }
        for (JsonElement cube : elements) {
            JsonObject cubeObject = cube.getAsJsonObject();
            JsonArray from = cubeObject.get("from").getAsJsonArray();
            JsonArray to = cubeObject.get("to").getAsJsonArray();

            addOffset(from, offset);
            addOffset(to, offset);

            JsonObject rotation = cubeObject.get("rotation").getAsJsonObject();
            JsonArray origin = rotation.get("origin").getAsJsonArray();

            origin.set(0, new JsonPrimitive(origin.get(0).getAsFloat() + offset[0]));
            origin.set(1, new JsonPrimitive(origin.get(1).getAsFloat() + offset[1]));
            origin.set(2, new JsonPrimitive(origin.get(2).getAsFloat() + offset[2]));
        }

        JsonObject displays = new JsonObject();

        JsonObject headDisplay = new JsonObject();
        float[] translation = new float[] { 0, DISPLAY_TRANSLATION_Y, 0 };

        if (offset[0] != 0F || offset[1] != 0F || offset[2] != 0F) {

            translation[0] = translation[0] - offset[0] * DISPLAY_SCALE;
            translation[1] = translation[1] - offset[1] * DISPLAY_SCALE;
            translation[2] = translation[2] - offset[2] * DISPLAY_SCALE;

            if (
                    Math.abs(translation[0]) > 80
                            || Math.abs(translation[1]) > 80
                            || Math.abs(translation[2]) > 80
            ) {
                throw new IllegalStateException("Translation value cannot be higher or lower than 80");
            }
        }

        headDisplay.add("translation", toJsonArray(translation));
        headDisplay.add("rotation", toJsonArray(0, 0, 0));
        headDisplay.add("scale", toJsonArray(DISPLAY_SCALE, DISPLAY_SCALE, DISPLAY_SCALE));

        displays.add("head", headDisplay);
        modelJson.add("display", displays);

        return modelJson;
    }

    private void addOffset(JsonArray array, float[] offset) {
        float x = array.get(0).getAsFloat() + offset[0];
        float y = array.get(1).getAsFloat() + offset[1];
        float z = array.get(2).getAsFloat() + offset[2];

        array.set(0, new JsonPrimitive(x));
        array.set(1, new JsonPrimitive(y));
        array.set(2, new JsonPrimitive(z));

        if (
                x > 32.0F || x < -16.0F
                        || y > 32.0F || y < -16.0F
                        || z > 32.0F || z < -16.0F
        ) {
            throw new IllegalStateException("Cube is not within 48x48x48 boundary");
        }
    }

    private void computeOffset(float[] offset, int i, JsonArray from) {
        float fromValue = from.get(i).getAsFloat();
        if (fromValue + offset[i] > 32.0F) {
            offset[i] -= fromValue + offset[i] - 32.0F;
        }
        if (fromValue + offset[i] < -16.0F) {
            offset[i] -= fromValue + offset[i] + 16.0F;
        }
    }

}
