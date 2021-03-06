package team.unnamed.hephaestus.model.view;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelAnimationQueue;

import java.util.HashMap;
import java.util.Map;

public class ModelView {

    private final static int DEFAULT_PRIORITY = 0;
    private final static int DEFAULT_TRANSITION_TIME = 8;

    private final ModelViewController controller;
    private final ModelViewAnimator animator;
    private final ModelAnimationQueue animationQueue;

    private final Model model;

    private final Player viewer;

    private Location location;

    private int taskId;

    /**
     * Holds a relation of model bone name as key
     * and its linked entity ID as value
     */
    private final Map<String, Object> entities = new HashMap<>();

    public ModelView(
            ModelViewController controller,
            ModelViewAnimator animator,
            Model model,
            Player viewer,
            Location location
    ) {
        this.controller = controller;
        this.animator = animator;
        this.animationQueue = new ModelAnimationQueue();

        this.model = model;
        this.viewer = viewer;
        this.location = location;

        this.taskId = 0;
    }

    public ModelAnimationQueue getAnimationQueue() {
        return animationQueue;
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

    public int getAnimatorTaskId() {
        return taskId;
    }

    public Map<String, Object> getEntities() {
        return entities;
    }

    public void playAnimation(String animationName){
        this.playAnimation(animationName, DEFAULT_PRIORITY, DEFAULT_TRANSITION_TIME);
    }

    public void playAnimation(String animationName, int priority, int transitionTicks) {
        ModelAnimation animation = model.getAnimations().get(animationName);
        if (animation == null) {
            throw new IllegalArgumentException("Unknown animation: '" + animationName + "'");
        }
        playAnimation(animation, priority, transitionTicks);
    }

    public void playAnimation(ModelAnimation animation) {
        this.playAnimation(animation, DEFAULT_PRIORITY, DEFAULT_TRANSITION_TIME);
    }

    public void playAnimation(ModelAnimation animation, int priority, int transitionTicks) {
        if (this.taskId == 0) {
            this.taskId = this.animator.animate(this);
        }
        this.animationQueue.pushAnimation(animation, priority, transitionTicks);
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

}