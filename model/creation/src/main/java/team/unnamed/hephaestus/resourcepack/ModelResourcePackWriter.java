package team.unnamed.hephaestus.resourcepack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.io.TreeOutputStream;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.ModelDescription;
import team.unnamed.hephaestus.model.animation.KeyFrame;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelBoneAnimation;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.KeyFrames;
import team.unnamed.hephaestus.util.Vectors;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelResourcePackWriter
        implements ResourcePackWriter {

    private final Collection<Model> models;
    private final String namespace;
    @Nullable private final ResourcePackInfo packInfo;

    private final ModelGeometryTransformer transformer;

    public ModelResourcePackWriter(
            Collection<Model> models,
            String namespace,
            @Nullable ResourcePackInfo packInfo
    ) {
        this.models = models;
        this.namespace = namespace;
        this.packInfo = packInfo;
        this.transformer = new ModelGeometryTransformer(namespace);
    }

    public ModelResourcePackWriter(Collection<Model> models) {
        this(models, "hephaestus", new ResourcePackInfo(6, "Hephaestus generated", null));
    }

    private int writeScaleKeyFrames(
            TreeOutputStream output,
            JsonArray overrides,
            ModelAnimation animation,
            Model model,
            int tick,
            ModelBone bone,
            Vector3Float sizeProduct,
            int lastData
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
            lastData++;

            String modelName = model.getName() + "/frames/" + bone.getName() + "-" + lastData;

            JsonObject overridePredicate = new JsonObject();
            overridePredicate.addProperty("custom_model_data", lastData);

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

            animation.getModelData().computeIfAbsent(bone.getName(), k -> new HashMap<>())
                    .put(tick, lastData);
        }

        for (ModelBone child : bone.getBones()) {
            lastData = writeScaleKeyFrames(output, overrides, animation, model, tick, child, sizeProduct, lastData);
        }

        return lastData;
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

        int lastData = this.applyCustomModelData(models);

        if (packInfo != null) {
            // write the pack data
            output.useEntry("pack.mcmeta");
            Streams.writeUTF(
                    output,
                    "{ " +
                            "\"pack\":{" +
                            "\"pack_format\":" + packInfo.getFormat() + "," +
                            "\"description\":\"" + packInfo.getDescription() + "\"" +
                            "}" +
                            "}"
            );
            output.closeEntry();

            Streamable icon = packInfo.getIcon();
            if (icon != null) {
                output.useEntry("pack.png");
                icon.transfer(output);
                output.closeEntry();
            }
        }

        JsonArray overrides = new JsonArray();

        for (Model model : models) {
            ModelDescription description = model.getGeometry().getDescription();
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
            for (ModelBone bone : this.transformer.getAllBones(model.getGeometry())) {

                JsonObject json = transformer.toJavaJson(model, description, bone);

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
        for (Model model : models) {
            for (ModelAnimation animation : model.getAnimations().values()) {
                for (ModelBone bone : model.getGeometry().getBones()) {
                    for (int tick = 0; tick <= animation.getAnimationLength(); tick++) {
                        lastData = writeScaleKeyFrames(
                                output,
                                overrides,
                                animation,
                                model,
                                tick,
                                bone,
                                Vector3Float.ONE,
                                lastData
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

    public int applyCustomModelData(Collection<Model> models) {
        int data = 1;
        for (Model model : models) {
            for (ModelBone bone : transformer.getAllBones(model.getGeometry())) {
                bone.setCustomModelData(data++);
            }
        }
        return data;
    }

}