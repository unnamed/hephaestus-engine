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

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.io.TreeOutputStream;
import team.unnamed.hephaestus.ModelAsset;
import team.unnamed.hephaestus.ModelBoneAsset;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ModelResourcePackWriter
        implements ResourcePackWriter {

    private final Collection<ModelAsset> models;
    private final String namespace;

    private final ModelGeometryTransformer transformer;

    public ModelResourcePackWriter(
            Collection<ModelAsset> models,
            String namespace
    ) {
        this.models = models;
        this.namespace = namespace;
        this.transformer = new ModelGeometryTransformer(namespace);
    }

    public ModelResourcePackWriter(Collection<ModelAsset> models) {
        this(models, "hephaestus");
    }

    private static class ItemOverride implements Comparable<ItemOverride> {

        private final int customModelData;
        private final String model;

        public ItemOverride(int customModelData, String model) {
            this.customModelData = customModelData;
            this.model = model;
        }

        @Override
        public int compareTo(@NotNull ModelResourcePackWriter.ItemOverride other) {
            return Integer.compare(customModelData, other.customModelData);
        }

        @Override
        public String toString() {
            return "{ " +
                    "\"predicate\": { " +
                        "\"custom_model_data\": " + customModelData +
                    " }," +
                    "\"model\": \"" + model + "\" " +
                    "}";
        }

    }

    private void writeBoneCubes(
            TreeOutputStream output,
            ModelAsset model,
            Collection<ItemOverride> overrides,
            Collection<ModelBoneAsset> assets
    ) throws IOException {
        for (ModelBoneAsset bone : assets) {

            JsonObject json = transformer.toJavaJson(model, bone);

            overrides.add(new ItemOverride(
                    bone.getCustomModelData(),
                    namespace + ':' + model.getName() + '/' + bone.getName()
            ));

            output.useEntry(
                    "assets/" + namespace + "/models/"
                            + model.getName()
                            +  "/" + bone.getName()
                            + ".json"
            );

            Streams.writeUTF(output, json.toString());
            output.closeEntry();

            // write children
            writeBoneCubes(output, model, overrides, bone.getBones());
        }
    }

    /**
     * Transfers the resource pack information to the
     * given {@code output}
     *
     * <strong>Note that, as specified in {@link Streamable#transfer},
     * this method won't close the given {@code output}</strong>
     */
    @Override
    public void write(TreeOutputStream output) throws IOException {

        Set<ItemOverride> overrides = new TreeSet<>();

        for (ModelAsset model : models) {
            for (Map.Entry<String, Streamable> texture : model.getTextures().entrySet()) {
                String textureName = texture.getKey();
                Streamable data = texture.getValue();

                if (!textureName.endsWith(".png")) {
                    textureName += ".png";
                }

                // write the texture
                output.useEntry("assets/" + namespace
                        + "/textures/" + model.getName() + "/" + textureName);
                try (InputStream input = data.openIn()) {
                    Streams.pipe(input, output);
                }
                output.closeEntry();
            }
            // write all the model bones
            writeBoneCubes(output, model, overrides, model.getBones());
        }

        output.useEntry("assets/minecraft/models/item/leather_horse_armor.json");
        Streams.writeUTF(
                output,
                "{"
                        + "\"parent\": \"item/handheld\","
                        + "\"textures\": { \"layer0\": \"item/leather_horse_armor\" },"
                        + "\"overrides\": " + overrides
                        + "}"
        );
        output.closeEntry();
    }

}