package team.unnamed.hephaestus.view;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.AnimationController;
import team.unnamed.hephaestus.animation.ModelAnimation;
import team.unnamed.hephaestus.util.Vectors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class BukkitModelView
        implements ModelView<Player> {

    private final Model model;

    private final Map<String, BukkitBoneView> bones = new ConcurrentHashMap<>();
    private final Collection<BukkitBoneView> seats = new HashSet<>();

    private final ModelViewController controller;
    private final AnimationController animationController;
    private ModelInteractListener<Player> interactListener = ModelInteractListener.nop();

    private final Collection<Player> viewers = new HashSet<>();
    private Location location;

    public BukkitModelView(
            ModelViewController controller,
            Model model,
            Location location
    ) {
        this.controller = controller;
        this.animationController = AnimationController.create(this);

        this.model = model;
        this.location = location;
        this.summonBones();
    }

    private void summonBones() {
        // create the bone entities
        double yawRadians = Math.toRadians(location.getYaw());

        for (Bone bone : model.bones()) {
            summonBone(yawRadians, bone, Vector3Float.ZERO);
        }

        // after spawning bones, process seats
        for (Bone seatBone : model.seats()) {
            BukkitBoneView seatView = bone(seatBone.name());
            if (seatView != null) {
                seats.add(seatView);
            }
        }
    }

    private void summonBone(
            double yawRadians,
            Bone bone,
            Vector3Float parentPosition
    ) {
        Vector3Float position = bone.offset().add(parentPosition);

        BukkitBoneView entity = controller.createBone(this, bone);
        entity.position(Vectors.rotateAroundY(position, yawRadians));
        bones.put(bone.name(), entity);

        for (Bone child : bone.children()) {
            summonBone(yawRadians, child, position);
        }
    }

    @Override
    public void interactListener(ModelInteractListener<Player> interactListener) {
        this.interactListener = requireNonNull(interactListener, "interactListener");
    }

    @ApiStatus.Internal // we probably should make this public
    public Collection<BukkitBoneView> bones() {
        return bones.values();
    }

    @Override
    public AnimationController animationController() {
        return animationController;
    }

    @Override
    public Model model() {
        return model;
    }

    @Override
    public Collection<BukkitBoneView> seats() {
        return seats;
    }

    @Override
    public void colorize(int r, int g, int b) {
        for (BukkitBoneView view : bones.values()) {
            view.colorize(r, g, b);
        }
    }

    public void colorize(Color color) {
        for (BukkitBoneView view : bones.values()) {
            view.colorize(color);
        }
    }

    @Override
    public @Nullable BukkitBoneView bone(String name) {
        return bones.get(name);
    }

    @Override
    public void playAnimation(String name, int transitionTicks) {
        ModelAnimation animation = model.animations().get(name);
        animationController.queue(animation, transitionTicks);
    }

    @Override
    public void tickAnimations() {
        animationController.tick(Math.toRadians(location.getYaw()));
    }

    /**
     * Sets the location to the given {@code location}
     * <strong>This just updates the location in server
     * side, to update location to viewers, use
     * {@link BukkitModelView#teleport}</strong>
     */
    public void setLocation(Location location) {
        this.location = location.clone();
    }

    public Collection<Player> viewers() {
        return viewers;
    }

    public Location location() {
        return location;
    }

    /**
     * Adds the given {@link Player} as viewer if they
     * are not already viewers, and if they were not,
     * shows the view ({@link ModelViewController#show})
     *
     * @param player The added viewer
     * @return True if the player was added as viewer,
     * false otherwise (may because they are already
     * viewers)
     * @since 1.0.0
     */
    public boolean addViewer(Player player) {
        if (viewers.add(player)) {
            controller.show(this, player);
            return true;
        }
        return false;
    }

    /**
     * Removes the given {@link Player} as viewer if they
     * are viewers, and if they were, the view is hidden
     * ({@link ModelViewController#hide})
     *
     * @param player The removed viewer
     * @return True if the player was correctly removed
     * as viewers, false otherwise (may because they
     * were not viewers)
     * @since 1.0.0
     */
    public boolean removeViewer(Player player) {
        if (viewers.remove(player)) {
            controller.hide(this, player);
            return true;
        }
        return false;
    }

    private void teleportBoneAndChildren(
            double yawRadians,
            Bone bone,
            Vector3Float parentPosition
    ) {
        // location computing
        var position = bone.offset().add(parentPosition);
        var rotatedPosition = Vectors.rotateAroundY(position, yawRadians);

        var entity = bones.get(bone.name());
        entity.position(rotatedPosition);

        for (var child : bone.children()) {
            teleportBoneAndChildren(
                    yawRadians,
                    child,
                    position
            );
        }
    }

    public void teleport(Location newLocation) {
        this.location = newLocation.clone();
        double yawRadians = Math.toRadians(newLocation.getYaw());
        for (Bone bone : model.bones()) {
            teleportBoneAndChildren(yawRadians, bone, Vector3Float.ZERO);
        }
    }

}