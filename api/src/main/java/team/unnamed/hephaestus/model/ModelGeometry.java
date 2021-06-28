package team.unnamed.hephaestus.model;

import java.util.List;
import java.util.Map;

/**
 * Main model class representing the
 * entire model geometry. Format is
 * similar to the Bedrock Model Geometries
 */
public class ModelGeometry {

    private final ModelDescription description;
    private final List<ModelBone> bones;
    private final Map<Integer, String> textureMap;

    public ModelGeometry(ModelDescription description, List<ModelBone> bones, Map<Integer, String> textureMap) {
        this.description = description;
        this.bones = bones;
        this.textureMap = textureMap;
    }

    public ModelDescription getDescription() {
        return description;
    }

    public List<ModelBone> getBones() {
        return bones;
    }

    public Map<Integer, String> getTextureMap() {
        return textureMap;
    }

}