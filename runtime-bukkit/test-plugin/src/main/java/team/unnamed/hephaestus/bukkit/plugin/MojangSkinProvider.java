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
package team.unnamed.hephaestus.bukkit.plugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.player.Skin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MojangSkinProvider {

    private final JsonParser parser = new JsonParser();
    private final Map<String, Skin> cached = new HashMap<>();

    public @Nullable Skin fetchSkin(String skinName) throws Exception {
        if (cached.containsKey(skinName)) return cached.get(skinName);

        // fetch UUID by username
        String uuid;
        {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + skinName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "mineaqua-npcs");
            connection.setRequestMethod("GET");

            // execute and read
            try (Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                uuid = parser.parse(reader)
                        .getAsJsonObject()
                        .get("id")
                        .getAsString();
            }
        }

        // fetch skinName by uuid
        {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setDoInput(true);
            try (InputStream is = connection.getInputStream()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                JsonObject node = parser.parse(in).getAsJsonObject();
                String value = node
                        .getAsJsonArray("properties").get(0).getAsJsonObject()
                        .get("value").getAsString();
                boolean slim = false;
                try {
                    System.out.println(new String(Base64.getDecoder().decode(value.getBytes())));
                    slim = parser.parse(new String(Base64.getDecoder().decode(value.getBytes())))
                            .getAsJsonObject()
                            .get("textures").getAsJsonObject()
                            .get("SKIN").getAsJsonObject()
                            .get("metadata").getAsJsonObject()
                            .get("model").getAsString().equals("slim");
                } catch (Exception ignored) {
                }
                Skin skin = Skin.skin(
                        node.getAsJsonArray("properties").get(0).getAsJsonObject().get("signature").getAsString(),
                        value,
                        slim ? Skin.Type.SLIM : Skin.Type.NORMAL
                );
                cached.put(skinName, skin);
                return skin;
            }
        }
    }

}