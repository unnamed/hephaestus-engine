package team.unnamed.hephaestus.resourcepack;

import team.unnamed.hephaestus.model.Model;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Responsible of exporting resource packs
 * from {@link Model}s
 */
public interface ResourcePackExporter {

    /**
     * Exports the given {@link Model}s
     *
     * <strong>Note that this method doesn't
     * close the given {@code stream}</strong>
     *
     * @param models The raw models
     * @return The exported models with all
     * it's bones model data loaded
     */
    List<Model> export(OutputStream stream, List<Model> models) throws IOException;

}
