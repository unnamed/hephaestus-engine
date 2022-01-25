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
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.view.ModelView;
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
    private final ModelView<?> view;
    private ModelAnimation animation;

    public AnimationQueue(ModelView<?> view) {
        this.view = view;
    }

    private void createIterators(ModelAnimation animation) {
        iterators.clear();
        animation.framesByBone().forEach((name, list) ->
                iterators.put(name, list.iterator()));
    }

    public synchronized void pushAnimation(ModelAnimation animation, int transitionTicks) {
        if (transitionTicks <= 0) {
            animations.addFirst(animation);
            nextAnimation();
            return;
        }

        Map<String, KeyFrameList> framesByBone = new HashMap<>();

        lastFrames.forEach((boneName, frame) -> {
            KeyFrameList keyFrames = framesByBone.computeIfAbsent(boneName, k -> new DynamicKeyFrameList());
            keyFrames.put(0, KeyFrameList.Channel.POSITION, frame.position());
            keyFrames.put(0, KeyFrameList.Channel.ROTATION, frame.rotation());
            keyFrames.put(0, KeyFrameList.Channel.SCALE, frame.scale());

            framesByBone.put(boneName, keyFrames);
        });

        animation.framesByBone().forEach((boneName, frames) -> {
            Iterator<KeyFrame> iterator = frames.iterator();
            if (iterator.hasNext()) {
                KeyFrame firstFrame = frames.iterator().next();

                KeyFrameList keyFrames = framesByBone.computeIfAbsent(boneName, k -> new DynamicKeyFrameList());
                keyFrames.put(transitionTicks, KeyFrameList.Channel.POSITION, firstFrame.position());
                keyFrames.put(transitionTicks, KeyFrameList.Channel.ROTATION, firstFrame.rotation());
                keyFrames.put(transitionTicks, KeyFrameList.Channel.SCALE, firstFrame.scale());
            }
        });

        animations.addFirst(new ModelAnimation(
                "generated-transition",
                false,
                transitionTicks,
                framesByBone
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
            Bone parent,
            Bone bone,
            Vector3Float parentRotation,
            Vector3Float parentPosition
    ) {

        KeyFrame frame = next(bone.name());
        Vector3Float framePosition = frame.position();

        Vector3Float frameRotation = Vectors.toRadians(frame.rotation());

        Vector3Float defaultPosition = bone.offset();
        Vector3Float defaultRotation = Vectors.toRadians(bone.rotation());

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
            globalRotation = Vectors.combineRotations(localRotation, parentRotation);
        }

        view.moveBone(bone.name(), globalPosition);
        view.rotateBone(bone.name(), globalRotation);

        //if (modelData != -1) {
        //    entity.updateBoneModelData(bone, modelData);
        //}

        for (Bone component : bone.children()) {
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
        for (Bone bone : view.model().bones()) {
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
                    if (animation.loop()) {
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
