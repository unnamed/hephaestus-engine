package team.unnamed.hephaestus.model;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Main model class representing the
 * entire model geometry. Format is
 * similar to the Bedrock Model Geometries
 */
public class ModelGeometry {

    private final List<ModelBone> bones;
    private final List<ModelBoneAsset> bonesAssets;

    private final Map<Integer, String> textureMap;

    public ModelGeometry(
            List<ModelBone> bones,
            List<ModelBoneAsset> bonesAssets,
            Map<Integer, String> textureMap
    ) {
        this.bones = bones;
        this.bonesAssets = bonesAssets;
        this.textureMap = textureMap;
    }

    public List<ModelBone> getBones() {
        return bones;
    }

    public List<ModelBoneAsset> getBonesAssets() {
        return bonesAssets;
    }

    @Nullable
    public Map<Integer, String> getTextureMap() {
        return textureMap;
    }

}