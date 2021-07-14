package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.List;

/**
 * It's a model cube holder, a {@link ModelCube}
 * composite, util to make rotations over the pivot
 */
public class ModelBone implements ModelComponent {

    private final String name;
    private final Vector3Float pivot;
    private final Vector3Float rotation;

    private final List<ModelBone> bones;
    private final List<ModelCube> cubes;

    private final Vector3Float globalOffset;
    private Vector3Float localOffset;

    private int customModelData;

    public ModelBone(String name, Vector3Float pivot, Vector3Float rotation, List<ModelBone> bones, List<ModelCube> cubes) {
        this.name = name;
        this.pivot = pivot;
        this.rotation = rotation;
        this.bones = bones;
        this.cubes = cubes;
        this.globalOffset = pivot.divide(16);
        this.localOffset = Vector3Float.ZERO;
    }

    public Vector3Float getGlobalOffset() {
        return globalOffset;
    }

    public Vector3Float getLocalOffset() {
        return localOffset;
    }

    public void setRelativeOffset(Vector3Float offset) {
        this.localOffset = new Vector3Float(
                globalOffset.getX() - offset.getX(),
                globalOffset.getY() - offset.getY(),
                globalOffset.getZ() - offset.getZ()
        );
    }

    public void updateChildRelativeOffset() {
        for (ModelBone bone : bones) {
            bone.setRelativeOffset(globalOffset);
            bone.updateChildRelativeOffset();
        }
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public String getName() {
        return name;
    }

    @Override
    public Vector3Float getPivot() {
        return pivot;
    }

    public Vector3Float getRotation() {
        return rotation;
    }

    public List<ModelBone> getBones() {
        return bones;
    }

    public List<ModelCube> getCubes() {
        return cubes;
    }

}