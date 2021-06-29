package team.unnamed.hephaestus.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.ModelComponent;
import team.unnamed.hephaestus.model.animation.KeyFrame;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelBoneAnimation;
import team.unnamed.hephaestus.model.entity.ModelEntityAnimator;
import team.unnamed.hephaestus.model.entity.ModelLivingEntity;
import team.unnamed.hephaestus.struct.Quaternion;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.util.Iterator;
import java.util.List;

public class ModelLivingEntityAnimator implements ModelEntityAnimator {

    private final Plugin plugin;

    public ModelLivingEntityAnimator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int animate(ModelLivingEntity entity, ModelAnimation animation) {

        entity.resetTick();
        entity.setAnimation(animation);

        return Bukkit.getScheduler()
                .runTaskTimer(
                        plugin,
                        new AnimationTask(entity, animation),
                        0L,
                        1L
                )
                .getTaskId();
    }

    public static class AnimationTask implements Runnable {

        private final ModelLivingEntity entity;
        private final ModelAnimation animation;

        public AnimationTask(ModelLivingEntity entity, ModelAnimation animation) {
            this.entity = entity;
            this.animation = animation;
        }

        private static KeyFrame getNext(float tick, List<KeyFrame> frames) {
            tick /= 20;
            KeyFrame selectedFrame = null;
            for (KeyFrame frame : frames) {
                if (frame.getPosition() > tick){
                    if (selectedFrame == null) {
                        selectedFrame = frame;
                    } else if (frame.getPosition() < selectedFrame.getPosition()) {
                        selectedFrame = frame;
                    }
                }
            }

            return selectedFrame;
        }

        private static KeyFrame getPrevious(float tick, List<KeyFrame> frames) {
            tick /= 20;
            KeyFrame selectedFrame = null;
            for (KeyFrame frame : frames) {
                if (frame.getPosition() <= tick) {
                    if (selectedFrame == null) {
                        selectedFrame = frame;
                    } else if(frame.getPosition() > selectedFrame.getPosition()) {
                        selectedFrame = frame;
                    }
                }
            }

            return selectedFrame;
        }

        private void updateBone(
                ModelBone bone,
                Vector3Float offset,
                EulerAngle angleOffset,
                float tick
        ) {
            ArmorStand boneEntity = (ArmorStand) Bukkit.getEntity(this.entity.getEntities().get(bone.getName()));
            if (boneEntity == null) {
                return;
            }

            ModelBoneAnimation boneAnimation = animation.getAnimationsByBoneName().get(bone.getName());

            KeyFrame previousPositionFrame = boneAnimation == null ? new KeyFrame(tick, Vector3Float.zero())
                    : getPrevious(tick, boneAnimation.getPositionFrames());

            KeyFrame nextPositionFrame = boneAnimation == null ? new KeyFrame(tick, Vector3Float.zero())
                    : getNext(tick, boneAnimation.getPositionFrames());

            if (previousPositionFrame == null) {
                previousPositionFrame = new KeyFrame(tick, Vector3Float.zero());
            }

            if (nextPositionFrame == null) {
                nextPositionFrame = new KeyFrame(tick, Vector3Float.zero());
            }

            KeyFrame previousRotationFrame = boneAnimation == null ? new KeyFrame(tick, Vector3Float.zero())
                    : getPrevious(tick, boneAnimation.getRotationFrames());

            KeyFrame nextRotationFrame = boneAnimation == null ? new KeyFrame(tick, Vector3Float.zero())
                    : getNext(tick, boneAnimation.getRotationFrames());

            if (previousRotationFrame == null) {
                previousRotationFrame = new KeyFrame(tick, Vector3Float.zero());
            }

            if (nextRotationFrame == null) {
                nextRotationFrame = new KeyFrame(tick, Vector3Float.zero());
            }

            Vector3Float positionAdd = Vectors.lerp(
                    previousPositionFrame.getValue(),
                    nextPositionFrame.getValue(),
                    (tick / 20) / nextPositionFrame.getPosition()
            ).multiply(1, 1, -1).divide(10);

            Vector3Float rotationVector = Vectors.lerp(
                    previousRotationFrame.getValue(),
                    nextRotationFrame.getValue(),
                    (tick / 20) / nextPositionFrame.getPosition()
            );

            EulerAngle rotationAdd = new EulerAngle(
                    Math.toRadians(rotationVector.getX()),
                    Math.toRadians(rotationVector.getY()),
                    Math.toRadians(rotationVector.getZ())
            );

            Vector3Float positionCorrection = bone.getLocalOffset().multiply(1, 1, -1)
                    .rotateAroundX(rotationVector.getX())
                    .rotateAroundY(rotationVector.getY())
                    .rotateAroundZ(rotationVector.getZ());

            Vector3Float globalPosition = Vectors.rotate(
                    positionAdd.add(offset).add(bone.getLocalOffset().multiply(1, 1, -1)),
                    -this.entity.getLocation().getYaw() * 0.017453292F
            );

            Location worldPosition = this.entity.getLocation().clone().add(
                    positionCorrection.getX(),
                    positionCorrection.getY(),
                    positionCorrection.getZ()
            );

            boneEntity.setHeadPose(angleOffset.add(rotationAdd.getX(), rotationAdd.getY(), rotationAdd.getZ()));
            boneEntity.teleport(worldPosition);

            for (ModelComponent component : bone.getComponents()) {
                if (component instanceof ModelBone) {
                    this.updateBone(
                            (ModelBone) component,
                            offset.add(positionAdd),
                            angleOffset.add(rotationAdd.getX(), rotationAdd.getY(), rotationAdd.getZ()),
                            tick
                    );
                }
            }
        }

        @Override
        public void run() {

            if (animation.isLoop() && entity.getTick() > animation.getAnimationLength()*20) {
                entity.resetTick();
            }

            for (ModelBone bone : this.entity.getModel().getGeometry().getBones()) {
                this.updateBone(
                        bone,
                        Vector3Float.zero(),
                        EulerAngle.ZERO,
                        entity.getTick()
                );
            }

            entity.increaseTick();
        }
    }

}