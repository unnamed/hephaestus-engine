/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
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
package team.unnamed.hephaestus.reader.blockbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import team.unnamed.creative.base.Axis3D;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.model.ElementFace;
import team.unnamed.creative.model.ElementRotation;
import team.unnamed.creative.texture.TextureUV;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.asset.BoneAsset;
import team.unnamed.hephaestus.asset.ElementAsset;
import team.unnamed.hephaestus.process.ElementScale;
import team.unnamed.hephaestus.reader.ModelFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class ElementReader {
    private static final TextureUV ZERO_UV = TextureUV.uv(0F, 0F, 0F, 0F);

    /**
     * Locally reads cubes from the "elements" property from
     * the given {@code json} object and then reads {@link Bone}
     * and {@link BoneAsset} from the "outliner" property
     */
    static void readElements(
            JsonObject json,
            BBModelData modelData
    ) {

        // Local map holding relations of cube identifier to
        // cube data, used to get bone cubes in constant time
        // when reading them
        Map<String, ElementAsset> cubeIdMap = new HashMap<>();

        for (JsonElement cubeElement : json.getAsJsonArray("elements")) {

            JsonObject cubeJson = cubeElement.getAsJsonObject();

            Vector3Float pivot = GsonUtil.getVector3FloatFromJson(cubeJson.get("origin"));
            Vector3Float to = GsonUtil.getVector3FloatFromJson(cubeJson.get("to"));
            Vector3Float from = GsonUtil.getVector3FloatFromJson(cubeJson.get("from"));

            Vector3Float rotation = GsonUtil.isNullOrAbsent(cubeJson, "rotation")
                    ? Vector3Float.ZERO
                    : GsonUtil.getVector3FloatFromJson(cubeJson.get("rotation"));

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

                int textureId = GsonUtil.isNullOrAbsent(faceJson, "texture")
                        ? -1
                        : faceJson.get("texture").getAsInt();

                JsonArray uvJson = faceJson.get("uv").getAsJsonArray();
                final TextureUV uv = TextureUV.uv(
                        uvJson.get(0).getAsFloat() / modelData.textureWidth,
                        uvJson.get(1).getAsFloat() / modelData.textureHeight,
                        uvJson.get(2).getAsFloat() / modelData.textureWidth,
                        uvJson.get(3).getAsFloat() / modelData.textureHeight
                );

                int faceRotation = faceJson.has("rotation") ? faceJson.get("rotation").getAsInt() : ElementFace.DEFAULT_ROTATION;

                if (!uv.equals(ZERO_UV)) {
                    faces.put(face, ElementFace.face()
                            .uv(uv)
                            .rotation(faceRotation)
                            .texture("#" + textureId)
                            .tintIndex(0)
                            .build());
                }
            }

            String uuid = cubeJson.get("uuid").getAsString();
            ElementAsset cube = new ElementAsset(
                    from,
                    to,
                    ElementRotation.of(pivot, axis, angle, ElementRotation.DEFAULT_RESCALE),
                    faces
            );
            cubeIdMap.put(uuid, cube);
        }

        // "outliner" field contains the root elements, like
        // bones and cubes, root cubes aren't supported yet
        for (JsonElement element : json.get("outliner").getAsJsonArray()) {
            if (element.isJsonObject()) {
                // if it's an object, then it represents a bone
                createBone(
                        modelData,
                        Vector3Float.ZERO,
                        cubeIdMap,
                        element.getAsJsonObject(),

                        modelData.bones,
                        modelData.boneAssets
                );
            }
            // TODO: Support elements without a bone
        }
    }

    /**
     * Creates a {@link Bone} and {@link Bone} from
     * the given {@code json} object
     *
     * @param parentAbsolutePosition The scaled pivot of the parent bone
     * @param cubeIdMap Map containing a relation of the cubes by
     *                  their identifiers for this model
     * @param json The json representation for this bone
     *
     * @param siblings The sibling bone map, the bone will be put
     *                 in this map by its name
     * @param siblingAssets The sibling bone asset map, the asset
     *                      will be put in this map by its name
     */
    private static void createBone(
            BBModelData modelData,
            Vector3Float parentAbsolutePosition,
            Map<String, ElementAsset> cubeIdMap,
            JsonObject json,

            Map<String, Bone> siblings,
            Map<String, BoneAsset> siblingAssets
    ) {

        String name = json.get("name").getAsString();
        BoneType boneType = BoneType.matchByBoneName(name);

        // The absolute position of this bone, in Blockbench units
        Vector3Float unitOrigin = GsonUtil.getVector3FloatFromJson(json.get("origin"));

        // The initial rotation of this bone
        Vector3Float rotation = GsonUtil.isNullOrAbsent(json, "rotation")
                ? Vector3Float.ZERO
                : GsonUtil.getVector3FloatFromJson(json.get("rotation"));

        // The position of this bone, in Minecraft units
        Vector3Float absolutePosition = unitOrigin.divide(-Blockbench.BLOCK_SIZE, Blockbench.BLOCK_SIZE, -Blockbench.BLOCK_SIZE);
        Vector3Float position = absolutePosition.subtract(parentAbsolutePosition);

        List<ElementAsset> cubes = new ArrayList<>();
        Map<String, Bone> children = new LinkedHashMap<>();
        Map<String, BoneAsset> childrenAssets = new LinkedHashMap<>();

        for (JsonElement childElement : json.get("children").getAsJsonArray()) {
            if (childElement.isJsonObject()) {
                // if it's an object, it's a sub-bone,
                // recursively read it
                createBone(
                        modelData,
                        absolutePosition,
                        cubeIdMap,
                        childElement.getAsJsonObject(),

                        children,
                        childrenAssets
                );
            } else if (childElement.isJsonPrimitive()
                    && childElement.getAsJsonPrimitive().isString()) {
                // if it's a string, it refers to a cube,
                // find it and add it to the cube map
                String cubeId = childElement.getAsString();
                ElementAsset cube = cubeIdMap.get(cubeId);

                if (cube == null) {
                    throw new ModelFormatException("Bone " + name + " contains " +
                            "an invalid cube id: '" + cubeId + "', not present in " +
                            "the 'elements' section");
                } else {
                    cubes.add(cube);
                }
            } else {
                throw new ModelFormatException("Invalid child type in bone " + name);
            }
        }

        if (boneType == BoneType.BOUNDING_BOX) {
            if (cubes.size() != 1) {
                throw new ModelFormatException("Bounding-box bone (" + name + ") has less or more than one cube");
            }

            ElementAsset boundingBoxCube = cubes.get(0);
            Vector3Float size = boundingBoxCube.to()
                    .subtract(boundingBoxCube.from())
                    .divide(Blockbench.BLOCK_SIZE);

            if (size.x() != size.z()) {
                throw new ModelFormatException("Bounding-box cube (bone: " + name
                        + ") has different 'X' (" + size.x() + ") and 'Z' ("
                        + size.z() + ") sizes");
            }

            modelData.boundingBox = new Vector2Float(size.x(), size.y());
            // skip other processing
            return;
        }

        ElementScale.Result processResult = ElementScale.process(unitOrigin, cubes);
        float scale = processResult.scale();
        float resourcePackScale = Math.min(4F, scale);
        float inGameScale = scale / resourcePackScale;

        BoneAsset asset = new BoneAsset(
                name,
                modelData.modelDataCursor.next(),
                processResult.elements(),
                childrenAssets,
                resourcePackScale
        );

        siblings.put(name, new Bone(name, position, rotation, children, asset.customModelData(), inGameScale));
        siblingAssets.put(name, asset);
    }

}
