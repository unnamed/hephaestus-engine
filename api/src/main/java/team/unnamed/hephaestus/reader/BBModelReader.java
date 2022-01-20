/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.hephaestus.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Axis3D;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.base.Vector4Float;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.model.Element;
import team.unnamed.creative.model.ElementFace;
import team.unnamed.creative.model.ElementRotation;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.partial.ModelAsset;
import team.unnamed.hephaestus.ModelBone;
import team.unnamed.hephaestus.partial.ModelBoneAsset;
import team.unnamed.hephaestus.ModelDataCursor;
import team.unnamed.hephaestus.animation.DynamicKeyFrameList;
import team.unnamed.hephaestus.animation.KeyFrameList;
import team.unnamed.hephaestus.animation.ModelAnimation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
    public Model read(InputStream input) throws IOException {

        Reader reader = new InputStreamReader(input);
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

        JsonObject resolution = json.getAsJsonObject("resolution");
        int width = resolution.get("width").getAsInt();
        int height = resolution.get("height").getAsInt();

        Map<String, ModelBone> bones = new LinkedHashMap<>();
        Map<String, ModelBoneAsset> boneAssets = new LinkedHashMap<>();
        Map<String, ModelAnimation> animations = new LinkedHashMap<>();
        Map<String, Writable> textures = new HashMap<>();
        Map<Integer, String> textureMapping = new HashMap<>();

        readTextures(json, textures, textureMapping);
        readElements(json, bones, boneAssets, width, height);
        readAnimations(json, animations);

        return new Model(
                modelName,
                bones,
                new ModelAsset(
                        modelName,
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
            Map<String, Writable> textures,
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
            textures.put(name, Writable.bytes(Base64.getDecoder().decode(base64Source)));
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

        if (!json.has("animations")) {
            // Model doesn't have animations
            return;
        }

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
            Map<String, ModelBoneAsset> boneAssets,
            int textureWidth,
            int textureHeight
    ) throws IOException {

        float widthRatio = 16F / textureWidth;
        float heightRatio = 16F / textureHeight;

        // Local map holding relations of cube identifier to
        // cube data, used to get bone cubes in constant time
        // when reading them
        Map<String, Element> cubeIdMap = new HashMap<>();

        for (JsonElement cubeElement : json.getAsJsonArray("elements")) {

            JsonObject cubeJson = cubeElement.getAsJsonObject();

            Vector3Float pivot = getVector3FloatFromJson(cubeJson.get("origin"))
                    .multiply(-1, 1, 1);
            Vector3Float to = getVector3FloatFromJson(cubeJson.get("to"));
            Vector3Float from = getVector3FloatFromJson(cubeJson.get("from"));

            Vector3Float origin = new Vector3Float(-to.x(), from.y(), from.z());

            Vector3Float rotation = isNullOrAbsent(cubeJson, "rotation")
                    ? Vector3Float.ZERO
                    : getVector3FloatFromJson(cubeJson.get("rotation")).multiply(-1, -1, 1);

            float x = rotation.x();
            float y = rotation.y();
            float z = rotation.z();

            // determine axis and check that it is rotated to a single direction
            Axis3D axis;
            float angle;

            if ((((x != 0) ? 1 : 0) ^ ((y != 0) ? 1 : 0) ^ ((z != 0) ? 1 : 0)) == 0 && (x != 0 || y != 0)) {
                throw new UnsupportedOperationException("Cube can't be rotated in multiple axis");
            } else if (x != 0) {
                axis = Axis3D.X;
                angle = x;
            } else if (y != 0) {
                axis = Axis3D.Y;
                angle = y;
            } else {
                axis = Axis3D.Z;
                angle = z;
            }

            Map<CubeFace, ElementFace> faces = new HashMap<>();

            for (Map.Entry<String, JsonElement> faceEntry
                    : cubeJson.getAsJsonObject("faces").entrySet()) {

                CubeFace face = CubeFace.valueOf(faceEntry.getKey().toUpperCase(Locale.ROOT));
                JsonObject faceJson = faceEntry.getValue().getAsJsonObject();

                int textureId = isNullOrAbsent(faceJson, "texture")
                        ? -1
                        : faceJson.get("texture").getAsInt();

                JsonArray uvJson = faceJson.get("uv").getAsJsonArray();
                Vector4Float uv = new Vector4Float(
                        uvJson.get(0).getAsFloat() * widthRatio,
                        uvJson.get(1).getAsFloat() * heightRatio,
                        uvJson.get(2).getAsFloat() * widthRatio,
                        uvJson.get(3).getAsFloat() * heightRatio
                );

                if (!uv.equals(Vector4Float.ZERO)) {
                    faces.put(face, ElementFace.builder()
                            .uv(uv)
                            .texture("#" + textureId)
                            .build());
                }
            }

            String uuid = cubeJson.get("uuid").getAsString();
            Element cube = Element.builder()
                    .from(origin)
                    .to(to)
                    .rotation(ElementRotation.of(pivot, axis, angle, ElementRotation.DEFAULT_RESCALE))
                    .faces(faces)
                    .build();

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
            Map<String, Element> cubeIdMap,
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

        List<Element> cubes = new ArrayList<>();
        Map<String, ModelBone> bones = new LinkedHashMap<>();
        Map<String, ModelBoneAsset> boneAssets = new LinkedHashMap<>();

        // instantiate bone and asset with empty and mutable
        // cubes, bones and assets, they'll be filled later
        ModelBoneAsset asset = new ModelBoneAsset(name, pivot, cursor.next(), cubes, boneAssets);
        ModelBone bone = new ModelBone(parent, name, rotation, bones, offset, true, asset.getCustomModelData());

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
                Element cube = cubeIdMap.get(cubeId);

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