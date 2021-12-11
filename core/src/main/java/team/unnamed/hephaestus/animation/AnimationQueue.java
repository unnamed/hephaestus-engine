package team.unnamed.hephaestus.animation;

import team.unnamed.hephaestus.ModelBone;
import team.unnamed.hephaestus.view.ModelView;
import team.unnamed.hephaestus.struct.Quaternion;
import team.unnamed.hephaestus.struct.Vector3Double;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class AnimationQueue {

    private final Map<String, KeyFrame> lastFrames = new HashMap<>();
    private final Map<String, Iterator<KeyFrame>> iterators = new HashMap<>();
    private int noNext;

    private final Deque<ModelAnimation> animations = new LinkedList<>();
    private final ModelView view;
    private ModelAnimation animation;

    public AnimationQueue(ModelView view) {
        this.view = view;
    }

    private void createIterators(ModelAnimation animation) {
        iterators.clear();
        animation.getAnimationsByBoneName().forEach((name, list) ->
                iterators.put(name, list.iterator()));
    }

    public synchronized void pushAnimation(ModelAnimation animation, int transitionTicks) {
        if (transitionTicks <= 0) {
            animations.addFirst(animation);
            nextAnimation();
            return;
        }

        Map<String, KeyFrameList> framesByBone = new HashMap<>();
        Map<String, Map<Integer, Integer>> modelData = new HashMap<>();

        lastFrames.forEach((boneName, frame) -> {
            KeyFrameList keyFrames = framesByBone.computeIfAbsent(boneName, k -> new DynamicKeyFrameList());
            keyFrames.put(0, KeyFrameList.Channel.POSITION, frame.getPosition());
            keyFrames.put(0, KeyFrameList.Channel.ROTATION, frame.getRotation());
            keyFrames.put(0, KeyFrameList.Channel.SCALE, frame.getScale());

            framesByBone.put(boneName, keyFrames);
        });

        animation.getAnimationsByBoneName().forEach((boneName, frames) -> {
            Iterator<KeyFrame> iterator = frames.iterator();
            if (iterator.hasNext()) {
                KeyFrame firstFrame = frames.iterator().next();

                KeyFrameList keyFrames = framesByBone.computeIfAbsent(boneName, k -> new DynamicKeyFrameList());
                keyFrames.put(transitionTicks, KeyFrameList.Channel.POSITION, firstFrame.getPosition());
                keyFrames.put(transitionTicks, KeyFrameList.Channel.ROTATION, firstFrame.getRotation());
                keyFrames.put(transitionTicks, KeyFrameList.Channel.SCALE, firstFrame.getScale());
            }
        });

        animations.addFirst(new ModelAnimation(
                "generated-transition",
                false,
                transitionTicks,
                framesByBone,
                modelData
        ));

        animations.addFirst(animation);
        nextAnimation();
    }

    public synchronized void removeAllAnimations() {
        animations.clear();
        animation = null;
    }


    private void nextAnimation() {
        animation = animations.pollLast();
        if (animation != null) {
            createIterators(animation);
        }
    }

    private void updateBone(
            double yaw,
            ModelBone parent,
            ModelBone bone,
            Vector3Double parentRotation,
            Vector3Float parentPosition
    ) {

        KeyFrame frame = next(bone.getName());
        Vector3Float framePosition = frame.getPosition();

        Vector3Double frameRotation = Vectors.toRadians(frame.getRotation());

        Vector3Float defaultPosition = bone.getOffset();
        Vector3Double defaultRotation = Vectors.toRadians(bone.getRotation());

        Vector3Float localPosition = framePosition.add(defaultPosition);
        Vector3Double localRotation = defaultRotation.add(frameRotation.getX(), frameRotation.getY(), frameRotation.getZ());

        Vector3Float globalPosition;
        Vector3Double globalRotation;

        if (parent == null) {
            globalPosition = Vectors.rotateAroundY(localPosition, yaw);
            globalRotation = localRotation;
        } else {
            globalPosition = Vectors.rotateAroundY(
                    Vectors.rotate(localPosition, parentRotation),
                    yaw
            ).add(parentPosition);
            globalRotation = Quaternion.fromEuler(localRotation)
                    .multiply(Quaternion.fromEuler(parentRotation))
                    .toEuler();
        }

        view.moveBone(bone.getName(), globalPosition);
        view.rotateBone(bone.getName(), globalRotation);

        //if (modelData != -1) {
        //    entity.updateBoneModelData(bone, modelData);
        //}

        for (ModelBone component : bone.getBones()) {
            this.updateBone(
                    yaw,
                    bone,
                    component,
                    globalRotation,
                    globalPosition
            );
        }
    }

    public synchronized void next(double yaw) {
        for (ModelBone bone : view.getModel().getBones()) {
            updateBone(
                    yaw,
                    null,
                    bone,
                    Vector3Double.ZERO,
                    Vector3Float.ZERO
            );
        }
    }

    private KeyFrame next(String boneName) {
        if (animation == null) {
            nextAnimation();
            if (animation == null) {
                return lastFrames.getOrDefault(boneName, KeyFrame.INITIAL);
            }
        }
        Iterator<KeyFrame> iterator = iterators.get(boneName);
        if (iterator == null) {
            return KeyFrame.INITIAL;
        } else if (iterator.hasNext()) {
            KeyFrame frame = iterator.next();
            if (!iterator.hasNext()) {
                if (++noNext >= iterators.size()) {
                    noNext = 0;
                    // all iterators fully-consumed
                    if (animation.isLoop()) {
                        createIterators(animation);
                    } else {
                        nextAnimation();
                    }
                }
            }
            lastFrames.put(boneName, frame);
            return frame;
        } else {
            return lastFrames.getOrDefault(boneName, KeyFrame.INITIAL);
        }
    }

}
