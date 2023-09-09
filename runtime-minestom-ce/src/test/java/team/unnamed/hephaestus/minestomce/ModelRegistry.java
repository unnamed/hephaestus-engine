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
package team.unnamed.hephaestus.minestomce;

import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.ModelDataCursor;
import team.unnamed.hephaestus.reader.blockbench.BBModelReader;
import team.unnamed.hephaestus.writer.ModelWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ModelRegistry {

    private final Map<String, Model> models = new HashMap<>();
    private final Map<String, ModelEntity> views = new HashMap<>();

    private final ModelDataCursor modelDataCursor = new ModelDataCursor(0);

    public void write(ResourcePack resourcePack) {
        ModelWriter.resource().write(resourcePack, models.values());
    }

    public void model(Model model) {
        models.put(model.name(), model);
    }

    public void loadModelFromResource(String name) {
        try (InputStream input = ModelRegistry.class.getClassLoader().getResourceAsStream(name)) {
            Objects.requireNonNull(input, "Model not found: " + name);
            model(BBModelReader.blockbench(modelDataCursor).read(input));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load model " + name, e);
        }
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
        return Long.toHexString(System.nanoTime());
    }

}
