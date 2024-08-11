package team.unnamed.hephaestus.minestomce.skin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import team.unnamed.hephaestus.player.Skin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AshconSkinProvider implements SkinProvider {

    private final Map<String, Skin> cache = new HashMap<>();

    @Override
    public Skin fetchSkin(String username) throws Exception {
        if (cache.containsKey(username)) {
            return cache.get(username);
        }

        URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + username);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "kaizen");
        connection.setRequestMethod("GET");

        try (Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            JsonObject node = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject textures = node.getAsJsonObject("textures");
            JsonObject raw = textures.getAsJsonObject("raw");

            Skin.Type type = textures.get("slim").getAsBoolean() ? Skin.Type.SLIM : Skin.Type.NORMAL;

            String signature = raw.get("signature").getAsString();
            String value = raw.get("value").getAsString();

            Skin result = Skin.skin(signature, value, type);
            cache.put(username, result);

            return result;
        }
    }
}
