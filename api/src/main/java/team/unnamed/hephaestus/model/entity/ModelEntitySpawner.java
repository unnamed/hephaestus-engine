package team.unnamed.hephaestus.model.entity;

import org.bukkit.Location;
import team.unnamed.hephaestus.model.Model;

/** Responsible of spawning {@link Model} */
public interface ModelEntitySpawner {

    /**
     * Spawns the bones of the given {@code model}
     * @return The living model entity
     */
    ModelLivingEntity spawn(Model model, Location location);

}
