package team.unnamed.hephaestus.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.animation.ModelFrameProvider;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.ModelComponent;
import team.unnamed.hephaestus.model.animation.FrameProvider;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.entity.ModelEntityAnimator;
import team.unnamed.hephaestus.model.entity.ModelLivingEntity;
import team.unnamed.hephaestus.struct.EulerOrder;
import team.unnamed.hephaestus.struct.Quaternion;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Offset;

//TODO: Pass entity handling to packet side and make animations 60 fps
public class ModelLivingEntityAnimator implements ModelEntityAnimator {

    private final Plugin plugin;
    private final FrameProvider frameProvider;

    public ModelLivingEntityAnimator(Plugin plugin) {
        this.plugin = plugin;
        this.frameProvider = new ModelFrameProvider();
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
                        6L
                )
                .getTaskId();
    }

    class AnimationTask implements Runnable {

        private final ModelLivingEntity entity;
        private final ModelAnimation animation;

        public AnimationTask(ModelLivingEntity entity, ModelAnimation animation) {
            this.entity = entity;
            this.animation = animation;
        }

        private void updateBone(
                ModelBone parent,
                ModelBone bone,
                EulerAngle parentRotation,
                Vector3Float parentPosition,
                float tick
        ) {
            ArmorStand boneEntity = (ArmorStand) Bukkit.getEntity(this.entity.getEntities().get(bone.getName()));
            if (boneEntity == null) {
                return;
            }

            Vector3Float framePosition = frameProvider.providePosition(tick, animation, bone).multiply(0.0625F);
            EulerAngle frameRotation = frameProvider.provideRotation(tick, animation, bone);

            Vector3Float defaultPosition = bone.getPivot().divide(16).multiply(1, 1, -1);
            EulerAngle defaultRotation = bone.getRotation().toEuler();

            Vector3Float localPosition = framePosition.add(defaultPosition);
            EulerAngle localRotation = defaultRotation.add(frameRotation.getX(), frameRotation.getY(), frameRotation.getZ());

            Vector3Float globalPosition;
            EulerAngle globalRotation;

            if (parent == null) {
                globalPosition = Offset.rotateYaw(localPosition, Math.toRadians(this.entity.getLocation().getYaw()));
                globalRotation = localRotation;
            } else {
                Quaternion rotation = Quaternion.fromEuler(
                        new EulerAngle(
                                -parentRotation.getX(),
                                parentRotation.getY(),
                                parentRotation.getZ()
                        ),
                        EulerOrder.XYZ
                );

                Quaternion positionQuaternion = Quaternion
                        .fromVector(parentPosition)
                        .premultiply(rotation.clone().conjugate())
                        .multiply(rotation);

                localPosition = new Vector3Float(
                        positionQuaternion.getX(),
                        positionQuaternion.getY(),
                        positionQuaternion.getZ()
                ).add(defaultPosition);

                globalPosition = Offset.rotateYaw(localPosition, Math.toRadians(this.entity.getLocation().getYaw()));

                globalRotation = Quaternion
                        .fromEuler(localRotation, EulerOrder.XYZ)
                        .multiply(Quaternion.fromEuler(parentRotation, EulerOrder.XYZ))
                        .toEuler(EulerOrder.XYZ);
            }

            Location worldPosition = this.entity.getLocation().clone().add(
                    globalPosition.getX(),
                    globalPosition.getY(),
                    globalPosition.getZ()
            );

            boneEntity.teleport(worldPosition);
            boneEntity.setHeadPose(globalRotation);

            for (ModelComponent component : bone.getComponents()) {
                if (component instanceof ModelBone) {
                    this.updateBone(
                            bone,
                            (ModelBone) component,
                            globalRotation,
                            globalPosition,
                            tick
                    );
                }
            }
        }

        @Override
        public void run() {

            if (animation.isLoop() && entity.getTick() > animation.getAnimationLength()) {
                entity.resetTick();
            }

            for (ModelBone bone : this.entity.getModel().getGeometry().getBones()) {
                this.updateBone(
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