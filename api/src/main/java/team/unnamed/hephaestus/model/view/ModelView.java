package team.unnamed.hephaestus.model.view;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.util.HashMap;
import java.util.Map;

public class ModelView {

    private final ModelViewController controller;
    private final ModelViewAnimator animator;

    private final Model model;

    private final Player viewer;

    private Location location;

    /**
     * Holds a relation of model bone name as key
     * and its linked entity ID as value
     */
    private final Map<String, Object> entities = new HashMap<>();


    /**
     * Reference of the current animation
     * for this living entity
     */
    private ModelAnimation animation;

    /** The current animation task id, -1 if absent */
    private int animationTaskId = -1;

    /** The current entity tick, used to animate the entity */
    private float tick = 0;

    public ModelView(
            ModelViewController controller,
            ModelViewAnimator animator,
            Model model,
            Player viewer,
            Location location
    ) {
        this.controller = controller;
        this.animator = animator;

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

    public Map<String, Object> getEntities() {
        return entities;
    }

    //#region Delegations to other handler classes
    public void playAnimation(String animationName) {
        ModelAnimation animation = model.getAnimations().get(animationName);
        if (animation == null) {
            throw new IllegalArgumentException("Unknown animation: '" + animationName + "'");
        }
        playAnimation(animation);
    }

    public void playAnimation(ModelAnimation animation) {
        // stop previous animation
        if (animationTaskId != -1) {
            Bukkit.getScheduler().cancelTask(animationTaskId);
        }
        if (this.animation == null) {
            this.animation = animation;
        } else {
            this.animation = ModelAnimation.merge(this.animation, animation);
        }
        this.animationTaskId = this.animator.animate(this, this.animation);
    }

    public void show() {
        controller.show(this);
    }

    public void hide() {
        controller.hide(this);
    }

    public void teleport(Location newLocation) {
        this.location = newLocation.clone();
        controller.teleport(this, this.location);
    }

    public void setBonePose(ModelBone bone, EulerAngle angle) {
        controller.setBonePose(this, bone, angle);
    }

    public void teleportBone(ModelBone bone, Location location) {
        controller.teleportBone(this, bone, location);
    }
    //#endregion

}
