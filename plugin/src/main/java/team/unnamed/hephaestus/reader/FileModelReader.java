package team.unnamed.hephaestus.reader;

import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelGeometry;
import team.unnamed.hephaestus.model.animation.ModelAnimation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        File geometryFile = new File(folder, "geometry.json");
        File animationsFile = new File(folder, "animations.json");
        File textureFile = new File(folder, "texture.png");

        if (!geometryFile.exists()) {
            throw new IllegalArgumentException("Geometry file does not exist in " + folder.getPath());
        }

        ModelGeometry geometry = this.geometryReader.read(new FileReader(geometryFile));
        List<ModelAnimation> animations = new ArrayList<>();

        if (animationsFile.exists()) {
            try (InputStream input = new FileInputStream(animationsFile)) {
                animations.addAll(this.animationsReader.read(input));
            }
        }

        return new Model(
                folder.getName(),
                geometry,
                animations,
                textureFile
        );
    }
}