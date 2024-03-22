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
package team.unnamed.hephaestus.view.modifier.player.skin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

final class MojangSkinProvider implements SkinProvider {
    @SuppressWarnings("deprecation")
    private static final JsonParser JSON_PARSER = new JsonParser();

    static @Nullable String fetchUUIDByUsername(final @NotNull String username) throws IOException {
        final var url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
        final var connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "hephaestus-engine/MojangSkinProvider");
        connection.setRequestMethod("GET");

        // execute and read
        final JsonObject json;
        try (final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            //noinspection deprecation
            json = JSON_PARSER.parse(reader).getAsJsonObject();
        }

        if (json.has("errorMessage")) {
            final var errorMessage = json.get("errorMessage").getAsString();
            if (errorMessage.startsWith("Couldn't find any profile with name")) {
                return null;
            } else {
                throw new IllegalStateException("Error while fetching UUID for username " + username + ": " + errorMessage);
            }
        }

        return json.get("id").getAsString();
    }
    
    @Override
    public @Nullable Skin fetch(final @NotNull String username) {
        try {
            return fetch0(username);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to fetch skin for username: '" + username + "'", e);
        }
    }

    private @Nullable Skin fetch0(final @NotNull String username) throws IOException {
        final var uuid = fetchUUIDByUsername(username);
        if (uuid == null) {
            return null;
        }
        
        // fetch skin by uuid
        final var url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
        final var connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setDoInput(true);

        try (final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            @SuppressWarnings("deprecation")
            final var json = JSON_PARSER.parse(reader).getAsJsonObject();
            final var textureProperty = json.getAsJsonArray("properties").get(0).getAsJsonObject();

            final var value = textureProperty.get("value").getAsString();
            final var signature = textureProperty.get("signature").getAsString();

            @SuppressWarnings("deprecation")
            final var valueJson = JSON_PARSER.parse(new String(Base64.getDecoder().decode(value.getBytes()))).getAsJsonObject();
            JsonObject texturesJson;
            JsonObject skinJson;
            JsonObject metadataJson;
            final var slim = valueJson.has("textures")
                    && (texturesJson = valueJson.getAsJsonObject("textures")).has("SKIN")
                    && (skinJson = texturesJson.getAsJsonObject("SKIN")).has("metadata")
                    && (metadataJson = skinJson.getAsJsonObject("metadata")).has("model")
                    && metadataJson.get("model").getAsString().equals("slim");

            return Skin.skin(signature, value, slim ? Skin.Type.SLIM : Skin.Type.NORMAL);
        }
    }
}