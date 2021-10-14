package team.unnamed.hephaestus.model.animation;

/**
 * Represents a bone animation, compound by
 * {@link KeyFrame}. They can be separated by
 * position, rotation and scale
 */
public class ModelBoneAnimation {

    private final KeyFrameList frames;

    public ModelBoneAnimation(KeyFrameList frames) {
        this.frames = frames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelBoneAnimation that = (ModelBoneAnimation) o;
        return frames.equals(that.frames);
    }

    @Override
    public int hashCode() {
        return frames.hashCode();
    }

    @Override
    public String toString() {
        return "ModelBoneAnimation{" + frames + '}';
    }

}