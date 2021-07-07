package team.unnamed.hephaestus.animation;

import org.bukkit.util.EulerAngle;
import team.unnamed.hephaestus.model.ModelBone;
import team.unnamed.hephaestus.model.animation.FrameProvider;
import team.unnamed.hephaestus.model.animation.KeyFrame;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelBoneAnimation;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.util.List;

public class ModelFrameProvider implements FrameProvider {

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
                } else if(frame.getPosition() > selectedFrame.getPosition()) {
                    selectedFrame = frame;
                }
            }
        }

        if (selectedFrame == null) {
            return new KeyFrame(0, new Vector3Float(0, 0, 0));
        }

        return selectedFrame;
    }

    @Override
    public Vector3Float providePosition(float tick, ModelAnimation animation, ModelBone bone) {
        ModelBoneAnimation boneAnimation = animation.getAnimationsByBoneName().get(bone.getName());

        if (boneAnimation == null) {
            return Vector3Float.ZERO;
        }

        KeyFrame previousPositionFrame = getPrevious(tick, boneAnimation.getPositionFrames());
        KeyFrame nextPositionFrame = getNext(tick, boneAnimation.getPositionFrames());

        Vector3Float framePosition = previousPositionFrame.getValue();
        if (nextPositionFrame != null) {
            float ratio = (tick - previousPositionFrame.getPosition()) / (nextPositionFrame.getPosition() - previousPositionFrame.getPosition());
            framePosition = Vectors.lerp(
                    previousPositionFrame.getValue(),
                    nextPositionFrame.getValue(),
                    ratio
            );
        }

        return framePosition;
    }

    @Override
    public EulerAngle provideRotation(float tick, ModelAnimation animation, ModelBone bone) {
        ModelBoneAnimation boneAnimation = animation.getAnimationsByBoneName().get(bone.getName());

        if (boneAnimation == null) {
            return EulerAngle.ZERO;
        }

        KeyFrame previousRotationFrame = getPrevious(tick, boneAnimation.getRotationFrames());
        KeyFrame nextRotationFrame = getNext(tick, boneAnimation.getRotationFrames());

        EulerAngle frameRotation = previousRotationFrame.getValue().toEuler();
        if (nextRotationFrame != null) {
            float ratio = ((tick) - previousRotationFrame.getPosition()) / (nextRotationFrame.getPosition() - previousRotationFrame.getPosition());
            frameRotation = Vectors.lerp(
                    previousRotationFrame.getValue(),
                    nextRotationFrame.getValue(),
                    ratio
            ).toEuler();
        }

        return frameRotation;
    }
}