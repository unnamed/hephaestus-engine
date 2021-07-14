package team.unnamed.hephaestus.model;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Main model class representing the
 * entire model geometry. Format is
 * similar to the Bedrock Model Geometries
 */
public class ModelGeometry {

    private final List<ModelBone> bones;
    private ModelDescription description;
    private Map<Integer, String> textureMap;

    public ModelGeometry(ModelDescription description, List<ModelBone> bones, Map<Integer, String> textureMap) {
        this.description = description;
        this.bones = bones;
        this.textureMap = textureMap;
    }

    @Nullable
    public ModelDescription getDescription() {
        return description;
    }

    public List<ModelBone> getBones() {
        return bones;
    }

    @Nullable
    public Map<Integer, String> getTextureMap() {
        return textureMap;
    }

    /**
     * Discards the information used only for
     * resource pack generation from this geometry
     * instance
     */
    public void discardResourcePackData() {
        this.description = null;
        this.textureMap = null;
        bones.forEach(ModelBone::discardResourcePackData);
    }

}