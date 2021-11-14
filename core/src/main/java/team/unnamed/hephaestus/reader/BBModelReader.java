package team.unnamed.hephaestus.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.ModelAsset;
import team.unnamed.hephaestus.ModelBone;
import team.unnamed.hephaestus.ModelBoneAsset;
import team.unnamed.hephaestus.ModelCube;
import team.unnamed.hephaestus.ModelDataCursor;
import team.unnamed.hephaestus.animation.DynamicKeyFrameList;
import team.unnamed.hephaestus.animation.KeyFrameList;
import team.unnamed.hephaestus.animation.ModelAnimation;
import team.unnamed.hephaestus.bound.FacedTextureBound;
import team.unnamed.hephaestus.bound.TextureFace;
import team.unnamed.hephaestus.struct.Vector3Float;

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
            "3.6",
            "4.0"
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
        if (!isNullOrAbsent(meta, "format_version")
                        && !SUPPORTED_FORMATS.contains(meta.get("format_version").getAsString())) {
            throw new IOException("Provided JSON doesn't have a valid format version");
        }

        // check for box uv
        if (!isNullOrAbsent(meta, "box_uv") && meta.get("box_uv").getAsBoolean()) {
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

            // remove PNG extension
            if (name.endsWith(".png")) {
                name = name.substring(0, name.length() - ".png".length());
            }

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
    ) throws IOException {

        for (JsonElement animationElement : json.get("animations").getAsJsonArray()) {

            JsonObject animationJson = animationElement.getAsJsonObject();

            String name = animationJson.get("name").getAsString();
            boolean loop = animationJson.get("loop").getAsString().equals("loop");
            int length = Math.round(parseLenientFloat(animationJson.get("length")) * TICKS_PER_SECOND);

            if (isNullOrAbsent(animationJson, "animators")) {
                // empty animation, no keyframes of any kind
                animations.put(name, new ModelAnimation(name, loop, length, new HashMap<>(), new HashMap<>()));
                continue;
            }

            Map<String, KeyFrameList> animators = new HashMap<>();
            Map<String, Map<Integer, Integer>> modelData = new HashMap<>();

            for (Map.Entry<String, JsonElement> animatorEntry : animationJson.get("animators")
                    .getAsJsonObject()
                    .entrySet()) {

                JsonObject animatorJson = animatorEntry.getValue().getAsJsonObject();
                String boneName = animatorJson.get("name").getAsString();

                KeyFrameList frames = new DynamicKeyFrameList();

                for (JsonElement keyFrameElement : animatorJson.get("keyframes").getAsJsonArray()) {

                    JsonObject keyframeJson = keyFrameElement.getAsJsonObject();
                    JsonObject dataPoint = keyframeJson.get("data_points").getAsJsonArray().get(0).getAsJsonObject();

                    float x = parseLenientFloat(dataPoint.get("x"));
                    float y = parseLenientFloat(dataPoint.get("y"));
                    float z = parseLenientFloat(dataPoint.get("z"));

                    Vector3Float value = new Vector3Float(x, y, z);

                    String channel = keyframeJson.get("channel").getAsString();
                    int time = Math.round(parseLenientFloat(keyframeJson.get("time")) * TICKS_PER_SECOND);

                    if (channel.equals("scale")) {
                        // TODO: support scale frames
                        throw new IOException("Scale frames aren't supported yet." +
                                " Check animation " + name + " and bone " + boneName);
                    }

                    if (channel.equals("position")) {
                        value = value.divide(16, 16, -16);
                    }

                    frames.put(time, KeyFrameList.Channel.valueOf(channel.toUpperCase()), value);
                }

                animators.put(boneName, frames);
            }

            ModelAnimation animation = new ModelAnimation(name, loop, length, animators, modelData);
            animations.put(name, animation);
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

            Vector3Float rotation = isNullOrAbsent(cubeJson, "rotation")
                    ? Vector3Float.ZERO
                    : getVector3FloatFromJson(cubeJson.get("rotation")).multiply(-1, -1, 1);

            FacedTextureBound[] textureBounds = new FacedTextureBound[TextureFace.values().length];

            for (Map.Entry<String, JsonElement> faceEntry
                    : cubeJson.getAsJsonObject("faces").entrySet()) {

                TextureFace face = TextureFace.valueOf(faceEntry.getKey().toUpperCase());
                JsonObject faceJson = faceEntry.getValue().getAsJsonObject();

                int textureId = isNullOrAbsent(faceJson, "texture")
                        ? -1
                        : faceJson.get("texture").getAsInt();

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
     * Determines if a property with the given {@code name}
     * exists in the specified {@code object} and it's
     * not null
     */
    private boolean isNullOrAbsent(JsonObject object, String name) {
        return !object.has(name) || object.get(name).isJsonNull();
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
        Vector3Float rotation = isNullOrAbsent(json, "rotation")
                ? Vector3Float.ZERO
                : getVector3FloatFromJson(json.get("rotation"));

        // scaled pivot of the bone (pivot / 16)
        Vector3Float scaledPivot = pivot.divide(16, 16, -16);
        Vector3Float offset = scaledPivot.subtract(parentScaledPivot);

        List<ModelCube> cubes = new ArrayList<>();
        Map<String, ModelBone> bones = new LinkedHashMap<>();
        Map<String, ModelBoneAsset> boneAssets = new LinkedHashMap<>();

        // instantiate bone and asset with empty and mutable
        // cubes, bones and assets, they'll be filled later
        ModelBoneAsset asset = new ModelBoneAsset(name, pivot, cursor.next(), cubes, boneAssets);
        ModelBone bone = new ModelBone(parent, name, rotation, bones, offset, asset.getCustomModelData());

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

    /**
     * Checks if the given {@code element} is a
     * string, if yes, it replaces the commas (,)
     * by dots and invokes {@link Float#parseFloat}
     * to parse the float, if not, it just calls
     * {@link JsonElement#getAsFloat()}
     */
    private static float parseLenientFloat(JsonElement element) {
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
    private static Vector3Float getVector3FloatFromJson(JsonElement element) {
        JsonArray array = element.getAsJsonArray();
        return new Vector3Float(
                array.get(0).getAsFloat(),
                array.get(1).getAsFloat(),
                array.get(2).getAsFloat()
        );
    }

}