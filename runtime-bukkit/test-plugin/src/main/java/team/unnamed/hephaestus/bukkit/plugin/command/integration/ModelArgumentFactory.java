package team.unnamed.hephaestus.bukkit.plugin.command.integration;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.annotated.part.PartFactory;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.part.CommandPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.bukkit.plugin.registry.ModelRegistry;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class ModelArgumentFactory implements PartFactory {
    private final ModelRegistry modelRegistry;

    public ModelArgumentFactory(final @NotNull ModelRegistry modelRegistry) {
        this.modelRegistry = requireNonNull(modelRegistry, "modelRegistry");
    }

    @Override
    public @NotNull CommandPart createPart(final @NotNull String name, final @NotNull List<? extends Annotation> modifiers) {
        return new Part(name, modelRegistry);
    }

    public static final class Part implements ArgumentPart {
        private final String name;
        private final ModelRegistry modelRegistry;

        public Part(final @NotNull String name, final @NotNull ModelRegistry modelRegistry) {
            this.name = requireNonNull(name, "name");
            this.modelRegistry = requireNonNull(modelRegistry, "modelRegistry");
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<Model> parseValue(CommandContext ctx, ArgumentStack args, CommandPart part) throws ArgumentParseException {
            final var modelName = args.next();
            final var model = modelRegistry.model(modelName);
            if (model == null) {
                throw new ArgumentParseException(Component.translatable("model.unknown", Component.text(modelName)))
                        .setArgument(this);
            }
            return Collections.singletonList(model);
        }

        @Override
        public List<String> getSuggestions(CommandContext ctx, ArgumentStack stack) {
            if (!stack.hasNext()) {
                return Collections.emptyList();
            }
            final var prefix = stack.next();
            final var suggestions = new ArrayList<String>();
            for (final var model : modelRegistry.models()) {
                if (model.name().startsWith(prefix)) {
                    suggestions.add(model.name());
                }
            }
            return suggestions;
        }
    }
}
