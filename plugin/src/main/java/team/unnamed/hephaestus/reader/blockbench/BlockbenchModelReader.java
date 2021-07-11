package team.unnamed.hephaestus.reader.blockbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelGeometry;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.reader.ModelReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockbenchModelReader implements ModelReader {

    private static final String BASE_64_PREFIX = "data:image/png;base64,";
    private static final JsonParser JSON_PARSER = new JsonParser();

    /**
     * List containing supported block-bench format
     * versions
     */
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "3.6"
    );

    private final BlockbenchModelGeometryReader geometryReader = new BlockbenchModelGeometryReader();
    private final BlockbenchModelAnimationsReader animationsReader = new BlockbenchModelAnimationsReader();

    @Override
    public Model read(File folder) throws IOException {

        File modelFile = new File(folder, "model.bbmodel");

        if (!modelFile.exists()) {
            throw new IOException(
                    "Could not read a model from '" + folder.getName() + "' "
                            + "due to the absence of a 'model.bbmodel' file"
            );
        }

        ModelGeometry geometry;
        Map<String, ModelAnimation> animations;

        try (Reader reader = new FileReader(modelFile)) {
            JsonObject json = JSON_PARSER.parse(reader).getAsJsonObject();
            JsonElement formatVersionElement = json.get("meta").getAsJsonObject().get("format_version");

            if (
                    formatVersionElement == null
                            || !SUPPORTED_FORMATS.contains(formatVersionElement.getAsString())
            ) {
                throw new IOException("Provided JSON doesn't have a valid format version");
            }

            geometry = geometryReader.read(json);
            animations = animationsReader.read(json);

            JsonArray texturesJson = json.get("textures").getAsJsonArray();
            Map<String, Streamable> textures = new HashMap<>();

            for (JsonElement textureElement : texturesJson) {
                JsonObject textureJson = textureElement.getAsJsonObject();

                String name = textureJson.get("name").getAsString();
                String source = textureJson.get("source").getAsString();

                if (!(source.startsWith(BASE_64_PREFIX))) {
                    throw new IOException(
                            "Model '" + folder.getName() + "' contains an invalid "
                                    + "texture source. Not Base64"
                    );
                }

                String base64Source = source.substring(BASE_64_PREFIX.length());
                textures.put(name, () -> Streams.fromBase64(base64Source, StandardCharsets.UTF_8));
            }

            return new Model(
                    folder.getName(),
                    geometry,
                    animations,
                    textures
            );
        }
    }

}
