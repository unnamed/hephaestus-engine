package team.unnamed.hephaestus.io;

import java.io.ByteArrayInputStream;
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
 *
 * @author yusshu (Andre Roldan)
 */
public interface Streamable {

    /**
     * Opens an {@link InputStream} for reading
     * data from this data source instance
     * @throws IOException If opening fails
     */
    default InputStream openIn() throws IOException {
        throw new UnsupportedOperationException(
                "This data source doesn't support reads"
        );
    }

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
     * Transfers the data from this streamable object
     * to the given {@code output}.
     *
     * <strong>This isn't a shortcut method for transferring
     * {@link Streamable#openIn()} information to the given
     * {@code output}, however, it's the default behavior</strong>
     *
     * <strong>Note that the support of this method
     * doesn't always indicate the support of the
     * {@link Streamable#openIn()} method</strong>
     *
     * <strong>The method implementations should not
     * close the given {@code output}</strong>
     *
     * @throws IOException If writing fails
     */
    default void transfer(OutputStream output) throws IOException {
        try (InputStream input = openIn()) {
            Streams.pipe(input, output);
        }
    }

    /**
     * Creates a {@link Streamable} from a resource.
     * @param loader The class loader holding the resource
     * @param name The resource name
     */
    static Streamable ofResource(ClassLoader loader, String name) {
        return new Streamable() {
            @Override
            public InputStream openIn() throws IOException {
                InputStream input = loader.getResourceAsStream(name);
                if (input == null) {
                    throw new IllegalStateException("Resource '"
                            + name + "' doesn't exist");
                }
                return input;
            }
        };
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

    /**
     * Creates a read-only {@link Streamable} representing
     * the given {@code bytes}. Doesn't support writing and
     * doesn't modify the bytes
     * @param bytes the wrapped bytes
     */
    static Streamable ofBytes(byte[] bytes) {
        return new Streamable() {
            @Override
            public InputStream openIn() {
                return new ByteArrayInputStream(bytes);
            }
        };
    }

}