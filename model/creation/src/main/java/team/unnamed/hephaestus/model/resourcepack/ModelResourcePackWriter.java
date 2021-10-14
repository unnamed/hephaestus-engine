package team.unnamed.hephaestus.model.resourcepack;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.io.TreeOutputStream;
import team.unnamed.hephaestus.model.ModelAsset;
import team.unnamed.hephaestus.model.ModelBoneAsset;
import team.unnamed.hephaestus.resourcepack.ResourcePackWriter;

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
            writeBoneCubes(output, model, overrides, assets);
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