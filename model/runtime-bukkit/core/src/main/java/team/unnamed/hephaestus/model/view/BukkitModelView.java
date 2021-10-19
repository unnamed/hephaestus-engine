package team.unnamed.hephaestus.model.view;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.animation.AnimationQueue;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.struct.Vector3Double;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BukkitModelView implements ModelView {

    private final ModelViewController controller;
    private final AnimationQueue animationQueue;

    private final Model model;

    private final Collection<Player> viewers;

    private Location location;

    /**
     * Holds a relation of model bone name as key
     * and its linked entity ID as value
     */
    private final Map<String, Object> entities = new HashMap<>();

    public BukkitModelView(
            ModelViewController controller,
            Model model,
            Collection<Player> viewers,
            Location location
    ) {
        this.controller = controller;
        this.animationQueue = new AnimationQueue(this);

        this.model = model;
        this.viewers = viewers;
        this.location = location;
    }

    @Override
    public Model getModel() {
        return model;
    }

    //#region Entire View Handling methods
    @Override
    public void colorize(int r, int g, int b) {
        controller.colorize(this, Color.fromRGB(r, g, b));
    }

    public void colorize(Color color) {
        controller.colorize(this, color);
    }
    //#endregion

    //#region Bone Handling methods
    @Override
    public void colorizeBone(String name, int r, int g, int b) {
        controller.colorizeBone(this, name, Color.fromRGB(r, g, b));
    }

    public void colorizeBone(ModelBone bone, Color color) {
        controller.colorizeBone(this, bone.getName(), color);
    }

    public void colorizeBone(String boneName, Color color) {
        controller.colorizeBone(this, boneName, color);
    }

    @Override
    public void moveBone(String name, Vector3Float position) {
        controller.teleportBone(this, name, location.clone().add(
                position.getX(),
                position.getY(),
                position.getZ()
        ));
    }

    @Override
    public void rotateBone(String name, Vector3Double rotation) {
        controller.setBonePose(this, name, rotation);
    }
    //#endregion

    //#region Animation Handling methods
    @Override
    public void playAnimation(String name) {
        ModelAnimation animation = model.getAnimations().get(name);
        animationQueue.pushAnimation(animation);
    }

    @Override
    public void playAnimation(ModelAnimation animation) {
        animationQueue.pushAnimation(animation);
    }

    @Override
    public boolean stopAnimation(String name) {
        // TODO
        return false;
    }

    @Override
    public void stopAllAnimations() {
        animationQueue.removeAllAnimations();
    }

    @Override
    public void tickAnimations() {
        animationQueue.next(Math.toRadians(location.getYaw()));
    }
    //#endregion

    /**
     * Sets the location to the given {@code location}
     * <strong>This just updates the location in server
     * side, to update location to viewers, use
     * {@link BukkitModelView#teleport}</strong>
     */
    public void setLocation(Location location) {
        this.location = location.clone();
    }

    public Collection<Player> getViewers() {
        return viewers;
    }

    public Location getLocation() {
        return location;
    }

    public Map<String, Object> getEntities() {
        return entities;
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
    }

    public void teleport(Location newLocation) {
        this.location = newLocation.clone();
        controller.teleport(this, this.location);
    }

}