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
package team.unnamed.hephaestus.bukkit.plugin.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.bukkit.ModelView;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public final class ModelRegistry {
    private final Map<String, Model> models = new ConcurrentHashMap<>();
    private final Map<UUID, ModelView> views = new ConcurrentHashMap<>();

    public void register(final @NotNull Model model) {
        requireNonNull(model, "model");
        models.put(model.name(), model);
    }

    public @Nullable Model model(final @NotNull String name) {
        requireNonNull(name, "name");
        return models.get(name);
    }

    public @Nullable ModelView view(final @NotNull UUID uuid) {
        requireNonNull(uuid, "uuid");
        return views.get(uuid);
    }

    public void view(final @NotNull ModelView view) {
        requireNonNull(view, "view");
        views.put(view.getUniqueId(), view);
    }

    public @NotNull Collection<ModelView> views() {
        return views.values();
    }

    public @NotNull Collection<Model> models() {
        return models.values();
    }
}