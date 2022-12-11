package team.unnamed.hephaestus.minestom;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.util.Validate;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.ModelEngine;
import team.unnamed.hephaestus.view.BaseModelView;

public class MinestomModelEngine implements ModelEngine {

    public enum BoneType {
        ARMOR_STAND,
        AREA_EFFECT_CLOUD
    }

    public ModelEntity spawn(EntityType entityType, Model model, BoneType boneType) {
        return new ModelEntity(entityType, model, boneType);
    }

    public ModelEntity spawn(EntityType entityType, Model model, BoneType boneType, Pos position, Instance world) {
        ModelEntity modelEntity = new ModelEntity(entityType, model, boneType);
        modelEntity.setInstance(world, position);
        return modelEntity;
    }

    @Override
    public BaseModelView spawn(Model model, Vector3Float position, float yaw, float pitch, Object world) {
        Validate.isTrue(world instanceof Instance);
        return spawn(
                EntityType.ARMOR_STAND,
                model,
                BoneType.ARMOR_STAND,
                new Pos(position.x(), position.y(), position.z(), yaw, pitch),
                (Instance) world
        );
    }
}