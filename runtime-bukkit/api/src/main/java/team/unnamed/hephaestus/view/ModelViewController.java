package team.unnamed.hephaestus.view;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;

public interface ModelViewController {

    void show(BukkitModelView view);

    void hide(BukkitModelView view);

    void teleport(BukkitModelView view, Location location);

    /**
     * Colorizes the entity with the given {@code color}
     */
    void colorize(BukkitModelView view, Color color);

    /**
     * Colorizes the {@code bone} for the specified
     * {@code view} using the provided {@code color}
     */
    void colorizeBone(BukkitModelView view, String boneName, Color color);

    void teleportBone(BukkitModelView view, String boneName, Location location);

    void updateBoneModelData(BukkitModelView view, Bone bone, int modelData);

    /**
     * Sets the bone armor stand head pose
     *
     * @param view The model view to update
     * @param boneName The model bone name
     * @param angle The new angle, in degrees
     */
    void setBonePose(BukkitModelView view, String boneName, Vector3Float angle);

    void showIndividually(BukkitModelView view, Player player);

    void hideIndividually(BukkitModelView view, Player player);

}