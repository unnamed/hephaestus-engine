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
import com.google.gson.JsonObject;
import team.unnamed.creative.base.Writable;
import team.unnamed.hephaestus.reader.ModelFormatException;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

final class TextureReader {

    private static final String BASE_64_PREFIX = "data:image/png;base64,";

    /**
     * Reads the textures from the given {@code json} object
     * and puts the data into the given {@code textures} and
     * their mappings to the given {@code textureMappings}
     */
    static void readTextures(
            JsonObject json,
            BBModelData modelData
    ) {
        JsonArray texturesJson = json.get("textures").getAsJsonArray();

        for (int index = 0; index < texturesJson.size(); index++) {

            JsonObject textureJson = texturesJson.get(index).getAsJsonObject();
            String name = textureJson.get("name").getAsString();
            String source = textureJson.get("source").getAsString();

            if (!(source.startsWith(BASE_64_PREFIX))) {
                throw new ModelFormatException("Model doesn't contains a valid texture source. Not Base64");
            }

            String base64Source = source.substring(BASE_64_PREFIX.length());

            // map to index
            modelData.textureMapping.put(index, name);
            modelData.textures.put(name, Writable.bytes(Base64.getDecoder().decode(base64Source)));
        }
    }

}
