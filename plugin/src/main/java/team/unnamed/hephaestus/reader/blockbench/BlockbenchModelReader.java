package team.unnamed.hephaestus.reader.blockbench;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelGeometry;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.reader.ModelReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BlockbenchModelReader implements ModelReader {

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
        }

        List<File> textures = new ArrayList<>();
        File[] children = folder.listFiles();

        if (children != null) {
            for (File texture : children) {
                if (texture.getName().endsWith(".png")) {
                    textures.add(texture);
                }
            }
        }

        return new Model(
                folder.getName(),
                geometry,
                animations,
                textures
        );
    }

}
