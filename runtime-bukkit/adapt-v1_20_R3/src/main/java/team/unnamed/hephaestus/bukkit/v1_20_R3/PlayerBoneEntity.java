/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.hephaestus.bukkit.v1_20_R3;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.minecraft.world.item.ItemDisplayContext;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.joml.Vector3f;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.player.PlayerBoneType;
import team.unnamed.hephaestus.player.PlayerModel;
import team.unnamed.hephaestus.player.Skin;
import team.unnamed.hephaestus.util.Quaternion;

final class PlayerBoneEntity
        extends BoneEntity {

    private PlayerBoneType boneType;

    public PlayerBoneEntity(MinecraftModelEntity view, Bone bone,
                            Vector3Float initialPosition,
                            Quaternion initialRotation,
                            float modelScale
    ) {
        super(view, bone, initialPosition, initialRotation, modelScale);
    }

    @Override
    protected void initialize(Vector3Float initialPosition, Quaternion initialRotation) {
        PlayerModel model = (PlayerModel) view.model();
        Skin skin = model.skin();
        this.boneType = model.boneTypeOf(bone.name());

        setItemTransform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        setTransformationInterpolationDuration(3);
        setViewRange(0.6f);
        setNoGravity(false);

        update(initialPosition, initialRotation, Vector3Float.ONE);

        var item = new ItemStack(Material.PLAYER_HEAD);
        var meta = (SkullMeta) item.getItemMeta();

        PlayerProfile profile = Bukkit.createProfile(uuid);
        profile.setProperty(new ProfileProperty("textures", skin.value(), skin.signature()));
        meta.setPlayerProfile(profile);
        meta.setCustomModelData(boneType.modelData());
        item.setItemMeta(meta);

        var nmsItem = CraftItemStack.asNMSCopy(item);

        setItemStack(nmsItem);
        initialData = super.getEntityData().packDirty();
    }

    @Override
    protected Vector3f modifyTranslation(Vector3f translation) {
        return translation.add(0, boneType.offset(), 0);
    }
}
