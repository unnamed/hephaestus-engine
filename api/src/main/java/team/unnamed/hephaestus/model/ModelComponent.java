package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.struct.Vector3Float;

/**
 * Interface that represents a model component,
 * it can be a bone or a cube
 */
public interface ModelComponent {

    /**
     * Gets the origin of the model component,
     * it's the pivot for the bones, and the
     * origin for the cubes
     */
    Vector3Float getOrigin();

}
