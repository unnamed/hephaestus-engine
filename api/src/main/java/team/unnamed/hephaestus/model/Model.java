package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.io.File;
import java.util.Map;

public class Model {

    private final String name;
    private final ModelGeometry geometry;
    private final Map<String, ModelAnimation> animations;
    private final File texture;

    public Model(String name, ModelGeometry geometry, Map<String, ModelAnimation> animations, File texture) {
        this.name = name;
        this.geometry = geometry;
        this.animations = animations;
        this.texture = texture;
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

    public File getTexture() {
        return texture;
    }

}