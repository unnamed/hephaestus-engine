package team.unnamed.hephaestus.resourcepack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.ModelDescription;
import team.unnamed.hephaestus.resourcepack.java.JavaItem;
import team.unnamed.hephaestus.serialize.GsonFactory;
import team.unnamed.hephaestus.io.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipResourcePackWriter
        implements ResourcePackWriter {

    private static final int PACK_FORMAT = 6;
    private static final String PACK_METADATA = "{\n" +
            "  \"pack\": {\n" +
            "    \"pack_format\": " + PACK_FORMAT + ",\n" +
            "    \"description\": \"Hephaestus custom generated resource pack\"\n" +
            "  }\n" +
            "}";

    private final String namespace;
    private final Gson gson;
    private final ModelGeometryTransformer transformer;

    public ZipResourcePackWriter(String namespace) {
        this.namespace = namespace;
        this.gson = GsonFactory.createDefault();
        this.transformer = new ModelGeometryTransformer(namespace);
    }

    public ZipResourcePackWriter() {
        this("hephaestus");
    }

    @Override
    public List<Model> write(OutputStream stream, List<Model> models) throws IOException {
        this.applyCustomModelData(models);

        ZipOutputStream output = stream instanceof ZipOutputStream
                ? (ZipOutputStream) stream
                : new ZipOutputStream(stream);

        try {

            // write the pack data
            output.putNextEntry(new ZipEntry("pack.mcmeta"));
            Streams.writeUTF(output, PACK_METADATA);
            output.closeEntry();

            // write the resource pack icon
            output.putNextEntry(new ZipEntry("pack.png"));
            InputStream iconPng = getClass().getClassLoader().getResourceAsStream("unnamed.png");
            if (iconPng != null) {
                Streams.pipe(iconPng, output);
            }
            output.closeEntry();

            List<JavaItem.Override> overrides = new ArrayList<>();

            for (Model model : models) {
                ModelDescription description = model.getGeometry().getDescription();
                String modelName = model.getName();

                for (Map.Entry<String, Streamable> texture : model.getTextures().entrySet()) {
                    String textureName = texture.getKey();
                    Streamable data = texture.getValue();

                    // write the texture
                    output.putNextEntry(new ZipEntry("assets/" + namespace
                            + "/textures/" + modelName + "/" + textureName));
                    try (InputStream input = data.openIn()) {
                        Streams.pipe(input, output);
                    }
                    output.closeEntry();
                }
                // then write all the model bones
                for (ModelBone bone : this.transformer.getAllBones(model.getGeometry())) {

                    JsonObject json = transformer.toJavaJson(model, description, bone);

                    overrides.add(new JavaItem.Override(
                                    bone.getCustomModelData(),
                                    namespace + ":"
                                            + modelName
                                            + "/" + bone.getName()
                            )
                    );

                    output.putNextEntry(new ZipEntry(
                            "assets/" + namespace + "/models/"
                                    + modelName
                                    +  "/" + bone.getName()
                                    + ".json"
                    ));

                    Streams.writeUTF(output, json.toString());
                    output.closeEntry();
                }
            }

            output.putNextEntry(new ZipEntry("assets/minecraft/models/item/bone.json"));
            Streams.writeUTF(output, gson.toJson(new JavaItem(overrides)));
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
