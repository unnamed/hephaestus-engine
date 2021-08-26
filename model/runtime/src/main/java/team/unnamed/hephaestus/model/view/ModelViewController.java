package team.unnamed.hephaestus.model.view;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.struct.Vector3Double;

public interface ModelViewController {

    void show(ModelView view);

    void hide(ModelView view);

    void teleport(ModelView view, Location location);

    /**
     * Colorizes the entity with the given {@code color}
     */
    void colorize(ModelView view, Color color);

    void teleportBone(ModelView view, ModelBone bone, Location location);

    void updateBoneModelData(ModelView view, ModelBone bone, int modelData);

    void setBonePose(ModelView view, ModelBone bone, Vector3Double angle);

    void showIndividually(ModelView view, Player player);

    void hideIndividually(ModelView view, Player player);

}