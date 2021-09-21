package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.util.Collection;
import java.util.Map;

public class ModelAsset implements BoneHolder {

    private final String name;
    private final int textureWidth;
    private final int textureHeight;
    private final Map<String, Streamable> textures;
    private final Map<Integer, String> textureMapping;
    private final Map<String, ModelAnimation> animations;
    private final Map<String, ModelBoneAsset> bones;

    public ModelAsset(
            String name,
            int textureWidth,
            int textureHeight,
            Map<String, Streamable> textures,
            Map<Integer, String> textureMapping,
            Map<String, ModelBoneAsset> bones,
            Map<String, ModelAnimation> animations
    ) {
        this.name = name;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.textures = textures;
        this.textureMapping = textureMapping;
        this.bones = bones;
        this.animations = animations;
    }

    public String getName() {
        return name;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public Map<String, Streamable> getTextures() {
        return textures;
    }

    public Map<Integer, String> getTextureMapping() {
        return textureMapping;
    }

    public Map<String, ModelAnimation> getAnimations() {
        return animations;
    }

    @Override
    public Collection<ModelBoneAsset> getBones() {
        return bones.values();
    }

}
