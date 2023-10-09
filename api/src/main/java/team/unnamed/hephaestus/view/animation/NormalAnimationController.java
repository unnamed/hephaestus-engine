/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
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
package team.unnamed.hephaestus.view.animation;

import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.animation.Frame;
import team.unnamed.hephaestus.animation.timeline.BoneTimeline;
import team.unnamed.hephaestus.util.Quaternion;
import team.unnamed.hephaestus.util.Vectors;
import team.unnamed.hephaestus.view.BaseBoneView;
import team.unnamed.hephaestus.view.BaseModelView;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class NormalAnimationController implements AnimationController {

    private final Deque<Animation> queue = new LinkedList<>();
    private final BaseModelView<?> view;

    private final Map<String, BoneTimeline.StateIterator> iterators = new HashMap<>();

    private final Map<String, Frame> lastFrames = new HashMap<>();

    // Reference to the animation currently being played
    private @Nullable Animation currentAnimation;

    NormalAnimationController(BaseModelView<?> view) {
        this.view = view;
    }

    @Override
    public synchronized void queue(Animation animation, int transitionTicks) {
        if (transitionTicks <= 0) {
            queue.addFirst(animation);
            nextAnimation();
            return;
        }

        // create a synthetic animation that will transition between
        // the last frame of the current animation and the first frame
        // of the next animation
        Map<String, BoneTimeline> boneTimelines = new HashMap<>();
        Animation transitionAnimation = Animation.animation("$transition", transitionTicks, Animation.LoopMode.HOLD, boneTimelines);

        lastFrames.forEach((boneName, frame) -> {
            BoneTimeline timeline = BoneTimeline.create();
            timeline.positions().put(0, frame.position());
            timeline.rotations().put(0, frame.rotation());
            timeline.scales().put(0, frame.scale());
            boneTimelines.put(boneName, timeline);
        });

        animation.timelines().forEach((boneName, nextTimeline) -> {
            BoneTimeline timeline = boneTimelines.get(boneName);
            if (timeline == null) {
                // this new animation animates an unknown bone?
                return;
            }

            timeline.positions().put(transitionTicks, nextTimeline.positions().tickiterator().next());
            timeline.rotations().put(transitionTicks, nextTimeline.rotations().tickiterator().next());
            timeline.scales().put(transitionTicks, nextTimeline.scales().tickiterator().next());
        });

        // queue transition and animation itself
        queue.addFirst(transitionAnimation);
        queue.addFirst(animation);
        nextAnimation();
    }

    private void tickBone(
            double yaw,
            Bone bone,
            Quaternion parentRotation,
            Vector3Float parentPosition
    ) {
        BaseBoneView boneView = view.bone(bone.name());
        assert boneView != null;

        Frame frame = nextFrame(bone.name());
        Vector3Float framePosition = frame.position();
        Vector3Float frameRotation = frame.rotation();

        Vector3Float defaultPosition = bone.position();
        Vector3Float defaultRotation = bone.rotation();

        Vector3Float localPosition = defaultPosition.add(framePosition);
        Vector3Float localRotation = defaultRotation.add(frameRotation);

        Quaternion globalRotation = parentRotation.multiply(Quaternion.fromEulerDegrees(localRotation));
        Vector3Float globalPosition = Vectors.rotateDegrees(
                localPosition,
                parentRotation.toEulerDegrees()
        ).add(parentPosition);

        boneView.update(globalPosition, globalRotation);

        for (Bone child : bone.children()) {
            tickBone(
                    yaw,
                    child,
                    globalRotation,
                    globalPosition
            );
        }
    }

    @Override
    public void clearQueue() {
        queue.clear();
        currentAnimation = null;
    }

    @Override
    public synchronized void tick(double yaw) {
        Quaternion bodyRotation = /*Quaternion.fromEulerDegrees(new Vector3Float(0,  (float) (360 - yaw), 0))*/ Quaternion.IDENTITY;
        for (Bone bone : view.model().bones()) {
            tickBone(
                    yaw,
                    bone,
                    bodyRotation,
                    Vector3Float.ZERO
            );
        }
    }

    private void nextAnimation() {
        if ((currentAnimation = queue.pollLast()) != null) {
            createIterators(currentAnimation);
        }
    }

    private void createIterators(Animation animation) {
        iterators.clear();
        animation.timelines().forEach((name, list) -> iterators.put(name, list.iterator()));
    }

    private Frame nextFrame(String boneName) {

        if (currentAnimation == null) {
            // if no animation currently being played,
            // try poll one from the animation queue
            nextAnimation();
            if (currentAnimation == null) {
                // if queue was empty, the last frame or
                // the initial keyframe is returned
                return fallback(boneName);
            }
        }

        BoneTimeline.StateIterator iterator = iterators.get(boneName);

        if (iterator != null) {
            Frame frame = iterator.next();
            lastFrames.put(boneName, frame);

            if (iterator.tick() >= currentAnimation.length()) {
                // animation ended!
                switch (currentAnimation.loopMode()) {
                    case ONCE:
                        nextAnimation();
                        // animation ended, lastFrames are removed
                        // so that next calls will return INITIAL
                        lastFrames.remove(boneName);
                        return frame;
                    case LOOP:
                        createIterators(currentAnimation);
                        return frame;
                    case HOLD:
                        nextAnimation();
                        return frame;
                }
            }
        }

        return fallback(boneName);
    }

    private Frame fallback(String boneName) {
        return lastFrames.getOrDefault(boneName, Frame.INITIAL);
    }

}