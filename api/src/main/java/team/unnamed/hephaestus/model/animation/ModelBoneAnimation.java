package team.unnamed.hephaestus.model.animation;

import java.util.List;
import java.util.Objects;

/**
 * Represents a bone animation, compound by
 * {@link KeyFrame}. They can be separated by
 * position, rotation and scale
 */
public class ModelBoneAnimation {

    private final List<KeyFrame> positionFrames;
    private final List<KeyFrame> rotationFrames;

    public ModelBoneAnimation(
            List<KeyFrame> positionFrames,
            List<KeyFrame> rotationFrames
    ) {
        this.positionFrames = positionFrames;
        this.rotationFrames = rotationFrames;
    }

    public List<KeyFrame> getPositionFrames() {
        return positionFrames;
    }

    public List<KeyFrame> getRotationFrames() {
        return rotationFrames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelBoneAnimation that = (ModelBoneAnimation) o;
        return Objects.equals(positionFrames, that.positionFrames)
                && Objects.equals(rotationFrames, that.rotationFrames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionFrames, rotationFrames);
    }

    @Override
    public String toString() {
        return "ModelBoneAnimation{" +
                "positionFrames=" + positionFrames +
                ", rotationFrames=" + rotationFrames +
                '}';
    }
}