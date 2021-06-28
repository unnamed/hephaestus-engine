package team.unnamed.hephaestus.reader;

import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.io.*;
import java.util.Map;

public interface ModelAnimationsReader {

    default Map<String, ModelAnimation> read(InputStream input) throws IOException {
        try (Reader reader = new InputStreamReader(input)) {
            return read(reader);
        }
    }

    Map<String, ModelAnimation> read(Reader reader) throws IOException;

}