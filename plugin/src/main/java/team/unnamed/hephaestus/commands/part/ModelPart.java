package team.unnamed.hephaestus.commands.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import team.unnamed.hephaestus.ModelRegistry;
import team.unnamed.hephaestus.model.Model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelPart implements ArgumentPart {

    private final ModelRegistry modelRegistry;
    private final String name;

    public ModelPart(ModelRegistry modelRegistry, String name) {
        this.modelRegistry = modelRegistry;
        this.name = name;
    }

    @Override
    public List<?> parseValue(CommandContext ctx, ArgumentStack stack) throws ArgumentParseException {
        String id = stack.next();
        Model model = modelRegistry.get(id);
        if (model == null) {
            throw new ArgumentParseException("Unknown model");
        }
        return Collections.singletonList(model);
    }

    @Override
    public List<String> getSuggestions(CommandContext ctx, ArgumentStack stack) {
        String prefix = stack.hasNext() ? stack.next().toLowerCase() : "";
        List<String> suggestions = new ArrayList<>();
        for (Model model : modelRegistry.getValues()) {
            if (model.getName().toLowerCase().startsWith(prefix)) {
                suggestions.add(model.getName());
            }
        }
        return suggestions;
    }

    @Override
    public Type getType() {
        return Model.class;
    }

    @Override
    public String getName() {
        return name;
    }

}