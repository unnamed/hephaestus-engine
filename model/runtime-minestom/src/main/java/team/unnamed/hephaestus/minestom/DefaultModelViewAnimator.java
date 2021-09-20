package team.unnamed.hephaestus.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.utils.time.TimeUnit;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.struct.Quaternion;
import team.unnamed.hephaestus.struct.Vector3Double;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

public class DefaultModelViewAnimator implements ModelViewAnimator {

    @Override
    public int animate(ModelView entity) {
        return new AnimationTask(entity).taskId;
    }

    static class AnimationTask implements Runnable {

        private final ModelView entity;
        private final int taskId;

        public AnimationTask(ModelView entity) {
            this.entity = entity;
            this.taskId = MinecraftServer.getSchedulerManager()
                    .buildTask(this)
                    .repeat(1, TimeUnit.SERVER_TICK)
                    .schedule()
                    .getId();
        }

        private void updateBone(
                double yaw,
                ModelBone parent,
                ModelBone bone,
                Vector3Double parentRotation,
                Vector3Float parentPosition
        ) {


            Vector3Float framePosition = entity.getAnimationQueue().currentPosition(bone)
                    .divide(16)
                    .multiply(1, 1, -1);

            Vector3Double frameRotation = entity.getAnimationQueue().currentRotation(bone);
            //int modelData = entity.getAnimationQueue().currentModelData(bone);

            Vector3Float defaultPosition = bone.getOffset().multiply(1, 1, -1);
            Vector3Double defaultRotation = Vectors.toRadians(bone.getRotation());

            Vector3Float localPosition = framePosition.add(defaultPosition);
            Vector3Double localRotation = defaultRotation.add(frameRotation.getX(), frameRotation.getY(), frameRotation.getZ());

            Vector3Float globalPosition;
            Vector3Double globalRotation;

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

            Pos worldPosition = this.entity.getPosition().add(
                    globalPosition.getX(),
                    globalPosition.getY(),
                    globalPosition.getZ()
            );

            entity.teleportBone(bone, worldPosition);
            entity.setBonePose(bone, globalRotation);
            //if (modelData != -1) {
            //    entity.updateBoneModelData(bone, modelData);
            //}

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

            double bodyYaw = Math.toRadians(this.entity.getPosition().yaw());

            for (ModelBone bone : this.entity.getModel().getBones()) {
                this.updateBone(
                        bodyYaw,
                        null,
                        bone,
                        Vector3Double.ZERO,
                        Vector3Float.ZERO
                );
            }
        }
    }
}