package team.unnamed.hephaestus.model;

import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.util.Map;

public class Model {

    private final String name;
    private final Map<String, ModelAnimation> animations;
    private final ModelGeometry geometry;

    /**
     * Map containing the model textures, it must
     * be discarded after resource pack generation
     * TODO: Exposing invalid values is a bit inconsistent
     */
    private Map<String, Streamable> textures;

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

    @Nullable
    public Map<String, Streamable> getTextures() {
        return textures;
    }

    /**
     * Discards the information used only in
     * the resource pack generation in this
     * model instance
     */
    public void discardResourcePackData() {
        this.textures = null;
        this.geometry.discardResourcePackData();
    }

}