/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.hephaestus.animation;

import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.ModelBone;
import team.unnamed.hephaestus.view.ModelView;
import team.unnamed.hephaestus.struct.Quaternion;
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
            Vector3Float parentRotation,
            Vector3Float parentPosition
    ) {

        KeyFrame frame = next(bone.getName());
        Vector3Float framePosition = frame.getPosition();

        Vector3Float frameRotation = Vectors.toRadians(frame.getRotation());

        Vector3Float defaultPosition = bone.getOffset();
        Vector3Float defaultRotation = Vectors.toRadians(bone.getRotation());

        Vector3Float localPosition = framePosition.add(defaultPosition);
        Vector3Float localRotation = defaultRotation.add(frameRotation);

        Vector3Float globalPosition;
        Vector3Float globalRotation;

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
                    Vector3Float.ZERO,
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
