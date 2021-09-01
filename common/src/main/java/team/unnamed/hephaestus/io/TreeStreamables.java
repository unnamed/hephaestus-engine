package team.unnamed.hephaestus.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for working with {@link Streamable}
 * implementations that use the {@link TreeOutputStream}
 */
public final class TreeStreamables {

    private TreeStreamables() {
    }

    /**
     * Wraps the given {@code components} so when {@link Streamable#transfer}
     * is called, it creates a {@link ZipOutputStream} and a {@link TreeOutputStream}
     * for it, so the specified {@code components} always receive an instance
     * of {@link TreeOutputStream}
     */
    public static Streamable transferringZip(Streamable... components) {
        return new Streamable() {

            @Override
            public void transfer(OutputStream output) throws IOException {
                TreeOutputStream treeOutput = output instanceof TreeOutputStream
                        ? (TreeOutputStream) output
                        : TreeOutputStream.forZip(new ZipOutputStream(output));
                try {
                    for (Streamable component : components) {
                        component.transfer(treeOutput);
                    }
                } finally {
                    treeOutput.finish();
                }
            }

        };
    }

}
