package team.unnamed.hephaestus.commands;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.AnimationEnginePlugin;
import team.unnamed.hephaestus.model.*;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.entity.ModelEntityAnimator;
import team.unnamed.hephaestus.model.entity.ModelEntitySpawner;
import team.unnamed.hephaestus.model.entity.ModelLivingEntity;

public class SummonCommand implements CommandClass {

    // TODO: This shit is temporal
    private final ModelEntitySpawner modelEntitySpawner = AnimationEnginePlugin.getSpawner();
    private final ModelEntityAnimator modelEntityAnimator = AnimationEnginePlugin.getAnimator();

    @Command(names = "summon")
    public void onCommand(
            @Sender Player player,
            Model model,
            @OptArg ModelAnimation animation
    ) {
        Location location = player.getLocation();

        ModelLivingEntity entity = modelEntitySpawner.spawn(model, location);
        player.sendMessage("Model '" + model.getName() + "' summoned. Entity ID is " + entity.getId());

        if (animation != null) {
            this.modelEntityAnimator.animate(entity, animation);
        }
    }
}