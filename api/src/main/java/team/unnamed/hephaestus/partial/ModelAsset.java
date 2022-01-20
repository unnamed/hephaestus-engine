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
package team.unnamed.hephaestus.partial;

import team.unnamed.creative.base.Writable;
import team.unnamed.hephaestus.animation.ModelAnimation;

import java.util.Collection;
import java.util.Map;

public class ModelAsset {

    private final String name;
    private final Map<String, Writable> textures;
    private final Map<Integer, String> textureMapping;
    private final Map<String, ModelAnimation> animations;
    private final Map<String, ModelBoneAsset> bones;

    public ModelAsset(
            String name,
            Map<String, Writable> textures,
            Map<Integer, String> textureMapping,
            Map<String, ModelBoneAsset> bones,
            Map<String, ModelAnimation> animations
    ) {
        this.name = name;
        this.textures = textures;
        this.textureMapping = textureMapping;
        this.bones = bones;
        this.animations = animations;
    }

    public String name() {
        return name;
    }

    public Map<String, Writable> textures() {
        return textures;
    }

    public Map<Integer, String> textureMapping() {
        return textureMapping;
    }

    public Map<String, ModelAnimation> animations() {
        return animations;
    }

    public Collection<ModelBoneAsset> bones() {
        return bones.values();
    }

}
