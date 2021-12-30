package team.unnamed.hephaestus;

import team.unnamed.hephaestus.struct.Vector3Float;

public class ModelCubeRotation {

    private final Axis axis;
    private final Vector3Float origin;
    private final float angle;

    public ModelCubeRotation(
            Axis axis,
            Vector3Float origin,
            float angle
    ) {
        this.axis = axis;
        this.origin = origin;
        this.angle = angle;
    }

    public Axis getAxis() {
        return axis;
    }

    public Vector3Float getOrigin() {
        return origin;
    }

    public float getAngle() {
        return angle;
    }

    public enum Axis {
        X,
        Y,
        Z
    }

}
