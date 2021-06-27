package team.unnamed.hephaestus.commands;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.AnimationEnginePlugin;
import team.unnamed.hephaestus.model.*;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.entity.ModelEntityAnimator;
import team.unnamed.hephaestus.model.entity.ModelEntitySpawner;
import team.unnamed.hephaestus.model.entity.ModelLivingEntity;

import java.util.Optional;
import java.util.stream.Collectors;

public class SummonCommand implements CommandClass {

    // TODO: This shit is temporal
    private final ModelEntitySpawner modelEntitySpawner = AnimationEnginePlugin.getSpawner();
    private final ModelEntityAnimator modelEntityAnimator = AnimationEnginePlugin.getAnimator();

    @Command(names = "summon")
    public void onCommand(
            @Sender Player player,
            Model model,
            String animation
    ) {
        Location location = player.getLocation();

        System.out.println("animasiones XD: " + model.getAnimations().stream().map(ModelAnimation::getName).collect(Collectors.joining(", ")));
        
        ModelLivingEntity entity = modelEntitySpawner.spawn(model, location);
        // TODO: puta madre flex te dije que debia ser un Map<String, ModelAnimation> :gatoXD:
        
        Optional<ModelAnimation> queried = model.getAnimations()
                .stream()
                .filter(a -> a.getName().equals(animation))
                .findFirst();
        
        if (!queried.isPresent()) {
            player.sendMessage(":monda:");
        } else {
            this.modelEntityAnimator.animate(
                    entity, queried.get()
            );
            player.sendMessage(
                    "sumoneado puta. el id de la entidad es "
                            + entity.getId() + ". Si se summoneo bien: k pro q sos. Si no: dale el bisio al model"
            );
        }
    }

}