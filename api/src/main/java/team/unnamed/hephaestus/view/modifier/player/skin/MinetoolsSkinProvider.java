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

import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

final class MinetoolsSkinProvider implements SkinProvider {
    @SuppressWarnings("deprecation")
    private static final JsonParser JSON_PARSER = new JsonParser();

    @Override
    public @Nullable Skin fetch(final @NotNull String username) {
        try {
            return fetch0(username);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to fetch skin for username: '" + username + "'", e);
        }
    }

    private @Nullable Skin fetch0(final @NotNull String username) throws IOException {
        // fetch UUID by username
        final var uuid = MojangSkinProvider.fetchUUIDByUsername(username);
        if (uuid == null) {
            return null;
        }

        // fetch skin by uuid
        final var url = new URL("https://api.minetools.eu/profile/" + uuid);
        final var connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "hephaestus-engine/MinetoolsSkinProvider");
        connection.setRequestMethod("GET");

        try (final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            @SuppressWarnings("deprecation")
            final var node = JSON_PARSER.parse(reader).getAsJsonObject();
            final var properties = node
                    .getAsJsonObject("raw")
                    .getAsJsonArray("properties");

            final var type = node
                    .getAsJsonObject("decoded")
                    .getAsJsonObject("textures")
                    .getAsJsonObject("SKIN")
                    .has("metadata")  ? Skin.Type.SLIM : Skin.Type.NORMAL;

            for (final var element : properties) {
                final var property = element.getAsJsonObject();
                if (property.get("name").getAsString().equals("textures")) {
                    final var signature = property.get("signature").getAsString();
                    final var value = property.get("value").getAsString();
                    return Skin.skin(signature, value, type);
                }
            }

            throw new IllegalStateException("'textures' property not found in returned skin");
        }
    }

}