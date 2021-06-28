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

        private static KeyFrame selectFrame(List<KeyFrame> frames, float tick) {
            tick /= 20; // convert tick to second TODO: This can be more efficient
            KeyFrame selectedFrame = null;
            for (KeyFrame frame : frames) {
                if (frame.getPosition() > tick) {
                    break;
                } else {
                    selectedFrame = frame;
                }
            }
            return selectedFrame;
        }

        private void updateBone(
                ModelBone bone,
                Vector3Float parentGlobalPosition,
                Vector3Float offset,
                EulerAngle parentGlobalRotation,
                float tick
        ) {
            ArmorStand boneEntity = (ArmorStand) Bukkit.getEntity(this.entity.getEntities().get(bone.getName()));
            if (boneEntity == null) {
                return;
            }

            ModelBoneAnimation boneAnimation = animation.getAnimationsByBoneName().get(bone.getName());
            KeyFrame positionFrame = selectFrame(boneAnimation.getPositionFrames(), tick);
            KeyFrame rotationFrame = selectFrame(boneAnimation.getRotationFrames(), tick);

            Vector3Float localPosition = (positionFrame == null
                    ? Vector3Float.zero()
                    : positionFrame.getValue()
            );

            EulerAngle localRotation = rotationFrame == null
                    ? new EulerAngle(0, 0, 0)
                    : new EulerAngle(
                            Math.toRadians(rotationFrame.getValue().getX()),
                            Math.toRadians(rotationFrame.getValue().getY()),
                            Math.toRadians(rotationFrame.getValue().getZ())
                    );

            EulerAngle globalRotation = Quaternion.combine(localRotation, parentGlobalRotation);

            Quaternion rotationQuaternion = Quaternion.toQuaternion(globalRotation);
            Vector3Float correction = Quaternion.multiply(
                    rotationQuaternion,
                    new Quaternion(0,
                            parentGlobalPosition.subtract(bone.getLocalOffset())
                    )
            ).getVec();


            Vector3Float globalPosition = Vectors.rotate(
                    localPosition.add(offset).add(bone.getLocalOffset().multiply(1, 1, -1)).add(correction),
                    -this.entity.getLocation().getYaw() * 0.017453292F
            );

            Location worldPosition = this.entity.getLocation().clone().add(new Vector(
                    globalPosition.getX(),
                    globalPosition.getY(),
                    globalPosition.getZ()
            ));

            boneEntity.setHeadPose(globalRotation);
            boneEntity.teleport(worldPosition);

            for (ModelComponent component : bone.getComponents()) {
                if (component instanceof ModelBone) {
                    this.updateBone((ModelBone) component, bone.getLocalOffset(), offset.add(localPosition), globalRotation, tick);
                }
            }
        }

        @Override
        public void run() {

            if (animation.isLoop() && entity.getTick() > animation.getAnimationLength()*20) {
                entity.resetTick();
            }

            for (ModelBone bone : this.entity.getModel().getGeometry().getBones()) {
                this.updateBone(bone, Vector3Float.zero(), Vector3Float.zero(), new EulerAngle(0, 0, 0), entity.getTick());
            }

            entity.increaseTick();
        }
    }

}