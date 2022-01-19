package team.unnamed.hephaestus.resourcepack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import team.unnamed.hephaestus.ModelAsset;
import team.unnamed.hephaestus.ModelBoneAsset;
import team.unnamed.hephaestus.ModelCube;
import team.unnamed.hephaestus.ModelCubeRotation;
import team.unnamed.hephaestus.bound.FacedTextureBound;
import team.unnamed.hephaestus.bound.TextureFace;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.List;
import java.util.Locale;

public class ModelGeometryTransformer {

    /**
     * The size of a block for models, this is the number that
     * relates Minecraft blocks to our models
     */
    private static final float BLOCK_SIZE = 16F;
    private static final float HALF_BLOCK_SIZE = BLOCK_SIZE / 2F;

    private static final float SMALL_RATIO = BLOCK_SIZE / (BLOCK_SIZE + 9.6F);
    private static final float LARGE_RATIO = BLOCK_SIZE / (BLOCK_SIZE + 20.57F);

    private static final float SMALL_DISPLAY_SCALE = 3.8095F;
    private static final float LARGE_DISPLAY_SCALE = 3.7333333F;

    public static final float DISPLAY_TRANSLATION_Y = -6.4f;


    // from minecraft.fandom.com/wiki/Model#Item_models:
    // "Values must be between -16 and 32"
    private static final float CUBE_MIN_BOUND = -16F;
    private static final float CUBE_MAX_BOUND = 32F;

    private static final float MIN_TRANSLATION = -80F;
    private static final float MAX_TRANSLATION = 80F;

    private final String namespace;

    public ModelGeometryTransformer(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Converts a {@link ModelBoneAsset} (a representation of a model
     * bone) to a resource-pack ready {@link JsonObject} JSON object
     *
     * @param model The model holding the given bone
     * @param bone The bone to be converted
     * @return The JSON representation of the bone
     */
    public JsonObject toJavaJson(ModelAsset model, ModelBoneAsset bone) {

        JsonArray elements = new JsonArray();
        Vector3Float bonePivot = bone.getPivot();
        float deltaX = bonePivot.getX() - HALF_BLOCK_SIZE;
        float deltaY = bonePivot.getY() - HALF_BLOCK_SIZE;
        float deltaZ = bonePivot.getZ() - HALF_BLOCK_SIZE;

        List<ModelCube> cubes = bone.getCubes();

        for (int i = 0; i < cubes.size(); i++) {

            ModelCube cube = cubes.get(i);
            Vector3Float origin = cube.getOrigin();
            Vector3Float size = cube.getSize();
            ModelCubeRotation rotation = cube.getRotation();
            Vector3Float rotationOrigin = rotation.getOrigin();

            if (rotationOrigin.equals(Vector3Float.ZERO)) {
                // rotate at the center
                rotationOrigin = new Vector3Float(
                        HALF_BLOCK_SIZE,
                        HALF_BLOCK_SIZE,
                        HALF_BLOCK_SIZE
                );
            } else {
                rotationOrigin = new Vector3Float(
                        shrink(-rotationOrigin.getX() + bonePivot.getX() + HALF_BLOCK_SIZE),
                        shrink(rotationOrigin.getY() - bonePivot.getY() + HALF_BLOCK_SIZE),
                        shrink(rotationOrigin.getZ() - bonePivot.getZ() + HALF_BLOCK_SIZE)
                );
            }

            JsonObject rotationJson = new JsonObject();
            rotationJson.addProperty("axis", rotation.getAxis().name().toLowerCase(Locale.ROOT));
            rotationJson.addProperty("angle", rotation.getAngle());
            rotationJson.add("origin", toJsonArray(rotationOrigin));

            JsonObject faces = new JsonObject();
            FacedTextureBound[] bounds = cube.getTextureBounds();

            float widthRatio = BLOCK_SIZE / model.getTextureWidth();
            float heightRatio = BLOCK_SIZE / model.getTextureHeight();

            for (TextureFace face : TextureFace.values()) {
                FacedTextureBound bound = bounds[face.ordinal()];

                if (bound == null) {
                    continue;
                }

                float[] uv = new float[] {
                        bound.getBounds()[0] * widthRatio,
                        bound.getBounds()[1] * heightRatio,
                        bound.getBounds()[2] * widthRatio,
                        bound.getBounds()[3] * heightRatio
                };

                JsonObject javaFace = new JsonObject();
                javaFace.add("uv", toJsonArray(uv));
                if (bound.getTextureId() != -1) {
                    javaFace.addProperty("texture", "#" + bound.getTextureId());
                }
                javaFace.addProperty("tintindex", 0);

                faces.add(face.name().toLowerCase(), javaFace);
            }

            float[] from = new float[] {
                    BLOCK_SIZE - origin.getX() + deltaX - size.getX(),
                    origin.getY() - deltaY,
                    origin.getZ() - deltaZ
            };

            JsonObject cubeJson = new JsonObject();
            cubeJson.addProperty("name", bone.getName() + "-c-" + i);
            cubeJson.add("from", toJsonArray(
                    shrink(from[0]),
                    shrink(from[1]),
                    shrink(from[2])
            ));
            cubeJson.add("to", toJsonArray(
                    shrink(from[0] + size.getX()),
                    shrink(from[1] + size.getY()),
                    shrink(from[2] + size.getZ())
            ));
            cubeJson.add("rotation", rotationJson);
            cubeJson.add("faces", faces);
            elements.add(cubeJson);
        }

        JsonObject textures = new JsonObject();
        model.getTextureMapping().forEach((id, name) ->
                textures.addProperty(id.toString(), namespace + ':' + model.getName() + '/' + name));

        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("file_name", bone.getName());
        modelJson.add("texture_size", toJsonArray(
                model.getTextureWidth(),
                model.getTextureHeight()
        ));
        modelJson.add("textures", textures);
        modelJson.add("elements", elements);

        float[] offset = computeOffset(elements);

        JsonObject displays = new JsonObject();
        JsonObject headDisplay = new JsonObject();

        computeTranslation(headDisplay, offset);

        headDisplay.add("rotation", toJsonArray(0, 0, 0));
        headDisplay.add("scale", toJsonArray(SMALL_DISPLAY_SCALE, SMALL_DISPLAY_SCALE, SMALL_DISPLAY_SCALE));
        displays.add("head", headDisplay);
        modelJson.add("display", displays);

        return modelJson;
    }

    private static float[] computeOffset(JsonArray elements) {
        float[] offset = {0.0F, 0.0F, 0.0F};
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

            add(from, offset);
            add(to, offset);

            JsonObject rotation = cubeObject.get("rotation").getAsJsonObject();
            JsonArray origin = rotation.get("origin").getAsJsonArray();

            origin.set(0, new JsonPrimitive(origin.get(0).getAsFloat() + offset[0]));
            origin.set(1, new JsonPrimitive(origin.get(1).getAsFloat() + offset[1]));
            origin.set(2, new JsonPrimitive(origin.get(2).getAsFloat() + offset[2]));
        }
        return offset;
    }

