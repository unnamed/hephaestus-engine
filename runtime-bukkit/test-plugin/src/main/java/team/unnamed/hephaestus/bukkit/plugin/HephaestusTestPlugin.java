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
package team.unnamed.hephaestus.bukkit.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.central.CreativeCentralProvider;
import team.unnamed.creative.central.event.pack.ResourcePackGenerateEvent;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.bukkit.BukkitModelEngine;
import team.unnamed.hephaestus.bukkit.v1_20_R2.BukkitModelEngine_v1_20_R2;
import team.unnamed.hephaestus.reader.blockbench.BBModelReader;
import team.unnamed.hephaestus.writer.ModelWriter;

import java.io.InputStream;
import java.util.Objects;

@SuppressWarnings("unused") // used via reflection by the server
public final class HephaestusTestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final BukkitModelEngine engine = BukkitModelEngine_v1_20_R2.create(this);

        // load models from resources
        final ModelRegistry registry = new ModelRegistry();
        registry.registerModel(loadModel("dragon.bbmodel"));
        registry.registerModel(loadModel("geometry.bbmodel"));
        registry.registerModel(loadModel("redstone_monstrosity.bbmodel"));

        // listen to resource pack generation
        CreativeCentralProvider.get().eventBus().listen(this, ResourcePackGenerateEvent.class, event -> {
            final ResourcePack resourcePack = event.resourcePack();

            // write models to the resource pack
            ModelWriter.resource("hephaestus_test_plugin_namespace")
                    .write(resourcePack, registry.models());
        });

        // register our command
        Objects.requireNonNull(getCommand("hephaestus"), "'hephaestus' command not registered! altered plugin.yml?")
                .setExecutor(new ModelCommand(registry, engine));
    }

    private @NotNull Model loadModel(final @NotNull String fileName) {
        try (final InputStream input = getResource("models/" + fileName)) {
            if (input == null) {
                throw new IllegalStateException("Model " + fileName + " not found");
            }
            return BBModelReader.blockbench().read(input);
        } catch (final Exception exception) {
            throw new RuntimeException("Failed to load model " + fileName, exception);
        }
    }

}
