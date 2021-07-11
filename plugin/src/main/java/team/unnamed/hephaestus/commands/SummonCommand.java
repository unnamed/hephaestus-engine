package team.unnamed.hephaestus.commands;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.AnimationEnginePlugin;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.view.ModelViewAnimator;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;
import team.unnamed.hephaestus.model.view.ModelView;

public class SummonCommand implements CommandClass {

    // TODO: This shit is temporal
    private final ModelViewRenderer modelViewRenderer = AnimationEnginePlugin.getRenderer();
    private final ModelViewAnimator modelViewAnimator = AnimationEnginePlugin.getAnimator();

    @Command(names = "summon")
    public void onCommand(
            @Sender Player player,
            Model model,
            @OptArg ModelAnimation animation
    ) {
        Location location = player.getLocation();

        ModelView entity = modelViewRenderer.render(player, model, location);
        player.sendMessage("Model '" + model.getName() + "' summoned.");

        if (animation != null) {
            this.modelViewAnimator.animate(entity, animation);
        }
    }
}