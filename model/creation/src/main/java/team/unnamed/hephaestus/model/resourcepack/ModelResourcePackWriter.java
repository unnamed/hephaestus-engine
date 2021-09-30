package team.unnamed.hephaestus.model.resourcepack;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.io.TreeOutputStream;
import team.unnamed.hephaestus.model.ModelAsset;
import team.unnamed.hephaestus.model.ModelBoneAsset;
import team.unnamed.hephaestus.model.animation.KeyFrame;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelBoneAnimation;
import team.unnamed.hephaestus.resourcepack.ResourcePackWriter;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.model.util.Bones;
import team.unnamed.hephaestus.model.util.KeyFrames;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

    private void writeScaleKeyFrames(
            TreeOutputStream output,
            Set<ItemOverride> overrides,
            ModelAnimation animation,
            ModelAsset model,
            int tick,
            ModelBoneAsset bone,
            Vector3Float sizeProduct
    ) throws IOException {

        ModelBoneAnimation boneAnimation = animation.getAnimationsByBoneName().get(bone.getName());

        Vector3Float size;

        if (boneAnimation == null) {
            size = Vector3Float.ONE;
        } else {
            List<KeyFrame> frames = boneAnimation.getScaleFrames();
            KeyFrame previous = KeyFrames.getPrevious(tick, frames, Vector3Float.ONE);
            size = previous.getValue();
        }

        sizeProduct = sizeProduct.multiply(size);
        int data;

        if (!sizeProduct.equals(Vector3Float.ONE)
                && (data = animation.getModelData().getOrDefault(bone.getName(), Collections.emptyMap()).getOrDefault(tick, -1)) != -1) {

            String modelName = model.getName() + "/frames/" + bone.getName() + "-" + data;

            overrides.add(new ItemOverride(data, namespace + ':' + modelName));

            output.useEntry(
                    "assets/" + namespace + "/models/"
                            + modelName
                            + ".json"
            );

            double displayScale = ModelGeometryTransformer.DISPLAY_SCALE;
            Streams.writeUTF(
                    output,
                    "{ " +
                            "\"parent\": \"" + namespace + ":" + model.getName() + "/" + bone.getName() + "\"," +
                            "\"display\": {" +
                            "\"head\": {" +
                            "\"scale\": [" + (displayScale * sizeProduct.getX())
                            + ", " + (displayScale * sizeProduct.getY())
                            + ", " + (displayScale * sizeProduct.getZ())
                            + "]" +
                            "}" +
                            "}" +
                            "}"
            );
            output.closeEntry();
        }

        for (ModelBoneAsset child : bone.getBones()) {
            writeScaleKeyFrames(output, overrides, animation, model, tick, child, sizeProduct);
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
            String modelName = model.getName();

            for (Map.Entry<String, Streamable> texture : model.getTextures().entrySet()) {
                String textureName = texture.getKey();
                Streamable data = texture.getValue();

                if (!textureName.endsWith(".png")) {
                    textureName += ".png";
                }

                // write the texture
                output.useEntry("assets/" + namespace
                        + "/textures/" + modelName + "/" + textureName);
                try (InputStream input = data.openIn()) {
                    Streams.pipe(input, output);
                }
                output.closeEntry();
            }
            // write all the model bones
            for (ModelBoneAsset bone : Bones.getAllBones(model)) {

                JsonObject json = transformer.toJavaJson(model, bone);

                overrides.add(new ItemOverride(
                        bone.getCustomModelData(),
                        namespace + ':' + modelName + '/' + bone.getName()
                ));

                output.useEntry(
                        "assets/" + namespace + "/models/"
                                + modelName
                                +  "/" + bone.getName()
                                + ".json"
                );

                Streams.writeUTF(output, json.toString());
                output.closeEntry();
            }
        }

        // write all the scale frame data
        for (ModelAsset model : models) {
            for (ModelAnimation animation : model.getAnimations().values()) {
                for (ModelBoneAsset bone : model.getBones()) {
                    for (int tick = 0; tick <= animation.getAnimationLength(); tick++) {
                         writeScaleKeyFrames(
                                output,
                                overrides,
                                animation,
                                model,
                                tick,
                                bone,
                                Vector3Float.ONE
                        );
                    }
                }
            }
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