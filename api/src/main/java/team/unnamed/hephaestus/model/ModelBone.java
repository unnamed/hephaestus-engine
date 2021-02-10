package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.List;

/**
 * It's a model cube holder, a {@link ModelCube}
 * composite, util to make rotations over the pivot
 */
public class ModelBone implements ModelComponent {

    private final String name;
    private final Vector3Float origin;
    private final List<ModelComponent> components;

    public ModelBone(String name, Vector3Float origin, List<ModelComponent> components) {
        this.name = name;
        this.origin = origin;
        this.components = components;
    }

    public String getName() {
        return name;
    }

    @Override
    public Vector3Float getOrigin() {
        return origin;
    }

    public List<ModelComponent> getComponents() {
        return components;
    }
}
