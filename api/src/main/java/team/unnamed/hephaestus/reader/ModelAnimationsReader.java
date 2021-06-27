package team.unnamed.hephaestus.reader;

import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.io.*;
import java.util.List;

public interface ModelAnimationsReader {

    default List<ModelAnimation> read(InputStream input) throws IOException {
        try (Reader reader = new InputStreamReader(input)) {
            return read(reader);
        }
    }

    List<ModelAnimation> read(Reader reader);

}