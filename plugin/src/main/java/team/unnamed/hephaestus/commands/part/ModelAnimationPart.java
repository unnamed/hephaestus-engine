package team.unnamed.hephaestus.commands.part;

import me.fixeddev.commandflow.CommandContext;
import me.fixeddev.commandflow.exception.ArgumentParseException;
import me.fixeddev.commandflow.part.ArgumentPart;
import me.fixeddev.commandflow.stack.ArgumentStack;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.resourcepack.ModelRegistry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO: MAKE THIS WORK
public class ModelAnimationPart implements ArgumentPart {

    private final ModelRegistry modelRegistry;
    private final String name;

    public ModelAnimationPart(ModelRegistry modelRegistry, String name) {
        this.modelRegistry = modelRegistry;
        this.name = name;
    }

    @Override
    public List<?> parseValue(CommandContext commandContext, ArgumentStack stack) throws ArgumentParseException {
        Model model = modelRegistry.get(stack.current());
        if (model == null) {
            throw new ArgumentParseException("Unknown model");
        }

        ModelAnimation animation = model.getAnimations().get(stack.next());
        if (animation == null) {
            throw new ArgumentParseException("Unknown animation");
        }

        return Collections.singletonList(animation);
    }

    @Override
    public List<String> getSuggestions(CommandContext ctx, ArgumentStack stack) {
        Model model = modelRegistry.get(stack.current());

        String prefix = stack.hasNext() ? stack.next().toLowerCase() : "";
        List<String> suggestions = new ArrayList<>();
        if (model == null) {
            throw new ArgumentParseException("Unknown model");
        }

        for (String animation : model.getAnimations().keySet()) {
            if (animation.toLowerCase().startsWith(prefix)) {
                suggestions.add(animation);
            }
        }
        return suggestions;
    }

    @Override
    public Type getType() {
        return ModelAnimation.class;
    }

    @Override
    public String getName() {
        return name;
    }
}
