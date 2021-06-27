package team.unnamed.hephaestus.resourcepack.java;

import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.ModelMath;

import java.util.Map;

public class JavaCube {

    private final String name;
    private Vector3Float from;
    private Vector3Float to;
    private final JavaRotation rotation;
    private final Map<String, JavaFace> faces;

    public JavaCube(String name, Vector3Float from, Vector3Float to, JavaRotation rotation, Map<String, JavaFace> faces) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.rotation = rotation;
        this.faces = faces;
    }

    public String getName() {
        return name;
    }

    public Vector3Float getFrom() {
        return from;
    }

    public Vector3Float getTo() {
        return to;
    }

    public JavaRotation getRotation() {
        return rotation;
    }

    public Map<String, JavaFace> getFaces() {
        return faces;
    }

    public void addFrom(float... offsets) {
        this.from = new Vector3Float(
                this.from.getX() + offsets[0],
                this.from.getY() + offsets[1],
                this.from.getZ() + offsets[2]
        );

        if (
                this.from.getX() > 32.0F || this.from.getX() < -16.0F
                || this.from.getY() > 32.0F || this.from.getY() < -16.0F
                || this.from.getZ() > 32.0F || this.from.getZ()< -16.0F
        )
            throw new IllegalStateException("Part is not within 48x48x48 boundary");
    }

    public void addTo(float... offsets) {
        this.to = new Vector3Float(
                this.to.getX() + offsets[0],
                this.to.getY() + offsets[1],
                this.to.getZ() + offsets[2]
        );

        if (
                this.to.getX() > 32.0F || this.to.getX() < -16.0F
                || this.to.getY() > 32.0F || this.to.getY() < -16.0F
                || this.to.getZ() > 32.0F || this.to.getZ()< -16.0F
        )
            throw new IllegalStateException("Part is not within 48x48x48 boundary");
    }

    public void shrink() {
        this.from = new Vector3Float(
                ModelMath.shrink(from.getX()),
                ModelMath.shrink(from.getY()),
                ModelMath.shrink(from.getZ())
        );

        this.to = new Vector3Float(
                ModelMath.shrink(this.to.getX()),
                ModelMath.shrink(this.to.getY()),
                ModelMath.shrink(this.to.getZ())
        );
    }

}