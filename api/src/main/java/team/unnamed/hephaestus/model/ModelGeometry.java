package team.unnamed.hephaestus.model;

import java.util.List;

/**
 * Main model class representing the
 * entire model geometry. Format is
 * similar to the Bedrock Model Geometries
 */
public class ModelGeometry {

    private final ModelDescription description;
    private final List<ModelBone> bones;

    public ModelGeometry(ModelDescription description, List<ModelBone> bones) {
        this.description = description;
        this.bones = bones;
    }

    public ModelDescription getDescription() {
        return description;
    }

    public List<ModelBone> getBones() {
        return bones;
    }

}