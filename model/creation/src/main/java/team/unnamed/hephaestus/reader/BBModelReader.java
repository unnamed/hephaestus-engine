package team.unnamed.hephaestus.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelAsset;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.ModelBoneAsset;
import team.unnamed.hephaestus.model.ModelCube;
import team.unnamed.hephaestus.model.ModelDataCursor;
import team.unnamed.hephaestus.model.animation.KeyFrame;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelBoneAnimation;
import team.unnamed.hephaestus.model.texture.bound.FacedTextureBound;
import team.unnamed.hephaestus.model.texture.bound.TextureFace;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static team.unnamed.hephaestus.util.Serialization.getVector3FloatFromJson;
import static team.unnamed.hephaestus.util.Serialization.parseLenientFloat;

/**
 * Implementation of {@link ModelReader} to create
 * {@link Model} instances from <a href="https://blockbench.net">
 * Blockbench</a>'s <b>.bbmodel</b> files
 */
public class BBModelReader implements ModelReader {

    private static final String BASE_64_PREFIX = "data:image/png;base64,";
    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final int TICKS_PER_SECOND = 20;

    /**
     * List containing supported block-bench format
     * versions
     */
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "3.6"
    );

    private final ModelDataCursor cursor;

    public BBModelReader(ModelDataCursor cursor) {
        this.cursor = cursor;
    }

    public BBModelReader() {
        this(new ModelDataCursor(1));
    }

    @Override
    public Model read(Reader reader) throws IOException {

        JsonObject json = JSON_PARSER.parse(reader).getAsJsonObject();
        JsonObject meta = json.get("meta").getAsJsonObject();

        // TODO: we can take the "meta.creation_time" date for generating resource pack
        String modelName = json.get("name").getAsString();

        // check for bbmodel format version
        if (meta.has("format_version")
                        && !SUPPORTED_FORMATS.contains(meta.get("format_version").getAsString())) {
            throw new IOException("Provided JSON doesn't have a valid format version");
        }

        // check for box uv
        if (meta.has("box_uv") && meta.get("box_uv").getAsBoolean()) {
            throw new IOException("Box UV not supported, please turn it off");
        }

        Map<String, ModelBone> bones = new LinkedHashMap<>();
        Map<String, ModelBoneAsset> boneAssets = new LinkedHashMap<>();
        Map<String, ModelAnimation> animations = new LinkedHashMap<>();
        Map<String, Streamable> textures = new HashMap<>();
        Map<Integer, String> textureMapping = new HashMap<>();

        readTextures(json, textures, textureMapping);
        readElements(json, bones, boneAssets);
        readAnimations(json, animations);

        JsonObject resolution = json.getAsJsonObject("resolution");

        return new Model(
                modelName,
                bones,
                new ModelAsset(
                        modelName,
                        resolution.get("width").getAsInt(),
                        resolution.get("height").getAsInt(),
                        textures,
                        textureMapping,
                        boneAssets,
                        animations
                )
        );
    }

    /**
     * Reads the textures from the given {@code json} object
     * and puts the data into the given {@code textures} and
     * their mappings to the given {@code textureMappings}
     */
    private void readTextures(
            JsonObject json,
            Map<String, Streamable> textures,
            Map<Integer, String> textureMappings
    ) throws IOException {

        JsonArray texturesJson = json.get("textures").getAsJsonArray();

        for (int index = 0; index < texturesJson.size(); index++) {

            JsonObject textureJson = texturesJson.get(index).getAsJsonObject();
            String name = textureJson.get("name").getAsString();
            String source = textureJson.get("source").getAsString();

            if (!(source.startsWith(BASE_64_PREFIX))) {
                throw new IOException("Model doesn't contains a valid" +
                        " texture source. Not Base64");
            }

            String base64Source = source.substring(BASE_64_PREFIX.length());

            // map to index
            textureMappings.put(index, name);
            textures.put(name, new Streamable() {
                @Override
                public InputStream openIn() {
                    return Streams.fromBase64(base64Source, StandardCharsets.UTF_8);
                }
            });
        }
    }

    /**
     * Reads {@link ModelAnimation} from the given {@code json}
     * object and puts them into the specified {@code animations}
     * map
     */
    private void readAnimations(
            JsonObject json,
            Map<String, ModelAnimation> animations
    ) {
        for (JsonElement animationElement : json.get("animations").getAsJsonArray()) {

            JsonObject animationJson = animationElement.getAsJsonObject();

            String name = animationJson.get("name").getAsString();
            boolean loop = animationJson.get("loop").getAsBoolean();
            int length = Math.round(parseLenientFloat(animationJson.get("length")) * TICKS_PER_SECOND);

            if (!animationJson.has("animators")) {
                // empty animation, no keyframes of any kind
                animations.put(name, new ModelAnimation(name, loop, length, new HashMap<>(), new HashMap<>()));
                continue;
            }

            Map<String, ModelBoneAnimation> boneAnimations = new HashMap<>();
            Map<String, Map<Integer, Integer>> modelData = new HashMap<>();

            for (Map.Entry<String, JsonElement> animatorEntry : animationJson.get("animators")
                    .getAsJsonObject()
                    .entrySet()) {

                JsonObject animatorJson = animatorEntry.getValue().getAsJsonObject();
                String boneName = animatorJson.get("name").getAsString();

                List<KeyFrame> rotationFrames = new ArrayList<>();
                List<KeyFrame> positionFrames = new ArrayList<>();
                List<KeyFrame> sizeFrames = new ArrayList<>();

                KeyFrame lastSizeFrame = null;

                for (JsonElement keyFrameElement : animatorJson.get("keyframes").getAsJsonArray()) {

                    JsonObject keyframeJson = keyFrameElement.getAsJsonObject();
                    JsonObject dataPoint = keyframeJson.get("data_points").getAsJsonArray().get(0).getAsJsonObject();

                    float x = parseLenientFloat(dataPoint.get("x"));
                    float y = parseLenientFloat(dataPoint.get("y"));
                    float z = parseLenientFloat(dataPoint.get("z"));

                    Vector3Float value = new Vector3Float(x, y, z);

                    String channel = keyframeJson.get("channel").getAsString();
                    int time = Math.round(parseLenientFloat(keyframeJson.get("time")) * TICKS_PER_SECOND);
                    KeyFrame keyFrame = new KeyFrame(
                            time,
                            value
                    );

                    switch (channel) {
                        case "scale": {
                            // scale require a special treatment
                            Map<Integer, Integer> boneData = modelData.computeIfAbsent(boneName, k -> new HashMap<>());

                            if (lastSizeFrame != null) {
                                int previous = lastSizeFrame.getPosition();
                                int current = keyFrame.getPosition();

                                Vector3Float lerpPrevious = null;
                                for (int i = previous + 1; i < current; i++) {
                                    float ratio = (float) (i - previous) / (float) (current - previous);
                                    Vector3Float size = Vectors.lerp(lastSizeFrame.getValue(), keyFrame.getValue(), ratio);

                                    if (size.equals(lerpPrevious)) {
                                        continue;
                                    }

                                    lerpPrevious = size;
                                    sizeFrames.add(new KeyFrame(i, size));
                                    boneData.put(i, cursor.next());
                                }
                            } else {
                                boneData.put(keyFrame.getPosition(), cursor.next());
                                sizeFrames.add(keyFrame);
                            }
                            lastSizeFrame = keyFrame;
                            break;
                        }
                        case "rotation": {
                            rotationFrames.add(keyFrame);
                            break;
                        }
                        case "position": {
                            positionFrames.add(keyFrame);
                            break;
                        }
                    }
                }

                boneAnimations.put(boneName, new ModelBoneAnimation(positionFrames, rotationFrames, sizeFrames));
            }

            animations.put(name, new ModelAnimation(name, loop, length, boneAnimations, modelData));
        }
    }

    /**
     * Locally reads cubes from the "elements" property from
     * the given {@code json} object and then reads {@link ModelBone}
     * and {@link ModelBoneAsset} from the "outliner" property
     */
    private void readElements(
            JsonObject json,
            Map<String, ModelBone> bones,
            Map<String, ModelBoneAsset> boneAssets
    ) throws IOException {

        // Local map holding relations of cube identifier to
        // cube data, used to get bone cubes in constant time
        // when reading them
        Map<String, ModelCube> cubeIdMap = new HashMap<>();

        for (JsonElement cubeElement : json.getAsJsonArray("elements")) {

            JsonObject cubeJson = cubeElement.getAsJsonObject();

            Vector3Float pivot = getVector3FloatFromJson(cubeJson.get("origin"))
                    .multiply(-1, 1, 1);
            Vector3Float to = getVector3FloatFromJson(cubeJson.get("to"));
            Vector3Float from = getVector3FloatFromJson(cubeJson.get("from"));

            Vector3Float origin = new Vector3Float(-to.getX(), from.getY(), from.getZ());
            Vector3Float size = new Vector3Float(
                    round(to.getX() - from.getX()),
                    round(to.getY() - from.getY()),
                    round(to.getZ() - from.getZ())
            );

            Vector3Float rotation = cubeJson.has("rotation")
                    ? getVector3FloatFromJson(cubeJson.get("rotation")).multiply(-1, -1, 1)
                    : Vector3Float.ZERO;

            FacedTextureBound[] textureBounds = new FacedTextureBound[TextureFace.values().length];

            for (Map.Entry<String, JsonElement> faceEntry
                    : cubeJson.getAsJsonObject("faces").entrySet()) {

                TextureFace face = TextureFace.valueOf(faceEntry.getKey().toUpperCase());
                JsonObject faceJson = faceEntry.getValue().getAsJsonObject();

                int textureId = faceJson.has("texture")
                        ? faceJson.get("texture").getAsInt()
                        : -1;

                JsonArray uvJson = faceJson.get("uv").getAsJsonArray();
                float[] bounds = {
                        uvJson.get(0).getAsFloat(),
                        uvJson.get(1).getAsFloat(),
                        uvJson.get(2).getAsFloat(),
                        uvJson.get(3).getAsFloat()
                };

                if (bounds[0] != 0 || bounds[1] != 0 || bounds[2] != 0 || bounds[3] != 0) {
                    textureBounds[face.ordinal()] = new FacedTextureBound(bounds, textureId);
                }
            }

            String uuid = cubeJson.get("uuid").getAsString();
            ModelCube cube = new ModelCube(origin, pivot, rotation, size, textureBounds);

            cubeIdMap.put(uuid, cube);
        }

        // "outliner" field contains the root elements, like
        // bones and cubes, root cubes aren't supported yet
        for (JsonElement element : json.get("outliner").getAsJsonArray()) {
            if (element.isJsonObject()) {
                // if it's an object, then it represents a bone
                createBone(
                        Vector3Float.ZERO,
                        null,
                        cubeIdMap,
                        element.getAsJsonObject(),

                        bones,
                        boneAssets
                );
            }
            // TODO: Support elements without a bone
        }
    }

    /**
     * Rounds the given {@code number} up to
     * two decimals, <a href="https://stackoverflow.com/questions/11701399">
     * see this question</a>
     */
    private float round(float number) {
        return (float) (Math.round(number * 100D) / 100D);
    }

    /**
     * Creates a {@link ModelBone} and {@link ModelBone} from
     * the given {@code json} object
     *
     * @param parentScaledPivot The scaled pivot of the parent bone
     * @param parent The parent bone, null if it's a root bone
     * @param cubeIdMap Map containing a relation of the cubes by
     *                  their identifiers for this model
     * @param json The json representation for this bone
     *
     * @param siblings The sibling bone map, the bone will be put
     *                 in this map by its name
     * @param siblingAssets The sibling bone asset map, the asset
     *                      will be put in this map by its name
     */
    private void createBone(
            Vector3Float parentScaledPivot,
            @Nullable ModelBone parent,
            Map<String, ModelCube> cubeIdMap,
            JsonObject json,

            Map<String, ModelBone> siblings,
            Map<String, ModelBoneAsset> siblingAssets
    ) throws IOException {

        String name = json.get("name").getAsString();

        // The pivot of this bone
        Vector3Float pivot = getVector3FloatFromJson(json.get("origin"))
                .multiply(-1, 1, 1);

        // The initial rotation of this bone
        Vector3Float rotation = json.has("rotation")
                ? getVector3FloatFromJson(json.get("rotation"))
                : Vector3Float.ZERO;

        // scaled pivot of the bone (pivot / 16)
        Vector3Float scaledPivot = pivot.divide(16);
        Vector3Float offset = scaledPivot.subtract(parentScaledPivot);

        List<ModelCube> cubes = new ArrayList<>();
        Map<String, ModelBone> bones = new LinkedHashMap<>();
        Map<String, ModelBoneAsset> boneAssets = new LinkedHashMap<>();

        // instantiate bone and asset with empty and mutable
        // cubes, bones and assets, they'll be filled later
        ModelBoneAsset asset = new ModelBoneAsset(name, pivot, cursor.next(), cubes, boneAssets);
        ModelBone bone = new ModelBone(parent, name, rotation, bones, offset, asset);

        for (JsonElement childElement : json.get("children").getAsJsonArray()) {
            if (childElement.isJsonObject()) {
                // if it's an object, it's a sub-bone,
                // recursively read it
                createBone(
                        scaledPivot,
                        bone,
                        cubeIdMap,
                        childElement.getAsJsonObject(),

                        bones,
                        boneAssets
                );
            } else if (childElement.isJsonPrimitive()
                    && childElement.getAsJsonPrimitive().isString()) {
                // if it's a string, it refers to a cube,
                // find it and add it to the cube map
                String cubeId = childElement.getAsString();
                ModelCube cube = cubeIdMap.get(cubeId);

                if (cube == null) {
                    throw new IOException("Bone " + name + " contains " +
                            "an invalid cube id: '" + cubeId + "', not present in " +
                            "the 'elements' section");
                } else {
                    cubes.add(cube);
                }
            } else {
                throw new IOException("Invalid child type in bone " + name);
            }
        }

        siblings.put(bone.getName(), bone);
        siblingAssets.put(asset.getName(), asset);
    }

}