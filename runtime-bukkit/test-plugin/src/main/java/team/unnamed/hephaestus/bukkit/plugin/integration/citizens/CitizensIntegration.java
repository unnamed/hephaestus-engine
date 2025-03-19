package team.unnamed.hephaestus.bukkit.plugin.integration.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.bukkit.plugin.integration.PluginIntegration;

public final class CitizensIntegration implements PluginIntegration {
    @Override
    public @NotNull String plugin() {
        return "Citizens";
    }

    @Override
    public void enable(final @NotNull Plugin plugin) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ModelTrait.class));
    }
}
