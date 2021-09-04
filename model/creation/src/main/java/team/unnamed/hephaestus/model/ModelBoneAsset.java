package team.unnamed.hephaestus.model;

import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.List;

public class ModelBoneAsset implements BoneHolder {

    private final String name;
    private final Vector3Float pivot;
    private final int customModelData;
    private final List<ModelCube> cubes;
    private final List<ModelBoneAsset> bones;

    public ModelBoneAsset(
            String name,
            Vector3Float pivot,
            int customModelData,
            List<ModelCube> cubes,
            List<ModelBoneAsset> bones
    ) {
        this.name = name;
        this.pivot = pivot;
        this.customModelData = customModelData;
        this.cubes = cubes;
        this.bones = bones;
    }

    public String getName() {
        return name;
    }

    public Vector3Float getPivot() {
        return pivot;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public List<ModelCube> getCubes() {
        return cubes;
    }

    @Override
    public List<ModelBoneAsset> getBones() {
        return bones;
    }
}
