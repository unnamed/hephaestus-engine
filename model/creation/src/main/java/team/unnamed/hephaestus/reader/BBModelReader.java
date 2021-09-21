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
import team.unnamed.hephaestus.util.Serialization;
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

/**
 * Implementation of {@link ModelReader} to create
 * {@link Model} instances from <a href="https://blockbench.net">
 * Blockbench</a>'s <b>.bbmodel</b> files
 */
public class BBModelReader implements ModelReader {

    private static final String BASE_64_PREFIX = "data:image/png;base64,";
    private static final JsonParser JSON_PARSER = new JsonParser();

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
        JsonObject resolutionJson = json.getAsJsonObject("resolution");

        // TODO: we can take the "meta.creation_time" date for generating resource pack
        String modelName = json.get("name").getAsString();

        if (meta.has("format_version")
                        && !SUPPORTED_FORMATS.contains(meta.get("format_version").getAsString())) {
            throw new IOException("Provided JSON doesn't have a valid format version");
        }

        if (meta.has("box_uv") && meta.get("box_uv").getAsBoolean()) {
            throw new IOException("Box UV not supported, please turn it off");
        }

        Map<String, ModelBone> bones = new LinkedHashMap<>();
        Map<String, ModelBoneAsset> boneAssets = new LinkedHashMap<>();
        Map<String, ModelAnimation> animations = readAnimations(json);

        JsonArray texturesJson = json.get("textures").getAsJsonArray();

        Map<String, Streamable> textures = new HashMap<>();
        Map<Integer, String> textureMapping = new HashMap<>();

        for (int index = 0; index < texturesJson.size(); index++) {

            JsonElement textureElement = texturesJson.get(index);
            JsonObject textureJson = textureElement.getAsJsonObject();

            String name = textureJson.get("name").getAsString();
            String source = textureJson.get("source").getAsString();

            if (!(source.startsWith(BASE_64_PREFIX))) {
                throw new IOException(
                        "Model '" + modelName + "' contains an invalid "
                                + "texture source. Not Base64"
                );
            }

            String base64Source = source.substring(BASE_64_PREFIX.length());
            textureMapping.put(index, name);
            textures.put(name, new Streamable() {
                @Override
                public InputStream openIn() {
                    return Streams.fromBase64(base64Source, StandardCharsets.UTF_8);
                }
            });
        }

        readElements(json, bones, boneAssets);

        return new Model(
                modelName,
                bones,
                new ModelAsset(
                        modelName,
                        resolutionJson.get("width").getAsInt(),
                        resolutionJson.get("height").getAsInt(),
                        textures,
                        textureMapping,
                        boneAssets,
                        animations
                )
        );
    }

    private Map<String, ModelAnimation> readAnimations(JsonObject json) {

        Map<String, ModelAnimation> animations = new HashMap<>();
        JsonElement animationsElement = json.get("animations");

        if (animationsElement == null) {
            return animations;
        }

        for (JsonElement animationElement : animationsElement.getAsJsonArray()) {
            JsonObject animationJson = animationElement.getAsJsonObject();

            String name = animationJson.get("name").getAsString();
            boolean loop = animationJson.get("loop").getAsString().equals("loop");
            int length = Math.round(animationJson.get("length").getAsFloat()*20);

            if (!animationJson.has("animators")) {
                animations.put(name, new ModelAnimation(name, loop, length, new HashMap<>(), new HashMap<>()));
                continue;
            }

            Map<String, ModelBoneAnimation> boneAnimations = new HashMap<>();
            Map<String, Map<Integer, Integer>> modelData = new HashMap<>();

            for (Map.Entry<String, JsonElement> boneAnimationEntry : animationJson.get("animators")
                    .getAsJsonObject()
                    .entrySet()) {

                JsonObject boneAnimationJson = boneAnimationEntry.getValue().getAsJsonObject();
                String boneName = boneAnimationJson.get("name").getAsString();

                List<KeyFrame> rotationFrames = new ArrayList<>();
                List<KeyFrame> positionFrames = new ArrayList<>();
                List<KeyFrame> sizeFrames = new ArrayList<>();

                KeyFrame lastSizeFrame = null;

                for (JsonElement keyFrameElement : boneAnimationJson.get("keyframes").getAsJsonArray()) {
                    JsonObject keyframeJson = keyFrameElement.getAsJsonObject();
                    JsonObject dataPoint = keyframeJson.get("data_points").getAsJsonArray().get(0).getAsJsonObject();

                    float x = Serialization.parseLenientFloat(dataPoint.get("x"));
                    float y = Serialization.parseLenientFloat(dataPoint.get("y"));
                    float z = Serialization.parseLenientFloat(dataPoint.get("z"));

                    Vector3Float value = new Vector3Float(x, y, z);

                    String channel = keyframeJson.get("channel").getAsString();
                    int time = Math.round(keyframeJson.get("time").getAsFloat() * 20);
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

        return animations;
    }

    private void readElements(
            JsonObject json,
            Map<String, ModelBone> bones,
            Map<String, ModelBoneAsset> boneAssets
    ) {

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
                int textureId = (textureElement == null || textureElement.isJsonNull())
                        ? -1
                        : textureElement.getAsInt();

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

    private float round(float number) {
        return (float) (Math.round(number * 100.0) / 100.0);
    }

    private void createBone(
            Vector3Float parentScaledPivot,
            @Nullable ModelBone parent,
            Map<String, ModelCube> cubeIdMap,
            JsonObject json,

            Map<String, ModelBone> siblings,
            Map<String, ModelBoneAsset> siblingAssets
    ) {
        String name = json.get("name").getAsString();
        Vector3Float pivot = Serialization.getVector3FloatFromJson(json.get("origin")).multiply(-1, 1, 1);
        Vector3Float rotation = json.get("rotation") == null
                ? Vector3Float.ZERO
                : Serialization.getVector3FloatFromJson(json.get("rotation"));

        Vector3Float scaledPivot = pivot.divide(16);
        Vector3Float offset = scaledPivot.subtract(parentScaledPivot);

        List<ModelCube> cubes = new ArrayList<>();
        Map<String, ModelBone> bones = new LinkedHashMap<>();
        Map<String, ModelBoneAsset> boneAssets = new LinkedHashMap<>();

        ModelBoneAsset asset = new ModelBoneAsset(
                name,
                pivot,
                cursor.next(),
                cubes,
                boneAssets
        );

        ModelBone bone = new ModelBone(
                parent,
                name,
                rotation,
                bones,
                offset,
                asset
        );

        for (JsonElement childElement : json.get("children").getAsJsonArray()) {
            if (childElement.isJsonObject()) {
                JsonObject childBoneObject = childElement.getAsJsonObject();
                createBone(
                        parentScaledPivot,
                        parent,
                        cubeIdMap,
                        childBoneObject,

                        bones,
                        boneAssets
                );
            } else {
                String cubeId = childElement.getAsString();
                cubes.add(cubeIdMap.get(cubeId));
            }
        }

        siblings.put(bone.getName(), bone);
        siblingAssets.put(asset.getName(), asset);
    }

}