package team.unnamed.hephaestus.io;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Extension for {@link ZipOutputStream}
 * to ease the write for some data objects
 */
public class ZipDataOutputStream
        extends ZipOutputStream {

    private final Gson gson;

    public ZipDataOutputStream(
            OutputStream out,
            Gson gson
    ) {
        super(out);
        this.gson = gson;
    }

    /**
     * Creates a new {@link ZipEntry} with
     * the given {@code name} and it's setted
     * as next entry
     */
    public ZipEntry startEntry(String path) throws IOException {
        ZipEntry entry = new ZipEntry(path);
        this.putNextEntry(entry);
        return entry;
    }

    /**
     * Writes the given {@code string} using
     * the {@link StandardCharsets#UTF_8} charset
     */
    public void writeString(String string) throws IOException {
        this.write(string.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Converts the given {@code object} to a
     * json string, and it's written into the
     * stream using the {@link StandardCharsets#UTF_8}
     * charset
     */
    public void writeJson(Object object) throws IOException {
        this.writeString(this.gson.toJson(object));
    }

}