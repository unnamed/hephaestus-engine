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
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.animation.timeline.bone.BoneFrame;
import team.unnamed.hephaestus.animation.timeline.bone.BoneTimelinePlayhead;
import team.unnamed.hephaestus.animation.timeline.effect.EffectsTimeline;
import team.unnamed.hephaestus.animation.timeline.effect.EffectsTimelinePlayhead;
import team.unnamed.hephaestus.util.Quaternion;
import team.unnamed.hephaestus.view.AbstractBoneView;
import team.unnamed.hephaestus.view.AbstractModelView;
import team.unnamed.mocha.MochaEngine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

class AnimationPlayerImpl implements AnimationPlayer {
    private final PriorityLinkedList<AnimationQueue> animations = new PriorityLinkedList<>();
    private final AbstractModelView<?> view;
    private final MochaEngine<AbstractModelView<?>> scriptEngine;

    private final Map<String, BoneFrame> lastFrames = new HashMap<>();

    AnimationPlayerImpl(final @NotNull AbstractModelView<?> view) {
        this.view = requireNonNull(view, "view");
        this.scriptEngine = MochaEngine.createStandard(view);
    }

    @Override
    public synchronized void add(final @NotNull Animation animation, final int transitionTicks) {
        requireNonNull(animation, "animation");

        animations.add(animation.priority(), queue -> {
            if (queue == null) {
                queue = new AnimationQueue();
            }

            if (transitionTicks == 0 || queue.current == null) {
                queue.clear();
                queue.setCurrent(animation);
            } else {
                queue.clear();
                final Animation transitionAnimation = SyntheticAnimations.transitionTo(lastFrames, animation, transitionTicks);
                queue.setCurrent(transitionAnimation);
                queue.queue(animation);
            }

            return queue;
        });
    }

    @Override
    public synchronized void remove(final @NotNull Animation animation) {
        requireNonNull(animation, "animation");
        //animations.remove(animation);
    }

    @Override
    public @NotNull Collection<Animation> animations() {
        //return animations;
        return Collections.emptySet();
    }

    @Override
    public void clear() {
        animations.clear();
    }

    @Override
    public synchronized void tick(final @NotNull Quaternion initialRotation, final @NotNull Vector3Float initialPosition) {
        // tick bones recursively
        for (final Bone bone : view.model().bones()) {
            tickBone(bone, initialRotation, initialPosition, Vector3Float.ONE);
        }

        for (final AnimationQueue queue : animations) {
            final var currentAnimation = queue.current;

            if (currentAnimation == null) {
                continue;
            }

            final var effectsIterator = queue.effectsIterator;
            final var effectsFrame = effectsIterator.next();
            if (effectsIterator.tick() + 1 >= currentAnimation.length()) {
                continue;
            }

            final var sounds = effectsFrame.sounds();
            final var instructions = effectsFrame.instructions();

            for (Sound sound : sounds) {
                view.emitSound(sound);
            }
            for (String instruction : instructions) {
                scriptEngine.eval(instruction);
            }
        }
    }

    private void tickBone(Bone bone, Quaternion parentRotation, Vector3Float parentPosition, Vector3Float parentScale) {
        BoneFrame boneFrame = nextFrame(bone.name());
        Vector3Float frameScale = boneFrame.scale();
        Vector3Float framePosition = boneFrame.position();
        Vector3Float frameRotation = boneFrame.rotation();

        Vector3Float localPosition = bone.position().add(framePosition);
        Vector3Float localRotation = bone.rotation().add(frameRotation);

        Vector3Float globalScale = parentScale.multiply(frameScale);

        Quaternion globalRotation = parentRotation.multiply(Quaternion.fromEulerDegrees(localRotation));
        Vector3Float globalPosition = parentRotation.transform(localPosition.multiply(globalScale)).add(parentPosition);

        AbstractBoneView boneView = view.bone(bone.name());
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
    public @NotNull MochaEngine<AbstractModelView<?>> scriptEngine() {
        return scriptEngine;
    }

    private BoneFrame nextFrame(String boneName) {
        if (animations.isEmpty()) {
            // no animations being played
            return fallback(boneName);
        }

        for (final var queue : animations) {
            final var animation = queue.current;
            if (animation == null) {
                continue;
            }
            final var iterator = queue.iterators.get(boneName);

            if (iterator == null) {
                // try with next animation
                continue;
            }

            final var frame = iterator.next();
            final var tick = iterator.tick();
            lastFrames.put(boneName, frame);

            if (tick + 1 >= animation.length()) {
                // animation ended!
                switch (animation.loopMode()) {
                    case ONCE:
                        queue.moveToNextAnimation();
                        // animation ended, lastFrames are removed
                        // so that next calls will return INITIAL
                        lastFrames.remove(boneName);
                        return frame;
                    case LOOP:
                        queue.setCurrent(animation);
                        return frame;
                    case HOLD:
                        queue.moveToNextAnimation();
                        return frame;
                }
            }
            break;
        }

        return fallback(boneName);
    }

    private BoneFrame fallback(String boneName) {
        return lastFrames.getOrDefault(boneName, BoneFrame.INITIAL);
    }

    private static class AnimationQueue {
        final List<Animation> queue = new LinkedList<>();

        @NotNull EffectsTimelinePlayhead effectsIterator = new EffectsTimelinePlayhead(EffectsTimeline.empty().build());
        final Map<String, BoneTimelinePlayhead> iterators = new HashMap<>();
        Animation current;

        void queue(Animation animation) {
            queue.add(animation);
        }

        void setCurrent(Animation animation) {
            current = animation;
            iterators.clear();
            animation.timelines().forEach((name, list) -> iterators.put(name, list.createPlayhead()));
            effectsIterator = animation.effectsTimeline().createPlayhead();
        }

        void moveToNextAnimation() {
            if (queue.isEmpty()) {
                current = null;
                return;
            }
            setCurrent(queue.remove(0));
        }

        void clear() {
            queue.clear();
            current = null;
        }
    }
}