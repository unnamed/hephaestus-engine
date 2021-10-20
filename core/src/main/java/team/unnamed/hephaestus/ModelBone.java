package team.unnamed.hephaestus;

import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.Collection;
import java.util.Map;

/**
 * It's a model cube holder, a {@link ModelCube}
 * composite, util to make rotations over the pivot
 */
public class ModelBone {

    @Nullable private final ModelBone parent;
    private final String name;
    private final Vector3Float rotation;

    private final Map<String, ModelBone> bones;
    private final Vector3Float offset;

    private final int customModelData;

    public ModelBone(
            @Nullable ModelBone parent,
            String name,
            Vector3Float rotation,
            Map<String, ModelBone> bones,
            Vector3Float offset,
            int customModelData
    ) {
        this.parent = parent;
        this.name = name;
        this.rotation = rotation;
        this.bones = bones;
        this.offset = offset;
        this.customModelData = customModelData;
    }

    @Nullable
    public ModelBone getParent() {
        return parent;
    }

    public Vector3Float getOffset() {
        return offset;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public String getName() {
        return name;
    }

    public Vector3Float getRotation() {
        return rotation;
    }

    public Collection<ModelBone> getBones() {
        return bones.values();
    }

    public Map<String, ModelBone> getBoneMap() {
        return bones;
    }

}