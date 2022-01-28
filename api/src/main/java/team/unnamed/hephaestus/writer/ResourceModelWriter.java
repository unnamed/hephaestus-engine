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
package team.unnamed.hephaestus.writer;

import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import team.unnamed.creative.base.Axis3D;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.Element;
import team.unnamed.creative.model.ElementRotation;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.ItemTransform;
import team.unnamed.creative.model.ModelTexture;
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
 * {@link Model} instances to a {@link FileTree}, which
 * represents a resource pack
 *
 * @since 1.0.0
 */
final class ResourceModelWriter implements ModelWriter<FileTree> {

    private static final String NAMESPACE = "hephaestus";

    /**
     * The size of a block for models, this is the number that
     * relates Minecraft blocks to our models
     */
    private static final float BLOCK_SIZE = 16F;
    private static final float HALF_BLOCK_SIZE = BLOCK_SIZE / 2F;

    private static final float SMALL_RATIO = BLOCK_SIZE / (BLOCK_SIZE + 9.6F);
    private static final float LARGE_RATIO = BLOCK_SIZE / (BLOCK_SIZE + 20.57F);

    private static final float SMALL_DISPLAY_SCALE = 3.8095F;
    private static final float LARGE_DISPLAY_SCALE = 3.7333333F;

    public static final float DISPLAY_TRANSLATION_Y = -6.4f;

    private static final float MIN_TRANSLATION = -80F;
    private static final float MAX_TRANSLATION = 80F;

    @Subst(NAMESPACE) private final String namespace;

    ResourceModelWriter(@Subst(NAMESPACE) String namespace) {
        this.namespace = namespace;

        // validate namespace
        Key.key(namespace, "dummy");
    }

    ResourceModelWriter() {
        this(NAMESPACE);
    }

