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
package team.unnamed.hephaestus.view.modifier.player.rig;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.ItemTransform;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTextures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class ResourcePlayerRigWriter implements PlayerRigWriter<ResourcePack> {
    private static final Key PLAYER_HEAD_KEY = Key.key("minecraft", "item/player_head");
    private static final Key TEMPLATE_SKULL_KEY = Key.key("minecraft", "item/template_skull");

    private final PlayerRig rig;

    public ResourcePlayerRigWriter(final @NotNull PlayerRig rig) {
        this.rig = requireNonNull(rig, "rig");
    }

    @Override
    public void write(ResourcePack resourcePack) {
        final List<ItemOverride> overrides = new ArrayList<>();

        // write overrides
        for (final PlayerBoneType boneType : rig.types()) {
            final var hasSlimVariant = boneType.modelData() != boneType.slimModelData();

            // Write normal variant
            final var key = Key.key("custom/entities/player/" + boneType.boneName());
            resourcePack.model(Model.model()
                    .key(key)
                    .parent(Model.BUILT_IN_ENTITY)
                    .display(new HashMap<>() {{
                        put(ItemTransform.Type.THIRDPERSON_LEFTHAND, ItemTransform.transform(
                                boneType.rotation(),
                                boneType.translation(),
                                boneType.scale()
                        ));
                    }})
                    .textures(ModelTextures.builder().build())
                    .build());
            overrides.add(ItemOverride.of(key, ItemPredicate.customModelData(boneType.modelData())));

            // Write slim variant, if this bone type has it
            if (hasSlimVariant) {
                final var slimKey = Key.key("custom/entities/player/slim_" + boneType.boneName());
                resourcePack.model(Model.model()
                        .key(slimKey)
                        .parent(Model.BUILT_IN_ENTITY)
                        .display(new HashMap<>() {{
                            put(ItemTransform.Type.THIRDPERSON_LEFTHAND, ItemTransform.transform(
                                    boneType.rotation(),
                                    boneType.slimTranslation(),
                                    boneType.slimScale()
                            ));
                        }})
                        .textures(ModelTextures.builder().build())
                        .build());
                overrides.add(ItemOverride.of(slimKey, ItemPredicate.customModelData(boneType.slimModelData())));
            }
        }

        // Sort overrides by custom model data
        overrides.sort(Comparator.comparingInt(override -> ((int) override.predicate().get(0).value())));

        // override player head model
        resourcePack.model(Model.model()
                .key(PLAYER_HEAD_KEY)
                .parent(TEMPLATE_SKULL_KEY)
                .overrides(overrides)
                .textures(ModelTextures.builder().build())
                .build());

        // copy our shaders
        resourcePack.unknownFile("assets/minecraft/shaders/core/rendertype_entity_translucent.fsh", rig.fragmentShader());
        resourcePack.unknownFile("assets/minecraft/shaders/core/rendertype_entity_translucent.vsh", rig.vertexShader());
    }
}