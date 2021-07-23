package team.unnamed.hephaestus.model.view;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.struct.Quaternion;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

public class DefaultModelViewAnimator implements ModelViewAnimator {

    private final Plugin plugin;

    public DefaultModelViewAnimator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int animate(ModelView entity) {
        return new AnimationTask(entity)
                .runTaskTimerAsynchronously(plugin, 0L, 1L)
                .getTaskId();
    }

    static class AnimationTask extends BukkitRunnable {

        private final ModelView entity;

        public AnimationTask(ModelView entity) {
            this.entity = entity;
        }

        private void updateBone(
                double yaw,
                ModelBone parent,
                ModelBone bone,
                EulerAngle parentRotation,
                Vector3Float parentPosition
        ) {


            Vector3Float framePosition = entity.getAnimationQueue().currentPosition(bone)
                    .divide(16)
                    .multiply(1, 1, -1);

            EulerAngle frameRotation = entity.getAnimationQueue().currentRotation(bone);
            int modelData = entity.getAnimationQueue().currentModelData(bone);

            Vector3Float defaultPosition = bone.getOffset().multiply(1, 1, -1);
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
            if (modelData != -1) {
                entity.updateBoneModelData(bone, modelData);
            }

            for (ModelBone component : bone.getBones()) {
                this.updateBone(
                        yaw,
                        bone,
                        component,
                        globalRotation,
                        globalPosition
                );
            }
        }

        @Override
        public void run() {
            entity.getAnimationQueue().incrementTick();

            double bodyYaw = Math.toRadians(this.entity.getLocation().getYaw());

            for (ModelBone bone : this.entity.getModel().getGeometry().getBones()) {
                this.updateBone(
                        bodyYaw,
                        null,
                        bone,
                        EulerAngle.ZERO,
                        Vector3Float.ZERO
                );
            }
        }
    }
}