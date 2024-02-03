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
package team.unnamed.hephaestus.player;

import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.asset.ModelAsset;

import java.util.Arrays;
import java.util.Map;

public class PlayerModel extends Model {

    private final Skin skin;
    private final PlayerBoneType[] playerBoneTypes;

    public PlayerModel(
            String name,
            Map<String, Bone> bones,
            Vector2Float boundingBox,
            ModelAsset asset,
            Map<String, Animation> animations,
            Skin skin,
            PlayerBoneType[] playerBoneTypes
    ) {
        super(name, bones, boundingBox, asset, animations);
        this.skin = skin;
        this.playerBoneTypes = playerBoneTypes;
    }

    public PlayerBoneType[] playerBoneTypes() {
        return playerBoneTypes;
    }

    public Skin skin() {
        return skin;
    }


    public @Nullable PlayerBoneType boneTypeOf(String boneName) {
        return Arrays.stream(playerBoneTypes)
                .filter(type -> type.boneName().equals(boneName)
                        && (skin.type() == Skin.Type.SLIM) == type.slim())
                .findAny()
                .orElse(null);
    }

    public static PlayerModel fromModel(Skin skin, Model model, PlayerBoneType[] playerBoneTypes) {
        return new PlayerModel(
                model.name(),
                model.boneMap(),
                model.boundingBox(),
                model.asset(),
                model.animations(),
                skin,
                playerBoneTypes
        );
    }
}
