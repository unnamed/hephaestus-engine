package team.unnamed.hephaestus.bukkit.plugin.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.bukkit.plugin.HephaestusPlugin;
import team.unnamed.hephaestus.bukkit.plugin.HephaestuserProvider;
import team.unnamed.hephaestus.bukkit.plugin.modifier.OnGroundBoneModifier;
import team.unnamed.hephaestus.bukkit.plugin.registry.ModelRegistry;

import static java.util.Objects.requireNonNull;

@Command(names = { "model" })
public final class ModelCommand implements CommandClass {
    private final ModelRegistry modelRegistry;

    public ModelCommand(final @NotNull ModelRegistry modelRegistry) {
        this.modelRegistry = requireNonNull(modelRegistry, "modelRegistry");
    }

    @Command(names = "reload")
    public void reload(final @NotNull CommandSender sender) {

    }

    @Command(names = "disguise")
    public void disguise(final @Sender @NotNull Player player, final @NotNull Model model) {
        final var engine = HephaestuserProvider.get().engine();

        // Make the player invisible and save metadata
        player.getPersistentDataContainer().set(HephaestusPlugin.DISGUISED_AS_KEY, PersistentDataType.STRING, model.name());
        player.setInvisible(true);

        // Create the model view on the player
        final var view = engine.spawn(model, player);

        // Make the model bones be on the ground
        new OnGroundBoneModifier(player).apply(view);

        // Save the created view so it's animated
        modelRegistry.view(view);

        player.sendPlainMessage("Disguised as " + model.name());
    }

    @Command(names = "undisguise")
    public void undisguise(final @Sender @NotNull Player player) {
        final var data = player.getPersistentDataContainer();
        final var disguisedAs = data.get(HephaestusPlugin.DISGUISED_AS_KEY, PersistentDataType.STRING);

        if (disguisedAs == null) {
            player.sendPlainMessage("You are not disguised!");
            return;
        }

        final var model = modelRegistry.model(disguisedAs);
        if (model == null) {
            player.sendPlainMessage("You were disguised as a model that doesn't exist anymore, undisguising you...");
        } else {
            // todo: !
            player.sendPlainMessage("Undisguised");
        }

        player.setInvisible(false);
        data.remove(HephaestusPlugin.DISGUISED_AS_KEY);
    }

    @Command(names = "summon")
    public void summon(final @Sender @NotNull Player player, final @NotNull Model model) {
        // Spawn base entity
        final var pig = player.getWorld().spawn(player.getLocation(), Pig.class);
        pig.setInvisible(true);

        // Create the model view on the pig
        final var view = HephaestuserProvider.get().engine().spawn(model, pig);

        // Make the model bones be on the ground
        new OnGroundBoneModifier(pig).apply(view);

        // Save the created view so it's animated
        modelRegistry.view(view);

        player.sendPlainMessage("Summoned " + model.name());
    }
}
