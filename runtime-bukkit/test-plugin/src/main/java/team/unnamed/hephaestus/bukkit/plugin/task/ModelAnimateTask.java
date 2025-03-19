package team.unnamed.hephaestus.bukkit.plugin.task;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.bukkit.ModelView;
import team.unnamed.hephaestus.bukkit.plugin.registry.ModelRegistry;
import team.unnamed.hephaestus.util.Quaternion;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

// TODO: This is unoptimized as hell and was made purely for the sake of the example
public final class ModelAnimateTask implements Runnable {
    private final ModelRegistry registry;

    private final Set<ModelView> viewsWithHeadRotationModifierAlreadyInjected = Collections.newSetFromMap(new WeakHashMap<>());
    private final Map<UUID, ModelViewData> data = new ConcurrentHashMap<>();

    public ModelAnimateTask(final @NotNull ModelRegistry registry) {
        this.registry = requireNonNull(registry, "registry");
    }

    @Override
    public void run() {
        final var now = System.currentTimeMillis();

        // reuse location instance
        final var location = new Location(null, 0, 0, 0);

        for (final var view : registry.views()) {
            final var base = view.base();

            if (base == null) {
                // Tried to tick a non-tracked view?
                continue;
            }

            base.getLocation(location);

            final var data = this.data.computeIfAbsent(base.getUniqueId(), k -> new ModelViewData(location.clone(), now));

            if (now - data.lastCheckTimestamp >= 200L) {
                if (location.distanceSquared(data.lastTrackedLocation) >= 0.1) {
                    // Moved!
                    if (data.animation == null || !data.animation.name().equals("walk")) {
                        final var walk = view.model().animations().get("walk");
                        view.animationPlayer().add(walk);
                        data.animation = walk;
                    }
                } else {
                    // Didn't move
                    if (data.animation == null || !data.animation.name().equals("idle")) {
                        final var idle = view.model().animations().get("idle");
                        view.animationPlayer().add(idle);
                        data.animation = idle;
                    }
                }
                data.lastTrackedLocation = location.clone();
                data.lastCheckTimestamp = now;
            }

            // tick the view
            if (base instanceof LivingEntity livingBase){
                // Use body yaw for living entities
                view.animationPlayer().tick(livingBase.getBodyYaw(), 0F);

                // Rotate heads too
                if (viewsWithHeadRotationModifierAlreadyInjected.add(view)) {
                    view.bones()
                            .stream()
                            .filter(bone -> bone.bone().name().startsWith("head"))
                            .forEach(bone -> bone.modifying(new BoneModifier() {
                                @Override
                                public @NotNull Quaternion modifyRotation(final @NotNull Quaternion original) {
                                    return Quaternion.fromEulerRadians(0D, Math.toRadians(livingBase.getBodyYaw() - livingBase.getYaw()), 0D)
                                            .multiply(Quaternion.fromEulerRadians(-Math.toRadians(livingBase.getPitch()), 0D, 0D))
                                            .multiply(original);
                                }
                            }));
                }
            } else {
                view.animationPlayer().tick(base.getYaw(), 0F);
            }
        }
    }

    private static class ModelViewData {
        private @NotNull Location lastTrackedLocation;
        private long lastCheckTimestamp;
        private Animation animation;

        private ModelViewData(final @NotNull Location lastTrackedLocation, final long lastCheckTimestamp) {
            this.lastTrackedLocation = lastTrackedLocation;
            this.lastCheckTimestamp = lastCheckTimestamp;
        }
    }
}
