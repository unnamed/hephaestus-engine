package team.unnamed.hephaestus.resourcepack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.ModelDescription;
import team.unnamed.hephaestus.io.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipResourcePackWriter
        implements ResourcePackWriter {

    private final String packMetadata;
    private final String namespace;

    private final ModelGeometryTransformer transformer;

    public ZipResourcePackWriter(String namespace, int packFormat, String description) {
        this.namespace = namespace;
        this.transformer = new ModelGeometryTransformer(namespace);
        this.packMetadata = "{ \"pack\": { \"pack_format\": "
                + packFormat + ", \"description\": \"" + description + "\" } }";
    }

    public ZipResourcePackWriter() {
        this("hephaestus", 6, "Hephaestus custom generated resource pack");
    }

    /**
     * Invokes {@link ZipOutputStream#putNextEntry} using
     * some default {@link ZipEntry} properties to avoid
     * creating different ZIPs when the resource pack is
     * the same (So hash doesn't change)
     */
    private void putNext(ZipOutputStream output, String entryName) throws IOException {
        ZipEntry entry = new ZipEntry(entryName);
        entry.setTime(0L);
        output.putNextEntry(entry);
    }

    @Override
    public List<Model> write(OutputStream stream, List<Model> models) throws IOException {
        this.applyCustomModelData(models);

        ZipOutputStream output = stream instanceof ZipOutputStream
                ? (ZipOutputStream) stream
                : new ZipOutputStream(stream);

        try {

            // write the pack data
            putNext(output, "pack.mcmeta");
            Streams.writeUTF(output, packMetadata);
            output.closeEntry();

            // write the resource pack icon
            putNext(output, "pack.png");
            InputStream iconPng = getClass().getClassLoader().getResourceAsStream("unnamed.png");
            if (iconPng != null) {
                Streams.pipe(iconPng, output);
            }
            output.closeEntry();

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
                    putNext(output, "assets/" + namespace
                            + "/textures/" + modelName + "/" + textureName);
                    try (InputStream input = data.openIn()) {
                        Streams.pipe(input, output);
                    }
                    output.closeEntry();
                }
                // then write all the model bones
                for (ModelBone bone : this.transformer.getAllBones(model.getGeometry())) {

                    JsonObject json = transformer.toJavaJson(model, description, bone);

                    JsonObject overridePredicate = new JsonObject();
                    overridePredicate.addProperty("custom_model_data", bone.getCustomModelData());

                    JsonObject override = new JsonObject();
                    override.add("predicate", overridePredicate);
                    override.addProperty("model", namespace + ":" + modelName + "/" + bone.getName());

                    overrides.add(override);

                    putNext(
                            output,
                            "assets/" + namespace + "/models/"
                                    + modelName
                                    +  "/" + bone.getName()
                                    + ".json"
                    );

                    Streams.writeUTF(output, json.toString());
                    output.closeEntry();
                }
            }

            putNext(output, "assets/minecraft/models/item/bone.json");
            Streams.writeUTF(
                    output,
                    "{"
                            + "\"parent\": \"item/handheld\","
                            + "\"textures\": { \"layer0\": \"item/bone\" },"
                            + "\"overrides\": " + overrides
                            + "}"
            );
            output.closeEntry();
        } finally {
            // finish but don't close
            output.finish();
        }

        return models;
    }

    public void applyCustomModelData(List<Model> models) {
        int data = 1;
        for (Model model : models) {
            for (ModelBone bone : transformer.getAllBones(model.getGeometry())) {
                bone.setCustomModelData(data++);
            }
        }
    }

}
