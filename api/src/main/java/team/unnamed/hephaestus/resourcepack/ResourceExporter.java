package team.unnamed.hephaestus.resourcepack;

import team.unnamed.hephaestus.model.Model;

import java.io.IOException;
import java.util.List;

/**
 * Interface for exporting resources packs,
 * defaults are created in {@link ResourceExports}
 * @param <T> Represents the export result
 */
public interface ResourceExporter<T> {

    /**
     * Exports the given {@code models},
     */
    T export(List<Model> models) throws IOException;

}
