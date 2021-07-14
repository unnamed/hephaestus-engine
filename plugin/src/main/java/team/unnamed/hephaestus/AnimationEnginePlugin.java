package team.unnamed.hephaestus;

import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.builder.AnnotatedCommandBuilderImpl;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.hephaestus.adapt.AdaptionModule;
import team.unnamed.hephaestus.adapt.AdaptionModuleFactory;
import team.unnamed.hephaestus.commands.HephaestusCommand;
import team.unnamed.hephaestus.commands.SummonCommand;
import team.unnamed.hephaestus.commands.part.ModelAnimationPart;
import team.unnamed.hephaestus.commands.part.ModelPart;
import team.unnamed.hephaestus.model.view.DefaultModelViewAnimator;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.view.ModelViewAnimator;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;
import team.unnamed.hephaestus.reader.ModelReader;
import team.unnamed.hephaestus.reader.blockbench.BlockbenchModelReader;
import team.unnamed.hephaestus.resourcepack.HephaestusResourcePackExporter;
import team.unnamed.hephaestus.resourcepack.ResourcePackExporter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class AnimationEnginePlugin extends JavaPlugin {

    private ModelRegistry modelRegistry;
    private ModelViewAnimator animator;
    private ModelViewRenderer renderer;

    @Override
    public void onLoad() {
        try {
            this.saveDefaultModels();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        modelRegistry = new ModelRegistry();
        AdaptionModule module = AdaptionModuleFactory.create();

        animator = new DefaultModelViewAnimator(this);
        renderer = module.createRenderer(animator);

        ModelReader modelReader = new BlockbenchModelReader();
        ResourcePackExporter resourcePackExporter = new HephaestusResourcePackExporter();

        File modelsDirectory = new File(this.getDataFolder(), "models");

        if (!modelsDirectory.exists() && !modelsDirectory.mkdirs()) {
            throw new IllegalStateException("Cannot create models folder");
        }

        File[] contents = modelsDirectory.listFiles();

        List<Model> models = new ArrayList<>();
        if (contents != null) {
            for (File modelFile : contents) {
                if (modelFile.isFile() && modelFile.getName().endsWith(".bbmodel")) {
                    try (Reader reader = new FileReader(modelFile)) {
                        Model model = modelReader.read(
                            modelFile.getName().split("\\.")[0],
                            reader
                        );
                        this.getLogger().log(Level.INFO, "Loaded model " + model.getName());
                        models.add(model);
                    } catch (IOException exception) {
                        this.getLogger().log(
                            Level.WARNING,
                            "Cannot read model data from directory " + modelFile.getName(),
                            exception
                        );
                    }
                }
            }
        }

        this.getLogger().log(Level.INFO, "Successfully loaded " + models.size() + " models!");

        try {
            File resourcePackFile = new File(this.getDataFolder(), "hephaestus-generated" + ".zip");

            if (!this.getDataFolder().exists() && !this.getDataFolder().mkdirs()) {
                throw new IOException("Cannot create folder");
            } else if (!resourcePackFile.exists() && !resourcePackFile.createNewFile()) {
                throw new IOException("Failed to create the resource pack file");
            }
            try (OutputStream output = new FileOutputStream(resourcePackFile)) {
                resourcePackExporter.export(output, models)
                    .forEach(model -> {
                        getLogger().info("Registered model " + model.getName());
                        modelRegistry.register(model);
                    });
            }
        } catch (IOException exception) {
            this.getLogger().log(
                Level.SEVERE,
                "Could not generate resource pack",
                exception
            );
        }
    }

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName(getConfig().getString("script.lang"));

        PartInjector partInjector = PartInjector.create();
        partInjector.install(new DefaultsModule());
        partInjector.install(new BukkitModule());
        partInjector.bindFactory(Model.class, (name, modifiers) ->
                new ModelPart(modelRegistry, name)
        );
        partInjector.bindFactory(ModelAnimation.class, (name, modifiers) ->
                new ModelAnimationPart(modelRegistry, name)
        );

        AnnotatedCommandTreeBuilder commandBuilder = new AnnotatedCommandTreeBuilderImpl(
                new AnnotatedCommandBuilderImpl(partInjector),
                (clazz, parent) -> {
                    if (clazz.isAssignableFrom(SummonCommand.class)) {
                        return new SummonCommand(renderer, animator);
                    } else {
                        try {
                            return clazz.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }
        );

        CommandManager commandManager = new BukkitCommandManager(this.getName());

        commandManager.registerCommands(commandBuilder.fromClass(new HephaestusCommand()));
    }

    public ModelRegistry getModelRegistry() {
        return modelRegistry;
    }

    public ModelViewAnimator getAnimator() {
        return animator;
    }

    public ModelViewRenderer getRenderer() {
        return renderer;
    }

    private void saveDefaultModels() throws IOException {
        File modelsDirectory = new File(this.getDataFolder(), "models");

        if (!modelsDirectory.exists() && !modelsDirectory.mkdirs()) {
            throw new IllegalStateException("Cannot create models folder");
        }

        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        if (jarFile.isFile()) {
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                if (name.startsWith("default" + "/") && !name.equals("default" + "/")) {
                    String fileName = name.split("default" + "/")[1];
                    Files.copy(
                            this.getClass().getClassLoader().getResourceAsStream("default" + "/" + fileName),
                            Paths.get(modelsDirectory.getPath() + File.separator + fileName)
                    );
                }
            }
            jar.close();
        }
    }
}
