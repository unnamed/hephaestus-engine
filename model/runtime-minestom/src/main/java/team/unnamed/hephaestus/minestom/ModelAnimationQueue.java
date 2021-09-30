package team.unnamed.hephaestus.minestom;

import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.animation.KeyFrame;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelBoneAnimation;
import team.unnamed.hephaestus.struct.Quaternion;
import team.unnamed.hephaestus.struct.Vector3Double;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.KeyFrames;
import team.unnamed.hephaestus.util.Vectors;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// todo: this can be done more efficiently
public class ModelAnimationQueue {

    private final Map<ModelBone, Vector3Float> bonesLastPosition = new ConcurrentHashMap<>();
    private final Map<ModelBone, Vector3Double> bonesLastRotation = new ConcurrentHashMap<>();

    private final List<QueuedAnimation> queuedAnimations = new LinkedList<>();

    public Vector3Float currentPosition(ModelBone bone) {
        QueuedAnimation animation = this.getAnimationForBone(bone);

        if (animation == null) {
            return bonesLastPosition.getOrDefault(bone, Vector3Float.ZERO);
        }

        ModelBoneAnimation boneAnimation = animation.getAnimationsByBoneName().get(bone.getName());

        KeyFrame previousPositionFrame = KeyFrames.getPrevious(animation.getTick(), boneAnimation.getPositionFrames());
        KeyFrame nextPositionFrame = KeyFrames.getNext(animation.getTick(), boneAnimation.getPositionFrames());

        Vector3Float framePosition = previousPositionFrame.getValue();
        if (!animation.isTransitioned()) {
            float ratio = (float) animation.getTick() / (float) animation.getTransitionTicks();

            Vector3Float initialPosition = KeyFrames.getPrevious(0, boneAnimation.getPositionFrames()).getValue();

            framePosition = Vectors.lerp(
                    bonesLastPosition.getOrDefault(bone, Vector3Float.ZERO),
                    initialPosition,
                    ratio
            );
        } else if (nextPositionFrame != null) {
            float ratio = (float) (animation.getTick() - previousPositionFrame.getPosition())
                    / (nextPositionFrame.getPosition() - previousPositionFrame.getPosition());

            framePosition = Vectors.lerp(
                    previousPositionFrame.getValue(),
                    nextPositionFrame.getValue(),
                    ratio
            );
            bonesLastPosition.put(bone, framePosition);
        } else {
            bonesLastPosition.put(bone, framePosition);
        }

        return framePosition;
    }

    public Vector3Double currentRotation(ModelBone bone) {
        QueuedAnimation animation = this.getAnimationForBone(bone);

        if (animation == null) {
            return bonesLastRotation.getOrDefault(bone, Vector3Double.ZERO);
        }

        ModelBoneAnimation boneAnimation = animation.getAnimationsByBoneName().get(bone.getName());

        KeyFrame previousRotationFrame = KeyFrames.getPrevious(animation.getTick(), boneAnimation.getRotationFrames());
        KeyFrame nextRotationFrame = KeyFrames.getNext(animation.getTick(), boneAnimation.getRotationFrames());

        Vector3Double frameRotation = Vectors.toRadians(previousRotationFrame.getValue());
        if (!animation.isTransitioned()) {
            float ratio = (float) animation.getTick() / (float) animation.getTransitionTicks();

            Vector3Double initialRotation = Vectors.toRadians(KeyFrames.getPrevious(0, boneAnimation.getRotationFrames()).getValue());

            frameRotation = Quaternion.lerp(
                    bonesLastRotation.getOrDefault(bone, Vector3Double.ZERO),
                    initialRotation,
                    ratio
            );
        } else if (nextRotationFrame != null) {
            float ratio = (float) (animation.getTick() - previousRotationFrame.getPosition())
                    / (nextRotationFrame.getPosition() - previousRotationFrame.getPosition());

            frameRotation = Quaternion.lerp(
                    Vectors.toRadians(previousRotationFrame.getValue()),
                    Vectors.toRadians(nextRotationFrame.getValue()),
                    ratio
            );
            bonesLastRotation.put(bone, frameRotation);
        } else {
            bonesLastRotation.put(bone, frameRotation);
        }

        return frameRotation;
    }

