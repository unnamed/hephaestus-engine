package team.unnamed.hephaestus.minestomce.skin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.playermodel.Skin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MinetoolsSkinProvider implements SkinProvider {

    private static final JsonParser JSON_PARSER = new JsonParser();

    @Override
    public @Nullable Skin fetchSkin(String skin) throws Exception {

        // fetch UUID by username
        String uuid;
        {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + skin);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "mineaqua-npcs");
            connection.setRequestMethod("GET");

            // execute and read
            try (Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                uuid = JSON_PARSER.parse(reader)
                        .getAsJsonObject()
                        .get("id")
                        .getAsString();
            }
        }

        // fetch skin by uuid
        {
            URL url = new URL("https://api.minetools.eu/profile/" + uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "mineaqua-npcs");
            connection.setRequestMethod("GET");

            try (Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                JsonObject node = JSON_PARSER.parse(reader).getAsJsonObject();
                JsonArray properties = node
                        .getAsJsonObject("raw")
                        .getAsJsonArray("properties");

                Skin.Type type = node
                        .getAsJsonObject("decoded")
                        .getAsJsonObject("textures")
                        .getAsJsonObject("SKIN")
                        .has("metadata")  ? Skin.Type.SLIM : Skin.Type.NORMAL;

                for (JsonElement element : properties) {
                    JsonObject property = element.getAsJsonObject();
                    if (property.get("name").getAsString().equals("textures")) {
                        String signature = property.get("signature").getAsString();
                        String value = property.get("value").getAsString();

                        return new Skin(signature, value, type);
                    }
                }

                throw new IllegalStateException("'textures' property not found in returned skin");
            }
        }
    }

}