package team.unnamed.hephaestus.bukkit.plugin.integration.citizens;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.bukkit.plugin.HephaestuserProvider;

@TraitName(ModelTrait.NAME)
public final class ModelTrait extends Trait {
    public static final String NAME = "model";

    private String modelName;

    public ModelTrait() {
        super(NAME);
    }

    public @Nullable String modelName() {
        return modelName;
    }

    @Override
    public void load(final @NotNull DataKey key) {
        this.modelName = key.getString("name");
    }

    @Override
    public void save(final @NotNull DataKey key) {
        key.setString("name", this.modelName);
    }

    public void model(final @NotNull Model model) {
        this.modelName = model.name();
        startGlobalTrackingOnNPC(model);
    }

    @Override
    public void onDespawn() {
        final var entity = getNPC().getEntity();
        // todo: remove model view
    }

    @Override
    public void onSpawn() {
        final var hephaestuser = HephaestuserProvider.get();

        if (modelName == null) {
            hephaestuser.plugin().getLogger().severe("Model name not found for NPC: " + getNPC().getId() + " with name (" + getNPC().getFullName() + ")");
            return;
        }

        final var model = hephaestuser.registry().model(modelName);

        if (model == null) {
            hephaestuser.plugin().getLogger().severe("Model not found: " + modelName + " for NPC: " + getNPC().getId() + " with name (" + getNPC().getFullName() + ")");
            return;
        }

        // Delay the start of the global tracking, so the Citizens entity tracker
        // is set and doesn't override ours, see their code on replacing here:
        // (Note that they call traits onSpawn() before they replace the tracker)
        // https://github.com/CitizensDev/Citizens2/blob/master/main/src/main/java/net/citizensnpcs/npc/CitizensNPC.java#L393
        Bukkit.getScheduler().runTaskLater(hephaestuser.plugin(), () -> startGlobalTrackingOnNPC(model), 1L);
    }

    private void startGlobalTrackingOnNPC(final @NotNull Model model) {
        final var entity = getNPC().getEntity();
        final var engine = HephaestuserProvider.get().engine();
        final var view = engine.createView(model, entity.getLocation());
        engine.tracker().startGlobalTrackingOn(view, entity);
    }
}
