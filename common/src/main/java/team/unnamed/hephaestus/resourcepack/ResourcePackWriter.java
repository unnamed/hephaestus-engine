package team.unnamed.hephaestus.resourcepack;

import team.unnamed.hephaestus.io.TreeOutputStream;

import java.io.IOException;

/**
 * Responsible for writing assets for a Minecraft resource pack,
 * it writes the information into a {@link TreeOutputStream}
 * @author yusshu (Andre Roldan)
 */
public interface ResourcePackWriter {

    /**
     * Writes the resource pack assets into
     * the given tree {@code output}
     *
     * <strong>Implementations of this method
     * should not close the provided {@code output}
     * </strong>
     */
    void write(TreeOutputStream output) throws IOException;

    /**
     * Creates a {@link ResourcePackWriter} instance compound
     * by other {@code writers}. The {@link ResourcePackWriter#write}
     * method is invoked for all the given writers
     */
    static ResourcePackWriter compose(ResourcePackWriter... writers) {
        return output -> {
            for (ResourcePackWriter writer : writers) {
                writer.write(output);
            }
        };
    }

}
