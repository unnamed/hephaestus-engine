package team.unnamed.hephaestus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for working with
 * {@link InputStream}s and {@link OutputStream}s
 */
public final class Streams {

    private Streams() {
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
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) != -1) {
            output.write(buffer, 0, len);
        }
    }

}
