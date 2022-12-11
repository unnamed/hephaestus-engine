package team.unnamed.hephaestus;

import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.view.BaseModelView;

public interface ModelEngine {

    BaseModelView spawn(Model model, Vector3Float position, float yaw, float pitch, Object world);

}