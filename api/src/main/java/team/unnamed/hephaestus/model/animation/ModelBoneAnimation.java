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
    private final List<KeyFrame> sizeFrames;

    public ModelBoneAnimation(
            List<KeyFrame> positionFrames,
            List<KeyFrame> rotationFrames,
            List<KeyFrame> sizeFrames) {
        this.positionFrames = positionFrames;
        this.rotationFrames = rotationFrames;
        this.sizeFrames = sizeFrames;
    }

    public List<KeyFrame> getPositionFrames() {
        return positionFrames;
    }

    public List<KeyFrame> getRotationFrames() {
        return rotationFrames;
    }

    public List<KeyFrame> getSizeFrames() {
        return sizeFrames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelBoneAnimation that = (ModelBoneAnimation) o;
        return positionFrames.equals(that.positionFrames) && rotationFrames.equals(that.rotationFrames) && sizeFrames.equals(that.sizeFrames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionFrames, rotationFrames, sizeFrames);
    }

    @Override
    public String toString() {
        return "ModelBoneAnimation{" +
                "positionFrames=" + positionFrames +
                ", rotationFrames=" + rotationFrames +
                ", sizeFrames=" + sizeFrames +
                '}';
    }
}