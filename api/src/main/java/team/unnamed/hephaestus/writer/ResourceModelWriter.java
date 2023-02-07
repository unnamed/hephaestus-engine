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
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.Element;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.ItemTransform;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.texture.Texture;
import team.unnamed.hephaestus.Model;
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
 * {@link Model} instances to a {@link FileTree}, which
 * represents a resource pack
 *
 * @since 1.0.0
 */
final class ResourceModelWriter implements ModelWriter<FileTree> {

    private static final Key LEATHER_HORSE_ARMOR_KEY = Key.key("item/leather_horse_armor");
    private static final String DEFAULT_NAMESPACE = "hephaestus";

    private static final Vector3Float SCALE = new Vector3Float(
            ItemTransform.MAX_SCALE,
            ItemTransform.MAX_SCALE,
            ItemTransform.MAX_SCALE
    );

    public static final float DISPLAY_TRANSLATION_Y = -6.4f;

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
            FileTree tree,
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

            tree.write(toCreative(key, model, bone));

            // write children
            writeBones(tree, model, overrides, bone.children());
        }
    }

    /**
     * Transfers the resource pack information to the
     * given {@code output}
     */
    @Override
    public void write(FileTree tree, Collection<Model> models) {

        List<ItemOverride> overrides = new ArrayList<>();

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
                tree.write(Texture.builder()
                        .key(Key.key(namespace, path))
                        .data(texture.getValue())
                        .build());
            }

            // write all the model bones
            writeBones(tree, asset, overrides, asset.bones());
        }

        // sort overrides comparing by customModelData
        overrides.sort(Comparator.comparing(override -> {
            ItemPredicate predicate = override.predicate().get(0);
            return (Integer) predicate.value();
        }));

        tree.write(team.unnamed.creative.model.Model.builder()
                .key(LEATHER_HORSE_ARMOR_KEY)
                .parent(team.unnamed.creative.model.Model.ITEM_HANDHELD)
                .textures(ModelTexture.builder()
                        .layers(Collections.singletonList(
                                LEATHER_HORSE_ARMOR_KEY
                        ))
                        .build())
                .overrides(overrides)
                .build());
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
        Vector3Float offset = bone.offset();

        Map<ItemTransform.Type, ItemTransform> displays = new HashMap<>();
        displays.put(ItemTransform.Type.HEAD, ItemTransform.builder()
                .translation(new Vector3Float(
                        -offset.x() * ItemTransform.MAX_SCALE,
                        -offset.y() * ItemTransform.MAX_SCALE + DISPLAY_TRANSLATION_Y,
                        -offset.z() * ItemTransform.MAX_SCALE
                ))
                .scale(SCALE)
                .build());

        Map<String, Key> textureMappings = new HashMap<>();
        model.textureMapping().forEach((id, texturePath) -> {
            @Subst("model/texture") String path = model.name() + '/' + texturePath;
            textureMappings.put(id.toString(), Key.key(namespace, path));
        });

        return team.unnamed.creative.model.Model.builder()
                .key(key)
                .display(displays)
                .textures(ModelTexture.builder()
                        .variables(textureMappings)
                        .build())
                .elements(bone.cubes().stream().map(cube -> Element.builder()
                        .from(cube.from())
                        .to(cube.to())
                        .rotation(cube.rotation())
                        .faces(cube.faces())
                        .build()).collect(Collectors.toList()))
                .build();
    }

}