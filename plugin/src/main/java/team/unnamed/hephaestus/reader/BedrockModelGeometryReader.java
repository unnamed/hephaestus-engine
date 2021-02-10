package team.unnamed.hephaestus.reader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import team.unnamed.hephaestus.model.ModelGeometry;

import java.io.IOException;
import java.io.Reader;

/**
 * Implementation of {@link ModelGeometryReader} that
 * parses the inputs to JSON (Format used by the
 * Bedrock Model Geometry format) and then reads the values.
 *
 * <p>The Bedrock Model Geometry format is supported by
 * some modelation tools like Blockbench</p>
 */
public class BedrockModelGeometryReader implements ModelGeometryReader {

    @Override
    public ModelGeometry load(Reader reader) throws IOException {
        JsonElement json = JsonParser.parseReader(reader);
        return null;
    }

}
