package team.unnamed.hephaestus.resourcepack;

import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.io.TreeOutputStream;

import java.io.IOException;

/**
 * Implementation of {@link ResourcePackWriter} that
 * writes the resource pack information.
 * @see ResourcePackInfo
 */
public class ResourcePackInfoWriter
        implements ResourcePackWriter {

    private final ResourcePackInfo info;

    public ResourcePackInfoWriter(ResourcePackInfo info) {
        this.info = info;
    }

    @Override
    public void write(TreeOutputStream output) throws IOException {
        // write the pack data
        output.useEntry("pack.mcmeta");
        Streams.writeUTF(
                output,
                "{ " +
                        "\"pack\":{" +
                        "\"pack_format\":" + info.getFormat() + "," +
                        "\"description\":\"" + info.getDescription() + "\"" +
                        "}" +
                        "}"
        );
        output.closeEntry();

        // write the pack icon if not null
        Streamable icon = info.getIcon();
        if (icon != null) {
            output.useEntry("pack.png");
            icon.transfer(output);
            output.closeEntry();
        }
    }

}
