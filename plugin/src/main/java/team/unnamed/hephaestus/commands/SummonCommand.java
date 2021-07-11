package team.unnamed.hephaestus.commands;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.view.ModelViewAnimator;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;
import team.unnamed.hephaestus.model.view.ModelView;

public class SummonCommand implements CommandClass {

    private final ModelViewRenderer renderer;
    private final ModelViewAnimator animator;

    public SummonCommand(ModelViewRenderer renderer, ModelViewAnimator animator) {
        this.renderer = renderer;
        this.animator = animator;
    }

    @Command(names = "summon")
    public void onCommand(
            @Sender Player player,
            Model model,
            @OptArg ModelAnimation animation
    ) {
        Location location = player.getLocation();

        ModelView entity = renderer.render(player, model, location);
        player.sendMessage("Model '" + model.getName() + "' summoned.");

        if (animation != null) {
            this.animator.animate(entity, animation);
        }
    }
}