package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.util.List;
import java.util.Map;

public class Model {

    private final String name;
    private final ModelGeometry geometry;
    private final Map<String, ModelAnimation> animations;
    private final Map<String, Streamable> textures;

    public Model(
            String name,
            ModelGeometry geometry,
            Map<String, ModelAnimation> animations,
            Map<String, Streamable> textures
    ) {
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

    public Map<String, Streamable> getTextures() {
        return textures;
    }
}