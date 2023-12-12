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
package team.unnamed.hephaestus.animation.controller;

import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.animation.timeline.bone.BoneFrame;
import team.unnamed.hephaestus.animation.timeline.bone.BoneTimeline;
import team.unnamed.hephaestus.animation.timeline.bone.BoneTimelinePlayhead;
import team.unnamed.hephaestus.animation.timeline.Timeline;
import team.unnamed.hephaestus.animation.timeline.effect.EffectsFrame;
import team.unnamed.hephaestus.animation.timeline.effect.EffectsTimeline;
import team.unnamed.hephaestus.animation.timeline.effect.EffectsTimelinePlayhead;
import team.unnamed.hephaestus.util.Quaternion;
import team.unnamed.hephaestus.util.Vectors;
import team.unnamed.hephaestus.view.BaseBoneView;
import team.unnamed.hephaestus.view.BaseModelView;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static java.util.Objects.requireNonNull;

class AnimationControllerImpl implements AnimationController {
    private final Deque<Animation> queue = new LinkedList<>();
    private final BaseModelView<?> view;

    private final Map<String, BoneTimelinePlayhead> iterators = new HashMap<>();
    private @NotNull EffectsTimelinePlayhead effectsIterator = new EffectsTimelinePlayhead(EffectsTimeline.empty().build());

    private final Map<String, BoneFrame> lastFrames = new HashMap<>();

    private @Nullable Animation currentAnimation;

    AnimationControllerImpl(final @NotNull BaseModelView<?> view) {
        this.view = requireNonNull(view, "view");
    }

    @Override
    public synchronized void queue(final @NotNull Animation animation, final int transitionTicks) {
        requireNonNull(animation, "animation");

        if (transitionTicks <= 0) {
            // no transition ticks, just queue the animation
            queue.addFirst(animation);
            nextAnimation();
            return;
        }

        // create a synthetic animation that will transition between
        // the last frame of the current animation and the first frame
        // of the next animation
        final Animation.Builder transitionAnimationBuilder = Animation.animation()
                .name("$$hephaestus_transition_animation")
                .length(transitionTicks)
                .loopMode(Animation.LoopMode.HOLD)
                .effectsTimeline(EffectsTimeline.empty().build());

        for (Map.Entry<String, BoneFrame> entry : lastFrames.entrySet()) {
            String boneName = entry.getKey();
            BoneFrame lastFrame = entry.getValue();

            // put last states (initial from next animation)
            BoneTimeline nextTimeline = animation.timelines().get(boneName);
            if (nextTimeline == null) {
                // this new animation animates an unknown bone?
                continue;
            }

            BoneTimeline.Builder timeline = BoneTimeline.boneTimeline();

            timeline.positions(
                    Timeline.<Vector3Float>timeline()
                            .keyFrame(0, lastFrame.position())
                            .keyFrame(transitionTicks, nextTimeline.positions().createPlayhead().next())
                            .build()
            );
            timeline.rotations(
                    Timeline.<Vector3Float>timeline()
                            .keyFrame(0, lastFrame.rotation())
                            .keyFrame(transitionTicks, nextTimeline.rotations().createPlayhead().next())
                            .build()
            );
            timeline.scales(
                    Timeline.<Vector3Float>timeline()
                            .keyFrame(0, lastFrame.scale())
                            .keyFrame(transitionTicks, nextTimeline.scales().createPlayhead().next())
                            .build()
            );

            transitionAnimationBuilder.timeline(boneName, timeline.build());
        }

        // queue transition and animation itself
        queue.addFirst(transitionAnimationBuilder.build());
        queue.addFirst(animation);
        nextAnimation();
    }

    private void tickBone(
            Bone bone,
            Quaternion parentRotation,
            Vector3Float parentPosition,
            Vector3Float parentScale
    ) {
        BoneFrame boneFrame = nextFrame(bone.name());
        Vector3Float frameScale = boneFrame.scale();
        Vector3Float framePosition = boneFrame.position();
        Vector3Float frameRotation = boneFrame.rotation();

        Vector3Float defaultPosition = bone.position();
        Vector3Float defaultRotation = bone.rotation();

        Vector3Float localPosition = defaultPosition.add(framePosition);
        Vector3Float localRotation = defaultRotation.subtract(frameRotation);

        Vector3Float globalScale = parentScale.multiply(frameScale);

        Quaternion globalRotation = parentRotation.multiply(Quaternion.fromEulerDegrees(localRotation));
        Vector3Float globalPosition = Vectors.rotateDegrees(
                localPosition.multiply(globalScale),
                parentRotation.toEulerDegrees().multiply(-1, 1, 1)
        ).add(parentPosition);

        BaseBoneView boneView = view.bone(bone.name());
        if (boneView != null) {
            boneView.update(globalPosition, globalRotation, globalScale);
        }

        for (Bone child : bone.children()) {
            tickBone(
                    child,
                    globalRotation,
                    globalPosition,
                    globalScale
            );
        }
    }

    @Override
    public void clearQueue() {
        queue.clear();
        currentAnimation = null;
    }

    @Override
    public synchronized void tick(@NotNull Quaternion initialRotation, @NotNull Vector3Float initialPosition) {
        for (Bone bone : view.model().bones()) {
            tickBone(
                    bone,
                    initialRotation,
                    initialPosition,
                    Vector3Float.ONE
            );
        }

        if (currentAnimation == null) {
            return;
        }

        EffectsFrame effectsFrame = effectsIterator.next();
        if (effectsIterator.tick() + 1 >= currentAnimation.length()) {
            return;
        }

        Sound[] sounds = effectsFrame.sounds();

        for (Sound sound : sounds) {
            System.out.println("play ");
            view.playSound(sound);
        }
    }

    private void nextAnimation() {
        if ((currentAnimation = queue.pollLast()) != null) {
            createIterators(currentAnimation);
        }
    }

    private void createIterators(Animation animation) {
        iterators.clear();
        lastFrames.clear();

        animation.timelines().forEach((name, list) -> iterators.put(name, list.createPlayhead()));
        effectsIterator = animation.effectsTimeline().createPlayhead();
    }

    private BoneFrame nextFrame(String boneName) {

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

        BoneTimelinePlayhead iterator = iterators.get(boneName);

        if (iterator != null) {
            BoneFrame boneFrame = iterator.next();
            int tick = iterator.tick();
            lastFrames.put(boneName, boneFrame);

            if (tick + 1 >= currentAnimation.length()) {
                // animation ended!
                switch (currentAnimation.loopMode()) {
                    case ONCE:
                        nextAnimation();
                        // animation ended, lastFrames are removed
                        // so that next calls will return INITIAL
                        lastFrames.remove(boneName);
                        return boneFrame;
                    case LOOP:
                        createIterators(currentAnimation);
                        return boneFrame;
                    case HOLD:
                        nextAnimation();
                        return boneFrame;
                }

            }
        }

        return fallback(boneName);
    }

    private BoneFrame fallback(String boneName) {
        return lastFrames.getOrDefault(boneName, BoneFrame.INITIAL);
    }
}