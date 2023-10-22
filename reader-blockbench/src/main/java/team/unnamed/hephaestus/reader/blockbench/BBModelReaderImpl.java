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

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.ModelDataCursor;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.partial.ModelAsset;
import team.unnamed.hephaestus.reader.ModelFormatException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Objects;

final class BBModelReaderImpl implements BBModelReader {

    private static final JsonParser JSON_PARSER = new JsonParser();

    private final ModelDataCursor cursor;

    BBModelReaderImpl(final @NotNull ModelDataCursor cursor) {
        this.cursor = Objects.requireNonNull(cursor, "cursor");
    }

    @Override
    public @NotNull Model read(final @NotNull InputStream input) {
        final Reader reader = new InputStreamReader(input);
        final JsonObject json;

        try {
            json = JSON_PARSER.parse(reader).getAsJsonObject();
        } catch (JsonIOException | JsonSyntaxException e) {
            throw new ModelFormatException("Failed to parse JSON from BBMODEL", e);
        } catch (IllegalStateException e) {
            throw new ModelFormatException("Data inside BBMODEL file is not a JSON object", e);
        }

        final BBModelData modelData = new BBModelData();

        final JsonObject meta = json.get("meta").getAsJsonObject();
        final String modelName = json.get("name").getAsString();

        modelData.modelDataCursor = cursor;
        modelData.name = modelName;

        // check for box uv
        if (!GsonUtil.isNullOrAbsent(meta, "box_uv") && meta.get("box_uv").getAsBoolean()) {
            throw new ModelFormatException("Model '" + modelName + "' uses box UV, which is not supported.");
        }

        final JsonObject resolution = json.getAsJsonObject("resolution");
        modelData.textureWidth = resolution.get("width").getAsInt();
        modelData.textureHeight = resolution.get("height").getAsInt();

        TextureReader.readTextures(json, modelData);
        ElementReader.readElements(json, modelData);
        final Map<String, Animation> animations = AnimationReader.readAnimations(json, modelData);

        return new Model(
                modelName,
                modelData.bones,
                modelData.boundingBox,
                new ModelAsset(
                        modelName,
                        modelData.textures,
                        modelData.textureMapping,
                        modelData.boneAssets
                ),
                animations
        );
    }

}
