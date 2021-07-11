package team.unnamed.hephaestus.resourcepack.java;

import team.unnamed.hephaestus.struct.Vector3Float;

public class JavaRotation {

    private final String axis;
    private final float angle;
    private Vector3Float origin;

    public JavaRotation(String axis, float angle, Vector3Float origin) {
        this.axis = axis;
        this.angle = angle;
        this.origin = origin;
    }

    public void addOrigin(float... offset) {
        this.origin = new Vector3Float(
                this.origin.getX() + offset[0],
                this.origin.getY() + offset[1],
                this.origin.getZ() + offset[2]
        );
    }
}