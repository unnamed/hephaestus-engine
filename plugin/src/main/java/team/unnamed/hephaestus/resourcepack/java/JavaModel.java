package team.unnamed.hephaestus.resourcepack.java;

import com.google.gson.annotations.SerializedName;
import team.unnamed.hephaestus.struct.Vector2Int;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.List;
import java.util.Map;

public class JavaModel {

    @SerializedName("file_name")
    private final String fileName;

    @SerializedName("texture_size")
    private final Vector2Int textureSize;

    private final Map<String, String> textures;

    private final Map<String, JavaDisplay> display;

    private final List<JavaCube> elements;

    public JavaModel(String fileName, Vector2Int textureSize, Map<String, String> textures, Map<String, JavaDisplay> display, List<JavaCube> elements) {
        this.fileName = fileName;
        this.textureSize = textureSize;
        this.display = display;
        this.textures = textures;
        this.elements = elements;
    }

    public String getFileName() {
        return fileName;
    }

    public JavaModel normalize() {
        float[] offset = { 0.0F, 0.0F, 0.0F };
        for (JavaCube cube : this.elements) {

            for (int i = 0; i < 3; i++) {
                Vector3Float from = cube.getFrom();
                Vector3Float to = cube.getTo();

                if (from.getInAxis(i) + offset[i] > 32.0F) {
                    offset[i] -= from.getInAxis(i) + offset[i] - 32.0F;
                }

                if (from.getInAxis(i) + offset[i] < -16.0F) {
                    offset[i] -= from.getInAxis(i) + offset[i] + 16.0F;
                }

                if (to.getInAxis(i) + offset[i] > 32.0F) {
                    offset[i] -= to.getInAxis(i) + offset[i] - 32.0F;
                }

                if (to.getInAxis(i) + offset[i] < -16.0F) {
                    offset[i] -= to.getInAxis(i) + offset[i] + 16.0F;
                }
            }
        }
        for (JavaCube cube : this.elements) {
            cube.addFrom(offset);
            cube.addTo(offset);
            cube.getRotation().addOrigin(offset);
        }
        if (offset[0] != 0.0F || offset[1] != 0.0F || offset[2] != 0.0F) {
            this.display.get("head").moveTranslation(offset[0], offset[1], offset[2]);
        }

        return this;
    }

}