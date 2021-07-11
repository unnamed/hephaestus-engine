package team.unnamed.hephaestus.model.view;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.Model;

/** Responsible of spawning {@link Model} */
public interface ModelViewRenderer {

    /**
     * Spawns the bones of the given {@code model}
     * @return The living model entity
     */
    ModelView render(Player viewer, Model model, Location location);

}
