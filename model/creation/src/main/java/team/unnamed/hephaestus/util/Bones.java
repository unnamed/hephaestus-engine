package team.unnamed.hephaestus.util;

import team.unnamed.hephaestus.model.BoneHolder;
import team.unnamed.hephaestus.model.ModelBoneAsset;

import java.util.ArrayList;
import java.util.List;

public class Bones {

    public static List<ModelBoneAsset> getAllBones(BoneHolder holder) {
        List<ModelBoneAsset> bones = new ArrayList<>(holder.getBones());
        holder.getBones().forEach(bone -> getBoneBones(bones, bone));
        return bones;
    }

    private static void getBoneBones(List<ModelBoneAsset> bones, BoneHolder holder) {
        for (ModelBoneAsset component : holder.getBones()) {
            bones.add(component);
            getBoneBones(bones, component);
        }
    }

}
