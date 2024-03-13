/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
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
package team.unnamed.hephaestus.minestom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.unnamed.creative.ResourcePack;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.view.modifier.player.rig.PlayerRigWriter;
import team.unnamed.hephaestus.reader.ModelReader;
import team.unnamed.hephaestus.reader.blockbench.BBModelReader;
import team.unnamed.hephaestus.writer.ModelWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class ModelRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelRegistry.class);

    private final Map<String, Model> models = new HashMap<>();
    private final Map<String, ModelEntity> views = new HashMap<>();

    private final ModelReader reader = BBModelReader.blockbench();

    public void write(ResourcePack resourcePack) {
        ModelWriter.resource().write(resourcePack, models.values());
        PlayerRigWriter.resource().write(resourcePack);
    }

    public void model(Model model) {
        models.put(model.name(), model);
    }

    public void loadModelFromResource(String name) {
        try (InputStream input = ModelRegistry.class.getClassLoader().getResourceAsStream(name)) {
            requireNonNull(input, "Model not found: " + name);
            model(reader.read(input));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load model " + name, e);
        }
    }

    public void loadModelsFromResourcesFolder(final @NotNull String folderPath) {
        final URI uri;
        try {
            uri = getClass().getClassLoader().getResource(folderPath).toURI();
        } catch (final URISyntaxException ignored) {
            return; // should never happen, but make the compiler happy
        }

        try {
            Path path;
            try {
                path = Paths.get(uri);
            } catch (final FileSystemNotFoundException ignored) {
                path = FileSystems.newFileSystem(uri, new HashMap<>())
                        .getPath('/' + folderPath);
            }

            try (final Stream<Path> contentStream = Files.list(path)) {
                contentStream.forEach(filePath -> {
                    if (Files.isRegularFile(filePath)) {
                        try (final InputStream input = ModelRegistry.class.getClassLoader().getResourceAsStream(folderPath + '/' + filePath.getFileName())) {
                            requireNonNull(input, "input for " + folderPath + "/" + filePath.getFileName());
                            final Model model = reader.read(input);
                            model(model);
                            LOGGER.info("Loaded '" + model.name() + "' model");
                        } catch (final IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                });
            }
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public ModelReader reader() {
        return reader;
    }

    public @Nullable Model model(String name) {
        return models.get(name);
    }

    public Collection<Model> models() {
        return models.values();
    }

    public void view(String id, ModelEntity view) {
        views.put(id, view);
    }

    public @Nullable ModelEntity view(String id) {
        return views.get(id);
    }

    public Collection<String> viewIds() {
        return views.keySet();
    }

    public Collection<ModelEntity> views() {
        return views.values();
    }

    public static String generateViewId() {
        // generate a hexadecimal string from the current time in milliseconds
        final String hexMillis = Long.toHexString(System.currentTimeMillis());
        final char[] chars = hexMillis.toCharArray();
        // reverse the array
        for (int i = 0; i < chars.length / 2; i++) {
            char tmp = chars[i];
            chars[i] = chars[chars.length - 1 - i];
            chars[chars.length - 1 - i] = tmp;
        }
        return new String(chars);
    }

}
