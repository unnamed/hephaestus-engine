package team.unnamed.hephaestus.bukkit.plugin.integration.mythicmobs.mechanic.disguise;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.bukkit.plugin.HephaestuserProvider;
import team.unnamed.hephaestus.bukkit.plugin.modifier.OnGroundBoneModifier;
import team.unnamed.hephaestus.view.track.ModelViewTrackingRule;

@MythicMechanic(name = "disguise", aliases = "modeldisguise")
public final class DisguiseMechanic implements ITargetedEntitySkill {
    private final String modelName;
    private final boolean seeSelf;

    public DisguiseMechanic(final @NotNull MythicLineConfig config) {
        this.modelName = config.getString(new String[] { "model", "mid", "m" });
        this.seeSelf = config.getBoolean(new String[] { "see", "s" }, true);
    }

    @Override
    public @NotNull SkillResult castAtEntity(final @NotNull SkillMetadata meta, final @NotNull AbstractEntity target) {
        if (!target.isPlayer()) {
            // Only players can be disguised with this mechanic
            return SkillResult.INVALID_TARGET;
        }

        final var player = (Player) target.getBukkitEntity();
        final var model = HephaestuserProvider.get().registry().model(modelName);

        if (model == null) {
            // Unknown model!
            return SkillResult.INVALID_CONFIG;
        }

        // Create view for the player
        final var view = HephaestuserProvider.get().engine().spawn(model, player,
                seeSelf ? ModelViewTrackingRule.all() : ($, viewer) -> viewer != player);

        // TODO: Metadata
        player.setInvisible(true);

        // Make the model be on the ground (moves all the bones down by the player height)
        new OnGroundBoneModifier(player).apply(view);

        // Register the view so it's animated
        HephaestuserProvider.get().registry().view(view);

        return SkillResult.SUCCESS;
    }
}
