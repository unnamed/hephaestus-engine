package team.unnamed.hephaestus.model.animation;

import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.struct.Vector3Float;

public interface FrameProvider {

     Vector3Float providePosition(float tick, ModelAnimation animation, ModelBone bone);

     EulerAngle provideRotation(float tick, ModelAnimation animation, ModelBone bone);

}