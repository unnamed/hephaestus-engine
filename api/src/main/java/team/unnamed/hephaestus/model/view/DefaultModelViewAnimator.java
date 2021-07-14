package team.unnamed.hephaestus.model.view;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.animation.FrameProvider;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.struct.Quaternion;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

public class DefaultModelViewAnimator implements ModelViewAnimator {

    private final Plugin plugin;

    public DefaultModelViewAnimator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int animate(ModelView entity, ModelAnimation animation) {
        return new AnimationTask(entity, animation)
                .runTaskTimerAsynchronously(plugin, 0L, 1L)
                .getTaskId();
    }

    static class AnimationTask extends BukkitRunnable {

        private final ModelView entity;
        private final ModelAnimation animation;

        public AnimationTask(ModelView entity, ModelAnimation animation) {
            this.entity = entity;
            this.animation = animation;
        }

        private void updateBone(
                double yaw,
                ModelBone parent,
                ModelBone bone,
                EulerAngle parentRotation,
                Vector3Float parentPosition,
                float tick
        ) {

            Vector3Float framePosition = FrameProvider.providePosition(tick, animation, bone)
                    .divide(16)
                    .multiply(1, 1, -1);

            EulerAngle frameRotation = FrameProvider.provideRotation(tick, animation, bone);

            Vector3Float defaultPosition = bone.getLocalOffset().multiply(1, 1, -1);
            EulerAngle defaultRotation = bone.getRotation().toEuler();

            Vector3Float localPosition = framePosition.add(defaultPosition);
            EulerAngle localRotation = defaultRotation.add(frameRotation.getX(), frameRotation.getY(), frameRotation.getZ());

            Vector3Float globalPosition;
            EulerAngle globalRotation;

            if (parent == null) {
                globalPosition = Vectors.rotateAroundY(localPosition, yaw);
                globalRotation = localRotation;
            } else {
                globalPosition = Vectors.rotateAroundY(
                        Vectors.rotate(localPosition, parentRotation),
                        yaw
                ).add(parentPosition);
                globalRotation = Quaternion.combine(localRotation, parentRotation);
            }

            Location worldPosition = this.entity.getLocation().clone().add(
                    globalPosition.getX(),
                    globalPosition.getY(),
                    globalPosition.getZ()
            );

            entity.teleportBone(bone, worldPosition);
            entity.setBonePose(bone, globalRotation);

            for (ModelBone component : bone.getBones()) {
                this.updateBone(
                        yaw,
                        bone,
                        component,
                        globalRotation,
                        globalPosition,
                        tick
                );
            }
        }

        @Override
        public void run() {

            if (entity.getTick() > animation.getAnimationLength()) {
                if (animation.isLoop()) {
                    entity.resetTick();
                } else {
                    cancel();
                    return;
                }
            }

            double bodyYaw = Math.toRadians(this.entity.getLocation().getYaw());

            for (ModelBone bone : this.entity.getModel().getGeometry().getBones()) {
                this.updateBone(
                        bodyYaw,
                        null,
                        bone,
                        EulerAngle.ZERO,
                        Vector3Float.ZERO,
                        entity.getTick()
                );
            }

            entity.increaseTick();
        }
    }
}