package team.unnamed.hephaestus.model.view;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.util.HashMap;
import java.util.Map;

public class ModelView {

    private final ModelViewController controller;

    private final Model model;

    private final Player viewer;

    private Location location;

    /**
     * Holds a relation of model bone name as key
     * and its linked entity ID as value
     */
    private final Map<String, Object> entities = new HashMap<>();

    /** The current entity tick, used to animate the entity */
    private float tick = 0;

    /**
     * Reference of the current animation
     * for this living entity
     */
    private ModelAnimation animation;

    public ModelView(
            ModelViewController controller,
            Model model,
            Player viewer,
            Location location
    ) {
        this.controller = controller;
        this.model = model;
        this.viewer = viewer;
        this.location = location;
    }

    public Model getModel() {
        return model;
    }

    public Player getViewer() {
        return viewer;
    }

    public Location getLocation() {
        return location;
    }

    public void resetTick() {
        tick = 1;
    }

    public float getTick() {
        return tick;
    }

    public void increaseTick() {
        tick++;
    }

    public ModelAnimation getAnimation() {
        return animation;
    }

    public void setAnimation(ModelAnimation animation) {
        this.animation = animation;
    }

    public Map<String, Object> getEntities() {
        return entities;
    }

    //#region Delegations to ModelViewController
    public void teleport(Location newLocation) {
        this.location = newLocation.clone();
        controller.teleport(this, this.location);
    }
    //#endregion

}