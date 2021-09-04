package team.unnamed.hephaestus.resourcepack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.io.TreeOutputStream;
import team.unnamed.hephaestus.model.ModelAsset;
import team.unnamed.hephaestus.model.ModelBoneAsset;
import team.unnamed.hephaestus.model.animation.KeyFrame;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelBoneAnimation;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Bones;
import team.unnamed.hephaestus.util.KeyFrames;
import team.unnamed.hephaestus.util.Vectors;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private void writeScaleKeyFrames(
            TreeOutputStream output,
            JsonArray overrides,
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
            KeyFrame next = KeyFrames.getNext(tick, frames);
            if (next == null) {
                size = previous.getValue();
            } else {
                float ratio = (tick - previous.getPosition())
                        / (next.getPosition() - previous.getPosition());
                size = Vectors.lerp(
                        previous.getValue(),
                        next.getValue(),
                        ratio
                );
            }
        }

        sizeProduct = sizeProduct.multiply(size);

        if (!sizeProduct.equals(Vector3Float.ONE)) {
            int data = animation.getModelData().getOrDefault(bone.getName(), Collections.emptyMap()).get(tick);

            String modelName = model.getName() + "/frames/" + bone.getName() + "-" + data;

            JsonObject overridePredicate = new JsonObject();
            overridePredicate.addProperty("custom_model_data", data);

            JsonObject override = new JsonObject();
            override.add("predicate", overridePredicate);
            override.addProperty("model", namespace + ":" + modelName);
            overrides.add(override);

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

        JsonArray overrides = new JsonArray();

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

                JsonObject overridePredicate = new JsonObject();
                overridePredicate.addProperty("custom_model_data", bone.getCustomModelData());

                JsonObject override = new JsonObject();
                override.add("predicate", overridePredicate);
                override.addProperty("model", namespace + ":" + modelName + "/" + bone.getName());

                overrides.add(override);

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