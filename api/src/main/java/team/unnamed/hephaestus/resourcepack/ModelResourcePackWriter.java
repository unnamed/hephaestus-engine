/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
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
package team.unnamed.hephaestus.resourcepack;

import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.texture.Texture;
import team.unnamed.hephaestus.partial.ModelAsset;
import team.unnamed.hephaestus.partial.BoneAsset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ModelResourcePackWriter {

    private final Collection<ModelAsset> models;
    private final String namespace;

    public ModelResourcePackWriter(
            Collection<ModelAsset> models,
            String namespace
    ) {
        this.models = models;
        this.namespace = namespace;
    }

    public ModelResourcePackWriter(Collection<ModelAsset> models) {
        this(models, "hephaestus");
    }

    private void writeBoneCubes(
            FileTree tree,
            ModelAsset model,
            Collection<ItemOverride> overrides,
            Collection<BoneAsset> assets
    ) {
        for (BoneAsset bone : assets) {

            Key modelKey = Key.key(namespace, model.name() + '/' + bone.name());
            Model creativeModel = ModelGeometryTransformer.toCreative(modelKey, model, bone);

            overrides.add(ItemOverride.of(
                    modelKey,
                    ItemPredicate.customModelData(bone.customModelData())
            ));

            tree.write(creativeModel);

            // write children
            writeBoneCubes(tree, model, overrides, bone.bones());
        }
    }

    /**
     * Transfers the resource pack information to the
     * given {@code output}
     */
    public void write(FileTree tree) throws IOException {

        List<ItemOverride> overrides = new ArrayList<>();

        for (ModelAsset model : models) {
            for (Map.Entry<String, Writable> texture : model.textures().entrySet()) {
                String textureName = texture.getKey();
                Writable data = texture.getValue();

                Key key = Key.key(namespace, model.name() + '/' + textureName);

                // write the texture
                tree.write(Texture.builder()
                        .key(key)
                        .data(data)
                        .build());
            }
            // write all the model bones
            writeBoneCubes(tree, model, overrides, model.bones());
        }

        // sort overrides comparing by customModelData
        overrides.sort(Comparator.comparing(override -> {
            ItemPredicate predicate = override.predicate().get(0);
            return (Integer) predicate.value();
        }));

        Model leatherHorseArmorModel = Model.builder()
                .key(Key.key("item/leather_horse_armor"))
                .parent(Model.ITEM_HANDHELD)
                .textures(ModelTexture.builder()
                        .layers(Collections.singletonList(
                                Key.key("item/leather_horse_armor")
                        ))
                        .build())
                .overrides(overrides)
                .build();

        tree.write(leatherHorseArmorModel);
    }

}