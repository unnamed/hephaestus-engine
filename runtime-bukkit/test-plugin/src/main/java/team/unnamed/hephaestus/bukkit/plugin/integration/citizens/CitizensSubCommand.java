package team.unnamed.hephaestus.bukkit.plugin.integration.citizens;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.Model;

@Command(names = { "citizens" })
public final class CitizensSubCommand implements CommandClass {
    @Command(names = "set")
    public void set(final @NotNull CommandSender sender, final @NotNull Model model) {
        final var selection = CitizensAPI.getDefaultNPCSelector().getSelected(sender);

        if (selection == null) {
            sender.sendPlainMessage("You must select an NPC to add a model");
            return;
        }

        final var trait = selection.getOrAddTrait(ModelTrait.class);
        trait.model(model);
    }

    @Command(names = "remove")
    public void remove(final @NotNull CommandSender sender) {
        final var selection = CitizensAPI.getDefaultNPCSelector().getSelected(sender);

        if (selection == null) {
            sender.sendPlainMessage("You must select an NPC to remove the model");
            return;
        }

        final var trait = selection.getTraitNullable(ModelTrait.class);
        if (trait == null) {
            sender.sendPlainMessage("The selected NPC does not have a model (No model trait)");
            return;
        }

        selection.removeTrait(ModelTrait.class);
        sender.sendPlainMessage("Model removed from the selected NPC");
    }

    @Command(names = "info")
    public void info(final @NotNull CommandSender sender) {
        final var selection = CitizensAPI.getDefaultNPCSelector().getSelected(sender);

        if (selection == null) {
            sender.sendPlainMessage("You must select an NPC to get the model info");
            return;
        }

        final var trait = selection.getTraitNullable(ModelTrait.class);
        if (trait == null) {
            sender.sendPlainMessage("The selected NPC does not have a model (No model trait)");
            return;
        }

        final var modelName = trait.modelName();
        if (modelName == null) {
            sender.sendPlainMessage("The selected NPC does not have a model (No model)");
        } else {
            sender.sendPlainMessage("The selected NPC has the model: " + modelName);
        }
    }
}
