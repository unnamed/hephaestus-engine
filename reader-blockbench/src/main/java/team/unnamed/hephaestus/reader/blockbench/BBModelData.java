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

import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Writable;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.ModelDataCursor;
import team.unnamed.hephaestus.partial.BoneAsset;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class BBModelData {

    // todo: !!!!!! this is ugly, fix it
    String name;
    Vector2Float boundingBox = new Vector2Float(1, 1);

    ModelDataCursor modelDataCursor;
    final Map<String, Bone> bones = new LinkedHashMap<>();
    final Map<String, Writable> textures = new HashMap<>();
    final Map<Integer, String> textureMapping = new HashMap<>();
    final Map<String, BoneAsset> boneAssets = new LinkedHashMap<>();

    int textureWidth;
    int textureHeight;

}
