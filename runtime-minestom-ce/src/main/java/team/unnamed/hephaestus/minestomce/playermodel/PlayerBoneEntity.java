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
package team.unnamed.hephaestus.minestomce.playermodel;

import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.PlayerHeadMeta;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.minestomce.BoneEntity;
import team.unnamed.hephaestus.player.PlayerBoneType;
import team.unnamed.hephaestus.player.PlayerModel;
import team.unnamed.hephaestus.player.Skin;
import team.unnamed.hephaestus.util.Quaternion;

import java.util.UUID;

public class PlayerBoneEntity extends BoneEntity {

    private static final ItemStack BASE_HEAD =
            ItemStack.builder(Material.PLAYER_HEAD)
                    .meta(new PlayerHeadMeta.Builder()
                            .skullOwner(UUID.fromString("34097e46-c233-c03c-d8b9-aee154c9946"))
                            .build())
                    .build();

    private Skin skin;
    private PlayerBoneType boneType;

    public PlayerBoneEntity(
            PlayerModelEntity view,
            Bone bone,
            Vector3Float initialPosition,
            Quaternion parentRotation,
            float scale
    ) {
        super(view, bone, initialPosition, parentRotation, scale);
    }

    @Override
    protected void initialize(Vector3Float initialPosition, Quaternion initialRotation) {
        this.skin = ((PlayerModel) view.model()).skin();
        this.boneType = PlayerBoneType.matchFor(skin, bone.name());

        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.THIRD_PERSON_LEFT_HAND);
        meta.setInterpolationDuration(3);
        meta.setViewRange(0.6f);
        meta.setHasNoGravity(true);

        meta.setItemStack(BASE_HEAD.withMeta(PlayerHeadMeta.class, itemMeta -> {
            itemMeta.playerSkin(new PlayerSkin(skin.value(), skin.signature()));
            itemMeta.customModelData(boneType.modelData());
        }));

        update(initialPosition, initialRotation, Vector3Float.ONE);
    }

    @Override
    public void update(Vector3Float position, Quaternion rotation, Vector3Float scale) {
        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setInterpolationStartDelta(0);

        meta.setTranslation(new Pos(position.x(), position.y(), position.z())
                .mul(modelScale)
                .add(0, boneType.offset(), 0)
        );

        meta.setRightRotation(rotation.toFloatArray());
        meta.setScale(new Vec(
                modelScale * scale.x(),
                modelScale * scale.y(),
                modelScale * scale.z()
        ));

        meta.setNotifyAboutChanges(true);
    }

    @Override
    public void colorize(Color color) {
        //empty method
    }
}
