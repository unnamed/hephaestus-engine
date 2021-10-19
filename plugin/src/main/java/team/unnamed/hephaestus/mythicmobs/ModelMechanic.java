package team.unnamed.hephaestus.mythicmobs;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import team.unnamed.hephaestus.AnimationEnginePlugin;
import team.unnamed.hephaestus.ModelRegistry;
import team.unnamed.hephaestus.ModelViewRegistry;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.view.BukkitModelView;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

public class ModelMechanic
        extends SkillMechanic
        implements ITargetedEntitySkill {

    private static final float ARMORSTAND_HEIGHT = 0.726F;

    private final ModelRegistry registry;
    private final ModelViewRegistry viewRegistry;
    private final PlaceholderString modelName;
    private final ModelViewRenderer renderer;

    public ModelMechanic(
            ModelRegistry registry,
            ModelViewRegistry viewRegistry,
            MythicLineConfig config,
            ModelViewRenderer renderer
    ) {
        super(config.getLine(), config);

        this.registry = registry;
        this.viewRegistry = viewRegistry;
        this.modelName = config.getPlaceholderString("model", null);
        this.renderer = renderer;
    }

    @Override
    public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
        Model model = registry.get(modelName.get(data, target));
        Entity entity = target.getBukkitEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            living.setInvisible(true);
        }

        BukkitModelView view = renderer.render(model, entity.getLocation());
        viewRegistry.register(view);
        view.playAnimation("walk");
        // TODO: don't use a task per model
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                AnimationEnginePlugin.getPlugin(AnimationEnginePlugin.class),
                () -> {
                    // todo: cancel if 'entity' isn't valid anymore
                    view.teleport(entity.getLocation().subtract(0, ARMORSTAND_HEIGHT, 0));
                    view.tickAnimations();
                },
                0L,
                1L
        );
        return true;
    }

}
