package team.unnamed.hephaestus.model.view;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimationQueue;

import java.util.Arrays;
import java.util.Collection;

/** Responsible of spawning {@link Model} */
public interface ModelViewRenderer {

    /**
     * Spawns the bones of the given {@code model}
     * @return The living model entity
     */
    ModelView render(
            Model model,
            Location location,
            ModelAnimationQueue animationQueue,
            Collection<? extends Player> viewers
    );

    default ModelView render(
            Model model,
            Location location,
            Collection<? extends Player> viewers
    ) {
        return render(model, location, new ModelAnimationQueue(), viewers);
    }

    default ModelView render(
            Model model,
            Location location,
            ModelAnimationQueue animationQueue,
            Player... viewers
    ) {
        return render(model, location, animationQueue, Arrays.asList(viewers));
    }

    default ModelView render(
            Model model,
            Location location,
            Player... viewers
    ) {
        return render(model, location, Arrays.asList(viewers));
    }

}