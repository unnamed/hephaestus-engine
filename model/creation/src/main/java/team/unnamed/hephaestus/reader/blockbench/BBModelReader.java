package team.unnamed.hephaestus.reader.blockbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelAsset;
import team.unnamed.hephaestus.model.ModelDataCursor;
import team.unnamed.hephaestus.model.ModelGeometry;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.reader.ModelReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BBModelReader implements ModelReader {

    private static final String BASE_64_PREFIX = "data:image/png;base64,";
    private static final JsonParser JSON_PARSER = new JsonParser();

    /**
     * List containing supported block-bench format
     * versions
     */
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "3.6"
    );

    private final BBModelGeometryReader geometryReader = new BBModelGeometryReader();
    private final BBModelAnimationReader animationsReader = new BBModelAnimationReader();

    private final ModelDataCursor cursor;

    public BBModelReader(ModelDataCursor cursor) {
        this.cursor = cursor;
    }

    public BBModelReader() {
        this(new ModelDataCursor(1));
    }

    @Override
    public Model read(String modelName, Reader reader) throws IOException {

        JsonObject json = JSON_PARSER.parse(reader).getAsJsonObject();
        JsonElement formatVersionElement = json.get("meta").getAsJsonObject().get("format_version");

        if (
                formatVersionElement == null
                        || !SUPPORTED_FORMATS.contains(formatVersionElement.getAsString())
        ) {
            throw new IOException("Provided JSON doesn't have a valid format version");
        }

        ModelGeometry geometry = geometryReader.read(cursor, json);
        Map<String, ModelAnimation> animations = animationsReader.read(cursor, json);

        JsonArray texturesJson = json.get("textures").getAsJsonArray();
        Map<String, Streamable> textures = new HashMap<>();

        for (JsonElement textureElement : texturesJson) {
            JsonObject textureJson = textureElement.getAsJsonObject();

            String name = textureJson.get("name").getAsString();
            String source = textureJson.get("source").getAsString();

            if (!(source.startsWith(BASE_64_PREFIX))) {
                throw new IOException(
                        "Model '" + modelName + "' contains an invalid "
                                + "texture source. Not Base64"
                );
            }

            String base64Source = source.substring(BASE_64_PREFIX.length());
            textures.put(name, new Streamable() {
                @Override
                public InputStream openIn() {
                    return Streams.fromBase64(base64Source, StandardCharsets.UTF_8);
                }
            });
        }

        return new Model(
                modelName,
                geometry.getBones(),
                new ModelAsset(
                        modelName,
                        geometry.getTextureWidth(),
                        geometry.getTextureHeight(),
                        textures,
                        geometry.getTextureMap(),
                        geometry.getBonesAssets(),
                        animations
                )
        );
    }
}