    private QueuedAnimation findRecursively(ModelBone bone) {
        QueuedAnimation queuedAnimation = this.getAnimationForBone(bone);

        if (queuedAnimation == null && bone.getParent() != null) {
            queuedAnimation = findRecursively(bone.getParent());
        }

        return queuedAnimation;
    }

    public int currentModelData(ModelBone bone) {
        QueuedAnimation animation = this.findRecursively(bone);

        if (animation == null) {
            return -1;
        }

        return animation.getModelData().getOrDefault(bone.getName(), Collections.emptyMap())
                .getOrDefault(animation.getTick(), -1);
    }

    public void pushAnimation(ModelAnimation animation, int priority, int transitionTicks) {
        queuedAnimations.add(new QueuedAnimation(animation, priority, transitionTicks));
    }

    public void clear() {
        this.queuedAnimations.clear();
    }

    public void removeAnimation(String name) {
        this.queuedAnimations.removeIf(
                animation -> animation.getName().equals(name)
        );
    }

    public void incrementTick() {
        this.queuedAnimations.forEach(QueuedAnimation::incrementTick);
    }

    public List<QueuedAnimation> getQueuedAnimations() {
        return queuedAnimations;
    }

    private QueuedAnimation getAnimationForBone(ModelBone bone) {
        QueuedAnimation realAnimation = null;

        for (QueuedAnimation animation : queuedAnimations) {
            ModelBoneAnimation boneAnimation = animation.getAnimationsByBoneName().get(bone.getName());

            if (realAnimation == null && boneAnimation != null) {
                realAnimation = animation;
            } else if (
                    realAnimation != null
                            && boneAnimation != null
                            && realAnimation.getPriority() < animation.getPriority()
            ) {
                realAnimation = animation;
            }
        }

        if (realAnimation != null && !realAnimation.isTransitioned() && realAnimation.getTick() >= realAnimation.getTransitionTicks()) {
            System.out.println("skeree");
            realAnimation.setTransitioned();
            realAnimation.resetTick();
        }

        if (realAnimation != null && realAnimation.isTransitioned() && realAnimation.getTick() >= realAnimation.getAnimationLength()) {
            System.out.println("a");
            if (!realAnimation.isLoop()) {
                System.out.println("not loop");
                queuedAnimations.remove(realAnimation);
                return getAnimationForBone(bone);
            } else {
                realAnimation.resetTick();
            }
        }

        return realAnimation;
    }

    public static class QueuedAnimation extends ModelAnimation {

        private final Map<String, Map<Integer, Integer>> modelData;

        private final int priority;
        private final int transitionTicks;
        private boolean transitioned;
        private int tick = 1;

        public QueuedAnimation(ModelAnimation animation, int priority, int transitionTicks) {
            super(
                    animation.getName(),
                    animation.isLoop(),
                    animation.getAnimationLength(),
                    animation.getAnimationsByBoneName(),
                    animation.getModelData()
            );
            this.modelData = animation.getModelData();
            this.priority = priority;
            this.transitionTicks = transitionTicks;
            this.transitioned = transitionTicks == 0;
        }

        @Override
        public Map<String, Map<Integer, Integer>> getModelData() {
            return this.modelData;
        }

        public int getPriority() {
            return priority;
        }

        public int getTick() {
            return tick;
        }

        public int getTransitionTicks() {
            return transitionTicks;
        }

        public boolean isTransitioned() {
            return transitioned;
        }

        public void incrementTick() {
            this.tick++;
        }

        public void resetTick() {
            tick = 1;
        }

        public void setTransitioned() {
            this.transitioned = true;
        }
    }
}