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

import net.kyori.adventure.key.Key;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.ItemTransform;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTextures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ResourcePlayerModelWriter implements PlayerModelWriter<ResourcePack> {
    private static final Key PLAYER_HEAD_KEY = Key.key("minecraft", "item/player_head");
    private static final Key TEMPLATE_SKULL_KEY = Key.key("minecraft", "item/template_skull");

    private final static ClassLoader CLASS_LOADER = ResourcePlayerModelWriter.class.getClassLoader();

    private final PlayerBoneType[] playerBoneTypes;
    private final Writable vshFile, fshFile;

    public ResourcePlayerModelWriter() {
        this(
                SimplePlayerBoneType.values(),
                Writable.resource(CLASS_LOADER, "playermodel/shaders/core/rendertype_entity_translucent.vsh"),
                Writable.resource(CLASS_LOADER, "playermodel/shaders/core/rendertype_entity_translucent.fsh")
        );
    }

    public ResourcePlayerModelWriter(PlayerBoneType[] playerBoneTypes, Writable vshFile, Writable fshFile) {
        this.playerBoneTypes = playerBoneTypes;
        this.vshFile = vshFile;
        this.fshFile = fshFile;
    }

    @Override
    public void write(ResourcePack resourcePack) {
        final List<ItemOverride> overrides = new ArrayList<>();

        // write overrides
        for (final PlayerBoneType boneType : playerBoneTypes) {
            final Key key = Key.key("custom/entities/player/" + boneType.boneName().toLowerCase());
            final Model model = Model.model()
                    .key(key)
                    .parent(Model.BUILT_IN_ENTITY)
                    .display(new HashMap<>() {{
                        put(ItemTransform.Type.THIRDPERSON_LEFTHAND, ItemTransform.transform(
                                boneType.rotation(),
                                boneType.translation(),
                                boneType.scale()
                        ));
                    }})
                    .textures(ModelTextures.builder().build()) // todo:
                    .build();

            resourcePack.model(model);

            overrides.add(ItemOverride.of(key, ItemPredicate.customModelData(boneType.modelData())));
        }

        // override player head model
        resourcePack.model(Model.model()
                .key(PLAYER_HEAD_KEY)
                .parent(TEMPLATE_SKULL_KEY)
                .overrides(overrides)
                .textures(ModelTextures.builder().build()) // todo:
                .build());

        // copy our shaders
        resourcePack.unknownFile("assets/minecraft/shaders/core/rendertype_entity_translucent.fsh", fshFile);
        resourcePack.unknownFile("assets/minecraft/shaders/core/rendertype_entity_translucent.vsh", vshFile);
    }
}