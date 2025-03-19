package team.unnamed.hephaestus.bukkit.plugin;

import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import me.fixeddev.commandflow.part.defaults.SubCommandPart;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.creative.central.CreativeCentralProvider;
import team.unnamed.creative.central.event.pack.ResourcePackGenerateEvent;
import team.unnamed.hephaestus.bukkit.plugin.command.ModelCommand;
import team.unnamed.hephaestus.bukkit.plugin.command.integration.CommandModuleImpl;
import team.unnamed.hephaestus.bukkit.plugin.integration.IntegrationManager;
import team.unnamed.hephaestus.bukkit.plugin.integration.citizens.CitizensIntegration;
import team.unnamed.hephaestus.bukkit.plugin.integration.citizens.CitizensSubCommand;
import team.unnamed.hephaestus.bukkit.plugin.listener.DisguisedPlayerJoinListener;
import team.unnamed.hephaestus.bukkit.plugin.loader.ModelFileLoader;
import team.unnamed.hephaestus.bukkit.plugin.task.ModelAnimateTask;
import team.unnamed.hephaestus.writer.ModelWriter;

@SuppressWarnings("unused") // instantiated via Reflection by server
public final class HephaestusPlugin extends JavaPlugin {
    public static final NamespacedKey DISGUISED_AS_KEY = new NamespacedKey("hephaestuser", "disguised_as");

    private Hephaestuser hephaestuser;

    @Override
    public void onEnable() {
        hephaestuser = new Hephaestuser(this);

        // load models
        new ModelFileLoader(this).load(hephaestuser.registry());

        HephaestuserProvider.set(hephaestuser);

        IntegrationManager.integrationManager(this)
                .register(new CitizensIntegration())
                .check();

        // resource-pack
        CreativeCentralProvider.get().eventBus().listen(this, ResourcePackGenerateEvent.class, event ->
            ModelWriter.resource("hephaestuser").write(event.resourcePack(), hephaestuser.registry().models()));

        // register commands
        final var manager = new BukkitCommandManager(getName());
        final var partInjector = PartInjector.create();
        partInjector.install(new DefaultsModule());
        partInjector.install(new BukkitModule());
        partInjector.install(new CommandModuleImpl(hephaestuser));
        final var builder = AnnotatedCommandTreeBuilder.create(partInjector);

        final var mainCommand = builder.fromClass(new ModelCommand(hephaestuser.registry())).get(0);
        final var subCommandPart = ((SubCommandPart) mainCommand.getPart());
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            subCommandPart.getSubCommandMap().put("citizens", builder.fromClass(new CitizensSubCommand()).get(0));
        }
        manager.registerCommand(mainCommand);

        // register listeners
        Bukkit.getPluginManager().registerEvents(new DisguisedPlayerJoinListener(hephaestuser), this);

        // tasks
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ModelAnimateTask(hephaestuser.registry()), 0L, 1L);
    }

    @Override
    public void onDisable() {
        if (hephaestuser != null) {
            hephaestuser.close();
        }
    }
}
