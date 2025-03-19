package team.unnamed.hephaestus.bukkit.plugin.modifier;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.bukkit.ModelView;
import team.unnamed.hephaestus.view.modifier.BoneModifier;

import java.lang.ref.WeakReference;

import static java.util.Objects.requireNonNull;

public final class OnGroundBoneModifier implements BoneModifier {
    private final WeakReference<Entity> base;

    public OnGroundBoneModifier(final @NotNull Entity base) {
        this.base = new WeakReference<>(requireNonNull(base, "base"));
    }

    @Override
    public @NotNull Vector3Float modifyPosition(final @NotNull Vector3Float original) {
        final var base = this.base.get();
        if (base == null) {
            // base removed?
            return original;
        }
        return original.y(original.y() - (float) base.getHeight());
    }

    public void apply(final @NotNull ModelView view) {
        view.bones().forEach(bone -> bone.modifying(this));
    }
}
