package team.unnamed.hephaestus;

import java.util.Collection;

/**
 * Abstraction for any class that holds bones
 * assets, like a ModelAsset or a ModelBoneAsset
 */
public interface BoneHolder {

    /**
     * Returns the bones that this
     * instance holds
     */
    Collection<ModelBoneAsset> getBones();

}
