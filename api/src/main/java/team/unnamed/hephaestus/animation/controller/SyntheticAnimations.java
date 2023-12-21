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

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.animation.interpolation.Interpolators;
import team.unnamed.hephaestus.animation.timeline.Timeline;
import team.unnamed.hephaestus.animation.timeline.bone.BoneFrame;
import team.unnamed.hephaestus.animation.timeline.bone.BoneTimeline;
import team.unnamed.hephaestus.animation.timeline.effect.EffectsTimeline;

import java.util.Map;

final class SyntheticAnimations {
    private SyntheticAnimations() {
    }

    public static @NotNull Animation transitionTo(
            final @NotNull Map<String, BoneFrame> lastFrames,
            final @NotNull Animation to,
            final int duration
    ) {
        final Animation.Builder transitionAnimationBuilder = Animation.animation()
                .name("$$hephaestus_transition_animation")
                .length(duration)
                .loopMode(Animation.LoopMode.HOLD)
                .effectsTimeline(EffectsTimeline.empty().build());

        for (Map.Entry<String, BoneFrame> entry : lastFrames.entrySet()) {
            String boneName = entry.getKey();
            BoneFrame lastFrame = entry.getValue();

            // put last states (initial from next animation)
            BoneTimeline nextTimeline = to.timelines().get(boneName);
            BoneTimeline.Builder timeline = BoneTimeline.boneTimeline();

            if (nextTimeline == null) {
                // animate to its default state
                timeline.positions(Timeline.<Vector3Float>timeline()
                        .initial(Vector3Float.ZERO)
                        .defaultInterpolator(Interpolators.lerpVector3Float())
                        .keyFrame(0, lastFrame.position())
                        .keyFrame(duration, BoneFrame.INITIAL.position())
                        .build());
                timeline.rotations(Timeline.<Vector3Float>timeline()
                        .initial(Vector3Float.ZERO)
                        .defaultInterpolator(Interpolators.lerpVector3Float())
                        .keyFrame(0, lastFrame.rotation())
                        .keyFrame(duration, BoneFrame.INITIAL.rotation())
                        .build());
                timeline.scales(Timeline.<Vector3Float>timeline()
                        .initial(Vector3Float.ONE)
                        .defaultInterpolator(Interpolators.lerpVector3Float())
                        .keyFrame(0, lastFrame.scale())
                        .keyFrame(duration, BoneFrame.INITIAL.scale())
                        .build());
            } else {
                timeline.positions(
                        Timeline.<Vector3Float>timeline()
                                .initial(Vector3Float.ZERO)
                                .defaultInterpolator(Interpolators.lerpVector3Float())
                                .keyFrame(0, lastFrame.position())
                                .keyFrame(duration, nextTimeline.positions().createPlayhead().next())
                                .build()
                );
                timeline.rotations(
                        Timeline.<Vector3Float>timeline()
                                .initial(Vector3Float.ZERO)
                                .defaultInterpolator(Interpolators.lerpVector3Float())
                                .keyFrame(0, lastFrame.rotation())
                                .keyFrame(duration, nextTimeline.rotations().createPlayhead().next())
                                .build()
                );
                timeline.scales(
                        Timeline.<Vector3Float>timeline()
                                .initial(Vector3Float.ONE)
                                .defaultInterpolator(Interpolators.lerpVector3Float())
                                .keyFrame(0, lastFrame.scale())
                                .keyFrame(duration, nextTimeline.scales().createPlayhead().next())
                                .build()
                );
            }

            transitionAnimationBuilder.timeline(boneName, timeline.build());
        }

        return transitionAnimationBuilder.build();
    }
}
