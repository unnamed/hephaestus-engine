package team.unnamed.hephaestus.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class for working with
 * {@link InputStream}s and {@link OutputStream}s
 */
public final class Streams {

    /**
     * Determines the length of the buffer
     * used in the {@link Streams#pipe}
     * operation
     */
    private static final int BUFFER_LENGTH = 1024;

    private Streams() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Reads and writes the data from the
     * given {@code input} to the given {@code output}
     * by using a fixed-size byte buffer
     * (fastest way)
     *
     * <p>Note that this method doesn't close
     * the inputs or outputs</p>
     *
     * @throws IOException If an error occurs while
     * reading or writing the data
     */
    public static void pipe(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[BUFFER_LENGTH];
        int length;
        while ((length = input.read(buffer)) != -1) {
            output.write(buffer, 0, length);
        }
    }

    /**
     * Creates a {@link ByteArrayInputStream} for reading
     * the given {@code string} using the specified {@code charset}
     */
    public static InputStream fromString(String string, Charset charset) {
        byte[] bytes = string.getBytes(charset);
        return new ByteArrayInputStream(bytes);
    }

    /**
     * Creates a {@link InputStream} for reading and decoding
     * the given {@code string} from Base64 using the specified
     * {@code charset}
     */
    public static InputStream fromBase64(String string, Charset charset) {
        InputStream original = fromString(string, charset);
        return Base64.getDecoder().wrap(original);
    }

}
