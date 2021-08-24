package team.unnamed.hephaestus.resourcepack;

import team.unnamed.hephaestus.io.Streamable;

import java.io.IOException;

/**
 * Interface for exporting resources packs,
 * defaults are created in {@link ResourceExports}
 * @param <T> Represents the export result
 */
public interface ResourceExporter<T> {

    /**
     * Exports the given {@code data},
     */
    T export(Streamable data) throws IOException;

}