package team.unnamed.hephaestus.view;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/** Responsible for spawning {@link Model} */
public interface ModelViewRenderer {

    /**
     * Spawns the bones of the given {@code model}
     * @return The living model entity
     */
    BukkitModelView render(
            Model model,
            Location location,
            Collection<Player> viewers
    );

    default BukkitModelView render(
            Model model,
            Location location,
            Player... viewers
    ) {
        return render(model, location, Arrays.asList(viewers));
    }

    default BukkitModelView render(
            Model model,
            Location location
    ) {
        return render(model, location, new ArrayList<>());
    }

}