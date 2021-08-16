package team.unnamed.hephaestus.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    /**
     * Creates a {@link Streamable} from a resource.
     * @param loader The class loader holding the resource
     * @param name The resource name
     */
    static Streamable ofResource(ClassLoader loader, String name) {
        return () -> loader.getResourceAsStream(name);
    }

    /**
     * Creates a {@link Streamable} representing the
     * given {@code file}. Assumes that the file is
     * readable and writable.
     * @param file The wrapped file
     */
    static Streamable ofFile(File file) {
        return new Streamable() {

            @Override
            public InputStream openIn() throws IOException {
                return new FileInputStream(file);
            }

            @Override
            public OutputStream openOut() throws IOException {
                return new FileOutputStream(file);
            }

        };
    }

}
