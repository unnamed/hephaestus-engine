package team.unnamed.hephaestus.model.entity;

import org.bukkit.Location;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModelLivingEntity {

    private final Model model;

    private final Location location;

    /**
     * The unique id for this living
     * entity
     */
    private final UUID id;

    /**
     * Holds a relation of model bone name as key
     * and its linked entity ID as value
     */
    private final Map<String, UUID> entities = new HashMap<>();

    /** The current entity tick, used to animate the entity */
    private float tick = 0;

    /**
     * Reference of the current animation
     * for this living entity
     */
    private ModelAnimation animation;

    public ModelLivingEntity(Model model, Location location, UUID id) {
        this.model = model;
        this.location = location;
        this.id = id;
    }

    public Model getModel() {
        return model;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getId() {
        return id;
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

    public Map<String, UUID> getEntities() {
        return entities;
    }

}