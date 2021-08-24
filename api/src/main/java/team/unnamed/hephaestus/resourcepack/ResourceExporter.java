package team.unnamed.hephaestus.resourcepack;

import team.unnamed.hephaestus.io.Streamable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Interface for exporting resources packs,
 * defaults are created in {@link ResourceExports}
 * @param <T> Represents the export result
 */
public interface ResourceExporter<T> {

    default T export(Streamable... data) throws IOException {
        return export(Arrays.asList(data));
    }

    /**
     * Exports the given {@code data},
     */
    T export(Collection<Streamable> data) throws IOException;

}