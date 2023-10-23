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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.key.InvalidKeyException;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Writable;
import team.unnamed.hephaestus.asset.TextureAsset;
import team.unnamed.hephaestus.reader.ModelFormatException;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

final class TextureReader {

    private static final String BASE_64_PREFIX = "data:image/png;base64,";

    private TextureReader() {
    }

    /**
     * Reads the textures from the given {@code json} object
     * and puts the data into the given {@code textures} and
     * their mappings to the given {@code textureMappings}
     */
    static @NotNull Map<String, TextureAsset> readTextures(final @NotNull JsonObject json, final @NotNull BBModelData modelData) {
        final Map<String, TextureAsset> textures = new LinkedHashMap<>();
        for (final JsonElement textureNode : json.get("textures").getAsJsonArray()) {
            final JsonObject textureObjectNode = textureNode.getAsJsonObject();

            final String id = textureObjectNode.get("id").getAsString();
            @Subst("texture.png")
            final String name = textureObjectNode.get("name").getAsString();
            final String source = textureObjectNode.get("source").getAsString();

            if (!source.startsWith(BASE_64_PREFIX)) {
                throw new ModelFormatException("Texture '" + name + "' of '" + modelData.name + "' doesn't" +
                        " contain a valid texture source. Must start with a Base64 prefix");
            }

            final Writable textureData = Writable.bytes(Base64.getDecoder().decode(source.substring(BASE_64_PREFIX.length())));
            final TextureAsset texture;

            try {
                texture = TextureAsset.textureAsset(id, name, textureData);
            } catch (final InvalidKeyException e) {
                throw new ModelFormatException("Texture '" + name + "' of '" + modelData.name + "' has" +
                        " an invalid name.", e);
            }

            textures.put(id, texture);
        }
        return textures;
    }

}
