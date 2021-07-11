package team.unnamed.hephaestus.model.view;

import org.bukkit.Location;
import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.model.ModelBone;

public interface ModelViewController {

    void teleport(ModelView view, Location location);

    void teleportBone(ModelView view, ModelBone bone, Location location);

    void setBonePose(ModelView view, ModelBone bone, EulerAngle angle);

}
