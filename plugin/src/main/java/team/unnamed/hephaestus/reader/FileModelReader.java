package team.unnamed.hephaestus.reader;

import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelGeometry;
import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileModelReader implements ModelReader {

    private final ModelGeometryReader geometryReader;
    private final ModelAnimationsReader animationsReader;

    public FileModelReader(
            ModelGeometryReader geometryReader,
            ModelAnimationsReader animationsReader
    ) {
        this.geometryReader = geometryReader;
        this.animationsReader = animationsReader;
    }

    @Override
    public Model read(File folder) throws IOException {
        File modelFile = new File(folder, "model.bbmodel");

        if (!modelFile.exists()) {
            throw new IllegalArgumentException("Geometry file does not exist in " + folder.getPath());
        }

        ModelGeometry geometry = this.geometryReader.read(new FileReader(modelFile));
        Map<String, ModelAnimation> animations = new HashMap<>();

        try (InputStream input = new FileInputStream(modelFile)) {
            animations.putAll(this.animationsReader.read(input));
        }

        List<File> textures = new ArrayList<>();
        File[] children = folder.listFiles();

        if (children != null) {
            for (File file : children) {
                if (file.getName().endsWith(".png")) {
                    textures.add(file);
                }
            }
        }

        return new Model(
                folder.getName(),
                geometry,
                animations,
                textures
        );
    }
}