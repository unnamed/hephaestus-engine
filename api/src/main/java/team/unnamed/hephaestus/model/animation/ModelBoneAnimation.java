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
    private final List<KeyFrame> scaleFrames;

    public ModelBoneAnimation(
            List<KeyFrame> positionFrames,
            List<KeyFrame> rotationFrames,
            List<KeyFrame> scaleFrames
    ) {
        this.positionFrames = positionFrames;
        this.rotationFrames = rotationFrames;
        this.scaleFrames = scaleFrames;
    }

    public List<KeyFrame> getPositionFrames() {
        return positionFrames;
    }

    public List<KeyFrame> getRotationFrames() {
        return rotationFrames;
    }

    public List<KeyFrame> getScaleFrames() {
        return scaleFrames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelBoneAnimation that = (ModelBoneAnimation) o;
        return Objects.equals(positionFrames, that.positionFrames)
                && Objects.equals(rotationFrames, that.rotationFrames)
                && Objects.equals(scaleFrames, that.scaleFrames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionFrames, rotationFrames, scaleFrames);
    }

    @Override
    public String toString() {
        return "ModelBoneAnimation{" +
                "positionFrames=" + positionFrames +
                ", rotationFrames=" + rotationFrames +
                ", scaleFrames=" + scaleFrames +
                '}';
    }
}
