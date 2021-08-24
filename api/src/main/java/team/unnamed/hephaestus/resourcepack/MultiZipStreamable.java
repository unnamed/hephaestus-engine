package team.unnamed.hephaestus.resourcepack;

import team.unnamed.hephaestus.io.Streamable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.ZipOutputStream;

public class MultiZipStreamable implements Streamable {

    private final Collection<Streamable> streamables;

    public MultiZipStreamable(Collection<Streamable> streamables) {
        this.streamables = streamables;
    }

    public MultiZipStreamable(Streamable... streamables) {
        this.streamables = Arrays.asList(streamables);
    }

    @Override
    public void transfer(OutputStream stream) throws IOException {
        ZipOutputStream output = stream instanceof ZipOutputStream
                ? (ZipOutputStream) stream
                : new ZipOutputStream(stream);

        for (Streamable streamable : streamables) {
            streamable.transfer(output);
        }
    }
}