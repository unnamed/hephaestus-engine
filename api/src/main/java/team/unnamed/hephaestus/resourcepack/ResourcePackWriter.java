package team.unnamed.hephaestus.resourcepack;

import team.unnamed.hephaestus.model.Model;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Responsible of writing resource packs
 * from {@link Model}s
 */
public interface ResourcePackWriter {

    /**
     * Writes the given {@link Model}s
     *
     * <strong>Note that this method doesn't
     * close the given {@code stream}</strong>
     *
     * @param models The raw models
     * @return The exported models with all
     * bone model data inside it
     */
    List<Model> write(OutputStream stream, List<Model> models) throws IOException;

}
