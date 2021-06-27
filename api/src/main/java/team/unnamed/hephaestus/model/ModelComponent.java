package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.struct.Vector3Float;

/**
 * Interface that represents a model component,
 * it can be a bone or a cube
 */
public interface ModelComponent {

    /**
     * Gets the pivot of the model component,
     */
    Vector3Float getPivot();

}