package team.unnamed.hephaestus.model;

import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.List;

/**
 * It's a model cube holder, a {@link ModelCube}
 * composite, util to make rotations over the pivot
 */
public class ModelBone implements ModelComponent {

    @Nullable private final ModelBone parent;
    private final String name;
    private final Vector3Float pivot;
    private final Vector3Float rotation;

    private final List<ModelBone> bones;
    private List<ModelCube> cubes;

    // cached pivot divided by 16, used to compute the
    // offset, that's divided by 16 too
    private final Vector3Float scaledPivot;
    private Vector3Float offset;

    private int customModelData;

    public ModelBone(
            @Nullable ModelBone parent,
            String name,
            Vector3Float pivot,
            Vector3Float rotation,
            List<ModelBone> bones,
            List<ModelCube> cubes
    ) {
        this.parent = parent;
        this.name = name;
        this.pivot = pivot;
        this.rotation = rotation;
        this.bones = bones;
        this.cubes = cubes;
        this.scaledPivot = pivot.divide(16);
        this.offset = Vector3Float.ZERO;
    }

    @Nullable
    public ModelBone getParent() {
        return parent;
    }

    public Vector3Float getOffset() {
        return offset;
    }

    public void computeOffsets(Vector3Float offset) {
        this.offset = scaledPivot.subtract(offset);
        for (ModelBone bone : bones) {
            bone.computeOffsets(scaledPivot);
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

    public void discardResourcePackData() {
        this.cubes = null;
    }

}