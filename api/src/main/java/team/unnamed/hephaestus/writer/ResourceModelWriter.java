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
package team.unnamed.hephaestus.writer;

import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.model.Element;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.ItemTransform;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;
import team.unnamed.creative.texture.Texture;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.partial.ElementAsset;
import team.unnamed.hephaestus.partial.ModelAsset;
import team.unnamed.hephaestus.partial.BoneAsset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ModelWriter} that writes
 * {@link Model} instances to a {@link ResourcePack}, which
 * represents a resource pack
 *
 * @since 1.0.0
 */
final class ResourceModelWriter implements ModelWriter<ResourcePack> {

    private static final Key LEATHER_HORSE_ARMOR_KEY = Key.key("item/leather_horse_armor");
    private static final String DEFAULT_NAMESPACE = "hephaestus";

    @Subst(DEFAULT_NAMESPACE) private final String namespace;

    ResourceModelWriter(@Subst(DEFAULT_NAMESPACE) String namespace) {
        this.namespace = namespace;

        // validate namespace
        Key.key(namespace, "dummy");
    }

    ResourceModelWriter() {
        this(DEFAULT_NAMESPACE);
    }

    private void writeBones(
            ResourcePack resourcePack,
            ModelAsset model,
            Collection<ItemOverride> overrides,
            Collection<BoneAsset> assets
    ) {
        for (BoneAsset bone : assets) {
            @Subst("model/bone") String path = model.name() + '/' + bone.name();
            Key key = Key.key(namespace, path);

            overrides.add(ItemOverride.of(
                    key,
                    ItemPredicate.customModelData(bone.customModelData())
            ));

            resourcePack.model(toCreative(key, model, bone));

            // write children
            writeBones(resourcePack, model, overrides, bone.children());
        }
    }

    /**
     * Transfers the resource pack information to the
     * given {@code output}
     */
    @Override
    public void write(ResourcePack resourcePack, Collection<Model> models) {
        List<ItemOverride> overrides = new ArrayList<>();
        List<AtlasSource> sources = new ArrayList<>();

        for (Model model : models) {
            ModelAsset asset = model.asset();

            if (asset == null) {
                throw new IllegalArgumentException("Model '"
                        + model.name() + "' does not have a model asset," +
                        " resource pack data already discarded?");
            }

            // write textures from this model
            for (Map.Entry<String, Writable> texture : asset.textures().entrySet()) {
                @Subst("model/texture") String path = model.name() + '/' + texture.getKey();

                // write the texture
                resourcePack.texture(Texture.builder()
                        .key(Key.key(namespace, path))
                        .data(texture.getValue())
                        .build()
                );
            }

            // write all the model bones
            writeBones(resourcePack, asset, overrides, asset.bones());
            sources.add(AtlasSource.directory(model.name(), model.name() + "/"));
        }

        // sort overrides comparing by customModelData
        overrides.sort(Comparator.comparing(override -> {
            ItemPredicate predicate = override.predicate().get(0);
            return (Integer) predicate.value();
        }));

        resourcePack.model(team.unnamed.creative.model.Model.builder()
                .key(LEATHER_HORSE_ARMOR_KEY)
                .parent(team.unnamed.creative.model.Model.ITEM_HANDHELD)
                .textures(ModelTextures.builder()
                        .layers(Collections.singletonList(
                                ModelTexture.ofKey(LEATHER_HORSE_ARMOR_KEY)
                        ))
                        .build())
                .overrides(overrides)
                .build()
        );

        resourcePack.atlas(Atlas.builder()
                .key(Atlas.BLOCKS)
                .sources(sources)
                .build()
        );
    }

    /**
     * Converts a {@link BoneAsset} (a representation of a model
     * bone) to a resource-pack ready {@link team.unnamed.creative.model.Model}
     * object
     *
     * @param model The model holding the given bone
     * @param bone The bone to be converted
     */
    private team.unnamed.creative.model.Model toCreative(
            Key key,
            ModelAsset model,
            BoneAsset bone
    ) {
        Map<ItemTransform.Type, ItemTransform> displays = new HashMap<>();
        displays.put(ItemTransform.Type.THIRDPERSON_LEFTHAND, ItemTransform.builder()
                .scale(new Vector3Float(bone.scale(), bone.scale(), bone.scale()))
                .build()
        );
        Map<String, ModelTexture> textureMappings = new HashMap<>();
        model.textureMapping().forEach((id, texturePath) -> {
            @Subst("model/texture") String path = model.name() + '/' + withoutExtension(texturePath);
            textureMappings.put(id.toString(), ModelTexture.ofKey(Key.key(namespace, path)));
        });

        final List<Element> elements = new ArrayList<>(bone.cubes().size());
        for (final ElementAsset elementAsset : bone.cubes()) {
            elements.add(
                    Element.builder()
                            .from(elementAsset.from())
                            .to(elementAsset.to())
                            .rotation(elementAsset.rotation())
                            .faces(elementAsset.faces())
                            .build()
            );
        }

        return team.unnamed.creative.model.Model.builder()
                .key(key)
                .display(displays)
                .textures(ModelTextures.builder()
                        .variables(textureMappings)
                        .build())
                .elements(elements)
                .build();
    }

    private static String withoutExtension(final String file) {
        int dotIndex = file.lastIndexOf('.');
        return (dotIndex == -1) ? file : file.substring(0, dotIndex);
    }

}