    private static void computeOffset(float[] offset, int axis, JsonArray from) {
        float off = offset[axis];
        float value = from.get(axis).getAsFloat();
        if (value + off > CUBE_MAX_BOUND) {
            off -= value + off - CUBE_MAX_BOUND;
        }
        if (value + off < CUBE_MIN_BOUND) {
            off -= value + off - CUBE_MIN_BOUND;
        }
        offset[axis] = off;
    }

    private static void computeTranslation(
            JsonObject headDisplay,
            float[] offset
    ) {
        float translationX = -offset[0] * SMALL_DISPLAY_SCALE;
        float translationY = DISPLAY_TRANSLATION_Y - offset[1] * SMALL_DISPLAY_SCALE;
        float translationZ = -offset[2] * SMALL_DISPLAY_SCALE;

        if (
                translationX < MIN_TRANSLATION || translationX > MAX_TRANSLATION
                        || translationY < MIN_TRANSLATION || translationY > MAX_TRANSLATION
                        || translationZ < MIN_TRANSLATION || translationZ > MAX_TRANSLATION
        ) {
            throw new IllegalStateException("Translation out of bounds");
        }

        headDisplay.add("translation", toJsonArray(translationX, translationY, translationZ));
    }

    private static void add(JsonArray array, float[] offset) {
        float x = array.get(0).getAsFloat() + offset[0];
        float y = array.get(1).getAsFloat() + offset[1];
        float z = array.get(2).getAsFloat() + offset[2];

        array.set(0, new JsonPrimitive(x));
        array.set(1, new JsonPrimitive(y));
        array.set(2, new JsonPrimitive(z));

        if (
                x > CUBE_MAX_BOUND || x < CUBE_MIN_BOUND
                        || y > CUBE_MAX_BOUND || y < CUBE_MIN_BOUND
                        || z > CUBE_MAX_BOUND || z < CUBE_MIN_BOUND
        ) {
            throw new IllegalStateException("Cube out of bounds");
        }
    }

    private static JsonArray toJsonArray(float... vector) {
        JsonArray array = new JsonArray();
        for (float element : vector) {
            array.add(element);
        }
        return array;
    }

    private static JsonArray toJsonArray(Vector3Float vector) {
        JsonArray array = new JsonArray();
        array.add(vector.getX());
        array.add(vector.getY());
        array.add(vector.getZ());
        return array;
    }

    private static JsonArray toJsonArray(int... vector) {
        JsonArray array = new JsonArray();
        for (int element : vector) {
            array.add(element);
        }
        return array;
    }

    private static float shrink(float p) {
        return HALF_BLOCK_SIZE - (SMALL_RATIO * (HALF_BLOCK_SIZE - p));
    }

}
