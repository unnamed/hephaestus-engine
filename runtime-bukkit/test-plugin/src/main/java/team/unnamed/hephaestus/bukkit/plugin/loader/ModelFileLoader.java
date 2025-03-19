package team.unnamed.hephaestus.bukkit.plugin.loader;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Readable;
import team.unnamed.hephaestus.ModelDataCursor;
import team.unnamed.hephaestus.bukkit.plugin.registry.ModelRegistry;
import team.unnamed.hephaestus.reader.ModelReader;
import team.unnamed.hephaestus.reader.blockbench.BBModelReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public final class ModelFileLoader {
    private final Plugin plugin;
    private final ModelReader bbModelReader;

    public ModelFileLoader(final @NotNull Plugin plugin) {
        this.plugin = requireNonNull(plugin, "plugin");

        final var modelDataCursor = new ModelDataCursor(7270);
        this.bbModelReader = BBModelReader.blockbench(modelDataCursor);
    }

    public void load(final @NotNull ModelRegistry modelRegistry) {
        final var folder = plugin.getDataFolder().toPath().resolve("models");

        if (Files.notExists(folder)) {
            try {
                Files.createDirectories(folder);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create models folder", e);
            }
            plugin.getLogger().info("No models found.");
            return;
        }

        try {
            load0(modelRegistry, folder);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load models", e);
        }

        plugin.getLogger().info("Loaded " + modelRegistry.models().size() + " models");
    }

    private void load0(final @NotNull ModelRegistry modelRegistry, final @NotNull Path folder) throws IOException {
        try (final var files = Files.list(folder)) {
            files.forEach(file -> {
                if (Files.isDirectory(file)) {
                    try {
                        load0(modelRegistry, file);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to load models", e);
                    }
                    return;
                }

                final var name = file.getFileName().toString();

                // Test model types
                if (name.endsWith(".bbmodel")) {
                    final var model = bbModelReader.read(Readable.path(file));
                    modelRegistry.register(model);
                } else {
                    plugin.getLogger().warning("Couldn't load model from file " + file + ", unknown model type. (Note that types are determined by their extension)");
                }
            });
        }
    }
}
