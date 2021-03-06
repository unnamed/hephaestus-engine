package team.unnamed.hephaestus.model.animation;

import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.struct.Quaternion;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelAnimationQueue {

    private final Map<ModelBone, Vector3Float> bonesLastPosition = new ConcurrentHashMap<>();
    private final Map<ModelBone, EulerAngle> bonesLastRotation = new ConcurrentHashMap<>();

    private final List<QueuedAnimation> queuedAnimations = new LinkedList<>();

    public Vector3Float currentPosition(ModelBone bone) {
        QueuedAnimation animation = this.getAnimationForBone(bone);

        if (animation == null) {
            return Vector3Float.ZERO;
        }

        ModelBoneAnimation boneAnimation = animation.getAnimationsByBoneName().get(bone.getName());

        KeyFrame previousPositionFrame = this.getPrevious(animation.getTick(), boneAnimation.getPositionFrames());
        KeyFrame nextPositionFrame = this.getNext(animation.getTick(), boneAnimation.getPositionFrames());

        Vector3Float framePosition = previousPositionFrame.getValue();
        if (!animation.isTransitioned()) {
            float ratio = (float) animation.getTick() / (float) animation.getTransitionTicks();

            Vector3Float initialPosition = getPrevious(0, boneAnimation.getPositionFrames()).getValue();

            framePosition = Vectors.lerp(
                    bonesLastPosition.getOrDefault(bone, Vector3Float.ZERO),
                    initialPosition,
                    ratio
            );
        } else if (nextPositionFrame != null) {
            float ratio = (
                    animation.getTick() - previousPositionFrame.getPosition()
            ) / (nextPositionFrame.getPosition() - previousPositionFrame.getPosition());

            framePosition = Vectors.lerp(
                    previousPositionFrame.getValue(),
                    nextPositionFrame.getValue(),
                    ratio
            );
            bonesLastPosition.put(bone, framePosition);
        }

        return framePosition;
    }

    public EulerAngle currentRotation(ModelBone bone) {
        QueuedAnimation animation = this.getAnimationForBone(bone);

        if (animation == null) {
            return EulerAngle.ZERO;
        }

        ModelBoneAnimation boneAnimation = animation.getAnimationsByBoneName().get(bone.getName());

        KeyFrame previousRotationFrame = this.getPrevious(animation.getTick(), boneAnimation.getRotationFrames());
        KeyFrame nextRotationFrame = this.getNext(animation.getTick(), boneAnimation.getRotationFrames());

        EulerAngle frameRotation = previousRotationFrame.getValue().toEuler();
        if (!animation.isTransitioned()) {
            float ratio = (float) animation.getTick() / (float) animation.getTransitionTicks();

            EulerAngle initialRotation = getPrevious(0, boneAnimation.getRotationFrames()).getValue().toEuler();

            frameRotation = Quaternion.lerp(
                    bonesLastRotation.getOrDefault(bone, EulerAngle.ZERO),
                    initialRotation,
                    ratio
            );
        } else if (nextRotationFrame != null) {
            float ratio = (
                    animation.getTick() - previousRotationFrame.getPosition()
            ) / (nextRotationFrame.getPosition() - previousRotationFrame.getPosition());

            frameRotation = Quaternion.lerp(
                    previousRotationFrame.getValue().toEuler(),
                    nextRotationFrame.getValue().toEuler(),
                    ratio
            );
            bonesLastRotation.put(bone, frameRotation);
        }

        return frameRotation;
    }

    public List<QueuedAnimation> getQueuedAnimations() {
        return queuedAnimations;
    }

    public void pushAnimation(ModelAnimation animation, int priority, int transitionTicks) {
        queuedAnimations.add(new QueuedAnimation(animation, priority, transitionTicks));
    }

    public void incrementTick() {
        this.queuedAnimations.forEach(QueuedAnimation::incrementTick);
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

        if (realAnimation != null && !realAnimation.isTransitioned() && realAnimation.getTick() > realAnimation.getTransitionTicks()) {
            realAnimation.setTransitioned();
            realAnimation.resetTick();
        }

        if (realAnimation != null && realAnimation.isTransitioned() && realAnimation.getTick() > realAnimation.getAnimationLength()) {
            realAnimation.resetTick();
            if (!realAnimation.isLoop()) {
                queuedAnimations.remove(realAnimation);
            }

            return getAnimationForBone(bone);
        }

        return realAnimation;
    }

    private KeyFrame getNext(float tick, List<KeyFrame> frames) {
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

    private KeyFrame getPrevious(float tick, List<KeyFrame> frames) {
        KeyFrame selectedFrame = null;
        for (KeyFrame frame : frames) {
            if (frame.getPosition() <= tick) {
                if (selectedFrame == null) {
                    selectedFrame = frame;
                } else if (frame.getPosition() > selectedFrame.getPosition()) {
                    selectedFrame = frame;
                }
            }
        }

        if (selectedFrame == null) {
            return new KeyFrame(0, new Vector3Float(0, 0, 0));
        }

        return selectedFrame;
    }

    static class QueuedAnimation extends ModelAnimation {

        private final int priority;
        private final int transitionTicks;
        private boolean transitioned;
        private int tick = 1;

        public QueuedAnimation(ModelAnimation animation, int priority, int transitionTicks) {
            super(
                    animation.getName(),
                    animation.isLoop(),
                    animation.getAnimationLength(),
                    animation.getAnimationsByBoneName()
            );
            this.priority = priority;
            this.transitionTicks = transitionTicks;
            this.transitioned = transitionTicks == 0;
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