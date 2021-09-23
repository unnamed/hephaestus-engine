package team.unnamed.hephaestus;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.builder.AnnotatedCommandBuilderImpl;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.hephaestus.adapt.AdaptionModule;
import team.unnamed.hephaestus.adapt.AdaptionModuleFactory;
import team.unnamed.hephaestus.commands.HephaestusCommand;
import team.unnamed.hephaestus.commands.SummonCommand;
import team.unnamed.hephaestus.commands.part.ModelAnimationPart;
import team.unnamed.hephaestus.commands.part.ModelPart;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.listener.ResourcePackApplyListener;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.ModelAsset;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.view.DefaultModelViewAnimator;
import team.unnamed.hephaestus.model.view.ModelViewAnimator;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;
import team.unnamed.hephaestus.mythicmobs.MythicMobsHookListener;
import team.unnamed.hephaestus.reader.ModelReader;
import team.unnamed.hephaestus.reader.BBModelReader;
import team.unnamed.hephaestus.resourcepack.ResourceExportMethodFactory;
import team.unnamed.hephaestus.resourcepack.ResourceExporter;
import team.unnamed.hephaestus.resourcepack.ModelResourcePackWriter;
import team.unnamed.hephaestus.resourcepack.ResourcePackInfo;
import team.unnamed.hephaestus.resourcepack.ResourcePackInfoWriter;
import team.unnamed.hephaestus.resourcepack.ResourcePackWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
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

    private String url;
    private byte[] hash;

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

        ModelReader modelReader = new BBModelReader();

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
                        Model model = modelReader.read(reader);
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
            ResourceExporter<?> resourceExporter = ResourceExportMethodFactory.createExporter(
                    this.getDataFolder(),
                    this.getConfig().getString("pack.generate", "file:hephaestus-generated.zip")
            );

            List<ModelAsset> modelAssets = new ArrayList<>();
            for (Model model : models) {
                modelAssets.add(model.getAsset());
            }
            Object response = resourceExporter.export(ResourcePackWriter.compose(
                    ResourcePackInfo.builder()
                            .setFormat(7)
                            .setDescription("Hephaestus-generated resource pack")
                            .setIcon(Streamable.ofResource(
                                    getClass().getClassLoader(),
                                    "hephaestus.png"
                            ))
                            .build()
                            .toWriter(),
                    new ModelResourcePackWriter(modelAssets)
            ));

            if (response instanceof String) {
                JsonObject json = new JsonParser().parse(response.toString()).getAsJsonObject();
                url = json.get("url").getAsString();
                hash = Streams.getBytesFromHex(json.get("hash").getAsString());
            }

            models.forEach(model -> {
                getLogger().info("Registered model " + model.getName());
                model.discardResourcePackData();
                modelRegistry.register(model);
            });
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
                        return new SummonCommand(renderer);
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

        if (url != null && hash != null) {
            Bukkit.getPluginManager().registerEvents(new ResourcePackApplyListener(this, url, hash), this);
        }

        Bukkit.getPluginManager().registerEvents(new MythicMobsHookListener(modelRegistry, new ModelViewRegistry(), renderer), this);
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
        if (modelsDirectory.exists()) {
            return;
        }

        if (!modelsDirectory.mkdirs()) {
            throw new IllegalStateException("Cannot create models folder");
        }

        File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        if (jarFile.isFile()) {
            JarFile jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith("default/") && !name.equals("default/")) {
                    InputStream input = this.getClass().getClassLoader()
                            .getResourceAsStream(name);
                    File target = new File(modelsDirectory, name.split("default/")[1]);
                    if (input != null && !target.exists() && target.createNewFile()) {
                        OutputStream output = new FileOutputStream(target);
                        try {
                            Streams.pipe(input, output);
                        } finally {
                            input.close();
                            output.close();
                        }
                    }
                }
            }
            jar.close();
        }
    }
}
