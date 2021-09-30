package team.unnamed.hephaestus.model.reader;

import team.unnamed.hephaestus.model.Model;

import java.io.IOException;
import java.io.Reader;

/**
 * Responsible for reading and parsing
 * {@link Model} from a sequence of bytes,
 * structure of models depend on implementation
 */
public interface ModelReader {

    /**
     * Reads a model from the given {@code reader}
     * @param reader The reader used to read and parse
     *               the {@link Model} instance
     * @return The parsed model
     * @throws IOException If parsing fails
     */
    Model read(Reader reader) throws IOException;

}