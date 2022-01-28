/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
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
package team.unnamed.hephaestus;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.creative.file.FileResource;
import team.unnamed.creative.file.ResourceWriter;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.PackMeta;
import team.unnamed.hephaestus.command.ModelCommand;
import team.unnamed.hephaestus.export.MCPacksHttpExporter;
import team.unnamed.hephaestus.listener.ResourcePackSetListener;
import team.unnamed.hephaestus.reader.ModelReader;
import team.unnamed.hephaestus.writer.ModelWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class ModelEnginePlugin extends JavaPlugin {

    private static final ModelReader READER = ModelReader.blockbench();

    @Override
    public void onEnable() {
        // load models from resources
        getLogger().info("Loading models...");
        ModelRegistry registry = new ModelRegistry();
        registry.add(loadModelFromResource("butterfly.bbmodel"));

        // upload resource pack
        getLogger().info("Uploading resource pack...");
        ResourcePack pack = exportResourcePack(registry);
        getLogger().info("Uploaded resource pack to " + pack.url());

        // register commands and listeners
        getLogger().info("Registering commands and listeners...");
        registerCommand("model", new ModelCommand(registry));
        registerListener(new ResourcePackSetListener(pack));
    }

    private ResourcePack exportResourcePack(ModelRegistry registry) {
        try {
            return new MCPacksHttpExporter().export(tree -> {
                try {
                    ModelWriter.resource().write(tree, registry.all());
                    tree.write(new FileResource() {
                        @Override
                        public String path() {
                            return "pack.mcmeta";
                        }

                        @Override
                        public void serialize(ResourceWriter writer) {
                            Metadata.builder()
                                    .add(PackMeta.of(8, "Generated resource pack"))
                                    .build()
                                    .serialize(writer);
                        }
                    });
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
             throw new UncheckedIOException(e);
        }
    }

    private Model loadModelFromResource(String resourceName) {
        try (InputStream input = super.getResource(resourceName)) {
            Model model = READER.read(input);
            getLogger().info("Loaded model " + model.name());
            return model;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read model " + resourceName, e);
        }
    }

    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = super.getCommand(name);
        if (command == null) {
            throw new IllegalArgumentException("Command not found: " + name);
        }
        command.setExecutor(executor);
    }

    private void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

}
