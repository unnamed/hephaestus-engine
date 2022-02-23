package team.unnamed.hephaestus.view;

import org.bukkit.Location;
import team.unnamed.hephaestus.Model;

/**
 * Responsible for spawning {@link Model}, or
 * converting {@link Model} to {@link ModelView}
 * concrete instances at specific world locations
 *
 * @since 1.0.0
 */
public interface ModelViewRenderer {

    /**
     * Spawns the given {@link Model} model
     * instance at the given world location
     *
     * @param model The rendered model
     * @param location The model view location
     *
     * @return The created model view
     * @since 1.0.0
     */
    BukkitModelView render(Model model, Location location);

}