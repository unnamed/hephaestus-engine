package team.unnamed.hephaestus.model.view;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelAnimationQueue;
import team.unnamed.hephaestus.struct.Vector3Double;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BukkitModelView implements ModelView {

    private final static int DEFAULT_PRIORITY = 0;
    private final static int DEFAULT_TRANSITION_TIME = 8;

    private final ModelViewController controller;
    private final ModelViewAnimator animator;
    private final ModelAnimationQueue animationQueue;

    private final Model model;

    private final Collection<Player> viewers;

    private Location location;

    private int taskId;

    /**
     * Holds a relation of model bone name as key
     * and its linked entity ID as value
     */
    private final Map<String, Object> entities = new HashMap<>();

    public BukkitModelView(
            ModelViewController controller,
            ModelViewAnimator animator,
            ModelAnimationQueue animationQueue,
            Model model,
            Collection<Player> viewers,
            Location location
    ) {
        this.controller = controller;
        this.animator = animator;
        this.animationQueue = animationQueue;

        this.model = model;
        this.viewers = viewers;
        this.location = location;

        this.taskId = -1;
    }

    public ModelAnimationQueue getAnimationQueue() {
        return animationQueue;
    }

    public Model getModel() {
        return model;
    }

    public Collection<Player> getViewers() {
        return viewers;
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

    @Override
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

    @Override
    public void playAnimation(ModelAnimation animation) {
        this.playAnimation(animation, DEFAULT_PRIORITY, DEFAULT_TRANSITION_TIME);
    }

    public void playAnimation(ModelAnimation animation, int priority, int transitionTicks) {
        if (this.taskId == -1) {
            this.taskId = this.animator.animate(this);
        }
        this.animationQueue.pushAnimation(animation, priority, transitionTicks);
    }

    @Override
    public boolean stopAnimation(String name) {
        this.animationQueue.removeAnimation(name);

        if (this.animationQueue.getQueuedAnimations().isEmpty() && this.taskId != -1) {
            Bukkit.getScheduler().cancelTask(this.taskId);
            this.taskId = -1;
        }

        // TODO:
        return true;
    }

    @Override
    public void stopAllAnimations() {
        this.animationQueue.clear();

        if (this.taskId != -1) {
            Bukkit.getScheduler().cancelTask(this.taskId);
            this.taskId = -1;
        }
    }

    public void animate() {
        if (this.taskId == -1) {
            this.taskId = this.animator.animate(this);
        }
    }

    public void colorize(Color color) {
        controller.colorize(this, color);
    }

    @Override
    public void colorize(int r, int g, int b) {
        controller.colorize(this, Color.fromRGB(r, g, b));
    }

    public void colorizeBone(ModelBone bone, Color color) {
        controller.colorizeBone(this, bone.getName(), color);
    }

    public void colorizeBone(String boneName, Color color) {
        controller.colorizeBone(this, boneName, color);
    }

    @Override
    public void colorizeBone(String name, int r, int g, int b) {
        controller.colorizeBone(this, name, Color.fromRGB(r, g, b));
    }

    public void show() {
        controller.show(this);
    }

    public void addViewer(Player player) {
        if (viewers.add(player)) {
            controller.showIndividually(this, player);
        }
    }

    public void removeViewer(Player player) {
        if (viewers.remove(player)) {
            controller.hideIndividually(this, player);
        }
    }

    public void hide() {
        controller.hide(this);

        if (this.taskId != -1) {
            Bukkit.getScheduler().cancelTask(this.taskId);
            this.taskId = -1;
        }
    }

    public void teleport(Location newLocation) {
        this.location = newLocation.clone();

        if (this.taskId == -1) {
            controller.teleport(this, this.location);
        }
    }

    public void setBonePose(ModelBone bone, Vector3Double angle) {
        controller.setBonePose(this, bone, angle);
    }

    public void updateBoneModelData(ModelBone bone, int modelData) {
        controller.updateBoneModelData(this, bone, modelData);
    }

    public void teleportBone(ModelBone bone, Location location) {
        controller.teleportBone(this, bone, location);
    }

}