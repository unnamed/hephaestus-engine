package team.unnamed.hephaestus.model;

import java.util.List;

/**
 * Abstraction for any class that holds bones
 * assets, like a ModelAsset or a ModelBoneAsset
 */
public interface BoneHolder {

    /**
     * Returns the bones that this
     * instance holds
     */
    List<ModelBoneAsset> getBones();

}
