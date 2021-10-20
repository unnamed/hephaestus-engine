package team.unnamed.hephaestus.view;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.ModelBone;
import team.unnamed.hephaestus.struct.Vector3Double;

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

    void updateBoneModelData(BukkitModelView view, ModelBone bone, int modelData);

    void setBonePose(BukkitModelView view, String boneName, Vector3Double angle);

    void showIndividually(BukkitModelView view, Player player);

    void hideIndividually(BukkitModelView view, Player player);

}