package team.unnamed.hephaestus.model.view;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimationQueue;

/** Responsible of spawning {@link Model} */
public interface ModelViewRenderer {

    /**
     * Spawns the bones of the given {@code model}
     * @return The living model entity
     */
    ModelView render(Player viewer, Model model, Location location, ModelAnimationQueue animationQueue);

    default ModelView render(Player viewer, Model model, Location location) {
        return render(viewer, model, location, new ModelAnimationQueue());
    }

}