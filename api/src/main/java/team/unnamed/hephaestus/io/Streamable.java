package team.unnamed.hephaestus.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents any object that can be
 * read or written using {@link InputStream}
 * and {@link OutputStream}, some implementations
 * may not support reading or writing
 */
public interface Streamable {

    /**
     * Opens an {@link InputStream} for reading
     * data from this data source instance
     * @throws IOException If opening fails
     */
    InputStream openIn() throws IOException;

    /**
     * Opens a {@link OutputStream} for writing
     * data to this data source instance
     * @throws IOException If opening fails
     */
    default OutputStream openOut() throws IOException {
        throw new UnsupportedOperationException(
                "This data source doesn't support writes"
        );
    }

}
