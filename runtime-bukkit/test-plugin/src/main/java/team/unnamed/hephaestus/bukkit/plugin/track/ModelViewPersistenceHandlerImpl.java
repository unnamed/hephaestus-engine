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
package team.unnamed.hephaestus.bukkit.plugin.track;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.bukkit.ModelView;
import team.unnamed.hephaestus.bukkit.plugin.ModelRegistry;
import team.unnamed.hephaestus.bukkit.track.ModelViewPersistenceHandler;

import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

public final class ModelViewPersistenceHandlerImpl implements ModelViewPersistenceHandler {
    private static final NamespacedKey MODEL_KEY = new NamespacedKey("hephaestus_test_plugin", "model");

    private final ModelRegistry modelRegistry;

    public ModelViewPersistenceHandlerImpl(final @NotNull ModelRegistry modelRegistry) {
        this.modelRegistry = requireNonNull(modelRegistry, "modelRegistry");
    }

    @Override
    public @NotNull CompletableFuture<Model> determineModel(final @NotNull Entity entity) {
        final var data = entity.getPersistentDataContainer();
        final var modelName = data.get(MODEL_KEY, PersistentDataType.STRING);

        if (modelName == null) {
            // This entity doesn't specify a model
            return CompletableFuture.completedFuture(null);
        }

        final var model = modelRegistry.model(modelName);
        if (model == null) {
            // This entity specifies an unknown model
            System.err.println("Entity with UUID: " + entity.getUniqueId() + " specifies an unknown model: " + modelName + "!");
            return CompletableFuture.completedFuture(null);
        }

        System.out.println("Found out model: " + model.name() + " for entity with UUID: " + entity.getUniqueId() + "!");
        return CompletableFuture.completedFuture(model);
    }

    @Override
    public void saveModel(final @NotNull Entity entity, final @NotNull ModelView view) {
        final var model = view.model();
        final var data = entity.getPersistentDataContainer();
        data.set(MODEL_KEY, PersistentDataType.STRING, model.name());
        System.out.println("Saved model: " + model.name() + " for entity with UUID: " + entity.getUniqueId() + "!");
    }
}
