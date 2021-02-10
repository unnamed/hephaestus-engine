package team.unnamed.hephaestus.reader;

import team.unnamed.hephaestus.model.ModelGeometry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Responsible of reading {@link ModelGeometry} from
 * pure text (it can be contained by input streams or
 * readers)
 */
public interface ModelGeometryReader {

    /**
     * Reads a {@link ModelGeometry} from the
     * given {@code input}.
     *
     * <p>Note that this method closes the given
     * {@link InputStream}</p>
     */
    default ModelGeometry load(InputStream input) throws Exception {
        try (Reader reader = new InputStreamReader(input)) {
            return load(reader);
        }
    }

    /**
     * Reads a {@link ModelGeometry} from the
     * given {@code input}
     *
     * <p>Not that this method closes the given
     * {@link Reader}</p>
     */
    ModelGeometry load(Reader reader) throws Exception;

}