    private void writeBones(
            FileTree tree,
            ModelAsset model,
            Collection<ItemOverride> overrides,
            Collection<BoneAsset> assets
    ) {
        for (BoneAsset bone : assets) {
            Key key = Key.key(namespace, model.name() + '/' + bone.name());

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
                @Subst(NAMESPACE) String textureName = texture.getKey();
                Writable data = texture.getValue();

                Key key = Key.key(namespace, model.name() + '/' + textureName);

                // write the texture
                tree.write(Texture.builder()
                        .key(key)
                        .data(data)
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
                .key(Key.key("item/leather_horse_armor"))
                .parent(team.unnamed.creative.model.Model.ITEM_HANDHELD)
                .textures(ModelTexture.builder()
                        .layers(Collections.singletonList(
                                Key.key("item/leather_horse_armor")
                        ))
                        .build())
                .overrides(overrides)
                .build());
    }

    private static List<ElementAsset> scale(
            Vector3Float bonePivot,
            List<ElementAsset> elements,
            float ratio
    ) {
        float deltaX = bonePivot.x() - HALF_BLOCK_SIZE;
        float deltaY = bonePivot.y() - HALF_BLOCK_SIZE;
        float deltaZ = bonePivot.z() - HALF_BLOCK_SIZE;
        List<ElementAsset> scaledElements = new ArrayList<>(elements.size());

        for (ElementAsset cube : elements) {

            Vector3Float origin = cube.from();
            Vector3Float to = cube.to();

            ElementRotation rotation = cube.rotation();
            Vector3Float rotationOrigin = rotation.origin();
            rotationOrigin = new Vector3Float(
                    scale(-rotationOrigin.x() + bonePivot.x() + HALF_BLOCK_SIZE, ratio),
                    scale(rotationOrigin.y() - bonePivot.y() + HALF_BLOCK_SIZE, ratio),
                    scale(rotationOrigin.z() - bonePivot.z() + HALF_BLOCK_SIZE, ratio)
            );

            scaledElements.add(new ElementAsset(
                    // from
                    new Vector3Float(
                            scale(BLOCK_SIZE + deltaX - to.x(), ratio),
                            scale(origin.y() - deltaY, ratio),
                            scale(origin.z() - deltaZ, ratio)
                    ),
                    // to
                    new Vector3Float(
                        scale(BLOCK_SIZE + deltaX - origin.x(), ratio),
                        scale(to.y() - deltaY, ratio),
                        scale(to.z() - deltaZ, ratio)
                    ),
                    rotation.origin(rotationOrigin),
                    cube.faces()
            ));
        }

        return scaledElements;
    }

    /**
     * Converts a {@link BoneAsset} (a representation of a model
     * bone) to a resource-pack ready {@link team.unnamed.creative.model.Model}
     * object
     *
     * @param model The model holding the given bone
     * @param bone The bone to be converted
     */
    private static team.unnamed.creative.model.Model toCreative(
            Key key,
            ModelAsset model,
            BoneAsset bone
    ) {
        float displayScale = SMALL_DISPLAY_SCALE;
        List<ElementAsset> scaledElements = scale(bone.pivot(), bone.cubes(), SMALL_RATIO);
        Vector3Float offset = computeOffset(scaledElements);

        if (!applyOffset(scaledElements, offset)) {
            // failed to use small scale, use large scale
            scaledElements = scale(bone.pivot(), bone.cubes(), LARGE_RATIO);
            offset = computeOffset(scaledElements);
            displayScale = LARGE_DISPLAY_SCALE;
            // TODO: Make 'bone' not small
            if (!applyOffset(scaledElements, offset)) {
                throw new IllegalStateException("Cubes out of bounds");
            }
        }

        Map<ItemTransform.Type, ItemTransform> displays = new HashMap<>();
        ItemTransform headTransform = ItemTransform.builder()
                .translation(computeTranslation(offset))
                .scale(new Vector3Float(displayScale, displayScale, displayScale))
                .build();
        displays.put(ItemTransform.Type.HEAD, headTransform);

        Map<String, Key> textureMappings = new HashMap<>();
        model.textureMapping().forEach((id, path) ->
                textureMappings.put(id.toString(), Key.key(key.namespace(), model.name() + '/' + path)));

        return team.unnamed.creative.model.Model.builder()
                .key(key)
                .display(displays)
                .textures(ModelTexture.builder()
                        .variables(textureMappings)
                        .build())
                .elements(scaledElements.stream().map(cube -> Element.builder()
                        .from(cube.from())
                        .to(cube.to())
                        .rotation(cube.rotation())
                        .faces(cube.faces())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    /**
     * Computes the offset for the given cube elements,
     * does not modify the provided list
     *
     * @param elements The elements to compute
     * @return The resulting offset
     */
    private static Vector3Float computeOffset(List<ElementAsset> elements) {
        Vector3Float offset = Vector3Float.ZERO;
        for (ElementAsset cube : elements) {
            Vector3Float from = cube.from();
            Vector3Float to = cube.to();

            for (Axis3D axis : Axis3D.values()) {
                offset = computeOffset(offset, axis, from);
                offset = computeOffset(offset, axis, to);
            }
        }
        return offset;
    }

    private static Vector3Float computeOffset(
            Vector3Float offset,
            Axis3D axis,
            Vector3Float from
    ) {
        float off = offset.get(axis);
        float value = from.get(axis);

        if (value + off > Element.MAX_EXTENT) {
            off -= value + off - Element.MAX_EXTENT;
        }
        if (value + off < Element.MIN_EXTENT) {
            off -= value + off - Element.MIN_EXTENT;
        }

        return offset.with(axis, off);
    }

    /**
     * Applies an offset to a given list of elements
     *
     * @param elements The element list to modify
     * @param offset The applied offset
     * @return True if successfully applied, false otherwise
     * (is out of MIN_EXTENT and MAX_EXTENT)
     */
    private static boolean applyOffset(List<ElementAsset> elements, Vector3Float offset) {
        // compute offset
        for (int i = 0; i < elements.size(); i++) {
            ElementAsset cube = elements.get(i);
            Vector3Float from = cube.from().add(offset);
            Vector3Float to = cube.to().add(offset);

            if (isOutOfBounds(from) || isOutOfBounds(to)) {
                // fail
                return false;
            }

            ElementRotation rotation = cube.rotation();

            Vector3Float origin = rotation.origin();
            rotation = rotation.origin(origin.add(offset));

            elements.set(i, new ElementAsset(from, to, rotation, cube.faces()));
        }

        // success
        return true;
    }

    private static boolean isOutOfBounds(Vector3Float location) {
        return location.x() < Element.MIN_EXTENT
                || location.y() < Element.MIN_EXTENT
                || location.z() < Element.MIN_EXTENT
                || location.x() > Element.MAX_EXTENT
                || location.y() > Element.MAX_EXTENT
                || location.z() > Element.MAX_EXTENT;
    }

    private static Vector3Float computeTranslation(Vector3Float offset) {
        float translationX = -offset.x() * SMALL_DISPLAY_SCALE;
        float translationY = DISPLAY_TRANSLATION_Y - offset.y() * SMALL_DISPLAY_SCALE;
        float translationZ = -offset.z() * SMALL_DISPLAY_SCALE;

        if (
                translationX < MIN_TRANSLATION || translationX > MAX_TRANSLATION
                        || translationY < MIN_TRANSLATION || translationY > MAX_TRANSLATION
                        || translationZ < MIN_TRANSLATION || translationZ > MAX_TRANSLATION
        ) {
            throw new IllegalStateException("Translation out of bounds");
        }

        return new Vector3Float(translationX, translationY, translationZ);
    }

    private static float scale(float value, float ratio) {
        return HALF_BLOCK_SIZE - (ratio * (HALF_BLOCK_SIZE - value));
    }

}