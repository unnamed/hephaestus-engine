package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Model {

    private final String name;
    private final ModelGeometry geometry;
    private final Map<String, ModelAnimation> animations;
    private final List<File> textures;

    public Model(String name, ModelGeometry geometry, Map<String, ModelAnimation> animations, List<File> textures) {
        this.name = name;
        this.geometry = geometry;
        this.animations = animations;
        this.textures = textures;
    }

    public String getName() {
        return name;
    }

    public ModelGeometry getGeometry() {
        return geometry;
    }

    public Map<String, ModelAnimation> getAnimations() {
        return animations;
    }

    public List<File> getTextureFiles() {
        return textures;
    }
}