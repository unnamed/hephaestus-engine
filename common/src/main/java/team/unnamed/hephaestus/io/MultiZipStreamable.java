package team.unnamed.hephaestus.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.ZipOutputStream;

public class MultiZipStreamable implements Streamable {

    private final Collection<Streamable> delegates;

    public MultiZipStreamable(Collection<Streamable> delegates) {
        this.delegates = delegates;
    }

    public MultiZipStreamable(Streamable... delegates) {
        this.delegates = Arrays.asList(delegates);
    }

    @Override
    public void transfer(OutputStream stream) throws IOException {
        ZipOutputStream output = stream instanceof ZipOutputStream
                ? (ZipOutputStream) stream
                : new ZipOutputStream(stream);

        try {
            for (Streamable streamable : delegates) {
                streamable.transfer(output);
            }
        } finally {
            if (stream != output) {
                // finish but don't close
                output.finish();
            }
        }
    }
}