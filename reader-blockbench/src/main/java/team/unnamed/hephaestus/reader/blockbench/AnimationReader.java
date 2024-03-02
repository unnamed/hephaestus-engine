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
package team.unnamed.hephaestus.reader.blockbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.animation.interpolation.Interpolators;
import team.unnamed.hephaestus.animation.interpolation.KeyFrameInterpolator;
import team.unnamed.hephaestus.animation.timeline.KeyFrame;
import team.unnamed.hephaestus.animation.timeline.KeyFrameBezierAttachment;
import team.unnamed.hephaestus.animation.timeline.bone.BoneTimeline;
import team.unnamed.hephaestus.animation.timeline.Timeline;
import team.unnamed.hephaestus.animation.timeline.effect.EffectsTimeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class AnimationReader {
    
    private static final int BEZIER_CURVE_DIVISIONS = Integer.getInteger("hephaestus.bezier_divisions", 200);
    private static final KeyFrameInterpolator<Vector3Float> BEZIER_INTERPOLATOR = Interpolators.bezierVector3Float(BEZIER_CURVE_DIVISIONS);
    private static final int TICKS_PER_SECOND = Integer.getInteger("hephaestus.tps", 20);

    /**
     * Reads {@link Animation} from the given {@code json}
     * object and puts them into the specified {@code animations}
     * map
     */
    public static Map<String, Animation> readAnimations(JsonObject json) {

        Map<String, Animation> animations = new LinkedHashMap<>();

        if (!json.has("animations")) {
            // Model doesn't have animations
            return animations;
        }

        for (JsonElement animationElement : json.get("animations").getAsJsonArray()) {

            JsonObject animationJson = animationElement.getAsJsonObject();

            String name = animationJson.get("name").getAsString();
            Animation.LoopMode loopMode = getLoopMode(animationJson);
            final int priority = getPriority(animationJson);
            int length = Math.round(GsonUtil.parseLenientFloat(animationJson.get("length")) * TICKS_PER_SECOND);

            if (GsonUtil.isNullOrAbsent(animationJson, "animators")) {
                // empty animation, no keyframes of any kind
                animations.put(name, Animation.animation()
                        .name(name)
                        .length(length)
                        .loopMode(loopMode)
                        .priority(priority)
                        .timelines(Collections.emptyMap())
                        .effectsTimeline(EffectsTimeline.empty().build())
                        .build());
                continue;
            }

            Map<String, BoneTimeline> animators = new HashMap<>();
            EffectsTimeline.Builder effectsTimeline = EffectsTimeline.empty();

            for (Map.Entry<String, JsonElement> animatorEntry : animationJson.get("animators")
                    .getAsJsonObject()
                    .entrySet()) {

                JsonObject animatorJson = animatorEntry.getValue().getAsJsonObject();
                String boneName = animatorJson.get("name").getAsString();
                String type = animatorJson.get("type").getAsString();

                if (type.equals("effect")) {
                    Map<Integer, List<Sound>> soundsTimeline = new HashMap<>();
                    Map<Integer, List<String>> instructionsTimeline = new HashMap<>();

                    for (JsonElement keyFrameElement : animatorJson.get("keyframes").getAsJsonArray()) {
                        JsonObject keyframeJson = keyFrameElement.getAsJsonObject();
                        JsonArray dataPoints = keyframeJson.get("data_points").getAsJsonArray();
                        String channel = keyframeJson.get("channel").getAsString();
                        int time = Math.round(GsonUtil.parseLenientFloat(keyframeJson.get("time")) * TICKS_PER_SECOND);

                        switch (channel) {
                            case "sound":
                                List<Sound> sounds = new ArrayList<>();

                                for (JsonElement dataPointElement : dataPoints) {
                                    JsonObject dataPoint = dataPointElement.getAsJsonObject();
                                    String soundName = dataPoint.get("effect").getAsString();

                                    sounds.add(Sound.sound(
                                            Key.key("hephaestus", soundName),
                                            Sound.Source.AMBIENT,
                                            1,
                                            1
                                    ));
                                }

                                soundsTimeline.put(time, sounds);
                                break;
                            case "timeline":
                                List<String> instructions = new ArrayList<>();
                                for (final var dataPointNode : dataPoints) {
                                    final var dataPoint = dataPointNode.getAsJsonObject();
                                    instructions.add(dataPoint.get("script").getAsString());
                                }
                                instructionsTimeline.put(time, instructions);
                                break;
                        }
                    }

                    effectsTimeline.sounds(soundsTimeline);
                    effectsTimeline.instructions(instructionsTimeline);
                } else if (type.equals("bone")) {
                    Timeline.Builder<Vector3Float> positionsTimeline = Timeline.<Vector3Float>timeline()
                            .initial(Vector3Float.ZERO)
                            .defaultInterpolator(Interpolators.lerpVector3Float());
                    Timeline.Builder<Vector3Float> rotationsTimeline = Timeline.<Vector3Float>timeline()
                            .initial(Vector3Float.ZERO)
                            .defaultInterpolator(Interpolators.lerpVector3Float());
                    Timeline.Builder<Vector3Float> scalesTimeline = Timeline.<Vector3Float>timeline()
                            .initial(Vector3Float.ONE)
                            .defaultInterpolator(Interpolators.lerpVector3Float());

                    for (JsonElement keyFrameElement : animatorJson.get("keyframes").getAsJsonArray()) {
                        JsonObject keyframeJson = keyFrameElement.getAsJsonObject();
                        JsonArray dataPoints = keyframeJson.get("data_points").getAsJsonArray();

                        String channel = keyframeJson.get("channel").getAsString();
                        int time = Math.round(GsonUtil.parseLenientFloat(keyframeJson.get("time")) * TICKS_PER_SECOND);
                        JsonObject dataPoint = dataPoints.get(0).getAsJsonObject();

                        float x = GsonUtil.parseLenientFloat(dataPoint.get("x"));
                        float y = GsonUtil.parseLenientFloat(dataPoint.get("y"));
                        float z = GsonUtil.parseLenientFloat(dataPoint.get("z"));

                        Vector3Float value = new Vector3Float(x, y, z);

                        if (channel.equals("position")) {
                            value = value.divide(-Blockbench.BLOCK_SIZE, Blockbench.BLOCK_SIZE, -Blockbench.BLOCK_SIZE);
                        } else if (channel.equals("rotation")) {
                            value = value.multiply(1, -1, -1);
                        }

                        String interpolation = keyframeJson.has("interpolation")
                                ? keyframeJson.get("interpolation").getAsString()
                                : "linear";
                        KeyFrameInterpolator<Vector3Float> interpolator;
                        switch (interpolation.toLowerCase()) {
                            case "bezier":
                                interpolator = BEZIER_INTERPOLATOR;
                                break;
                            case "linear":
                                interpolator = Interpolators.lerpVector3Float();
                                break;
                            case "catmullrom":
                            case "smooth": // <-- smooth is the displayed name of catmullrom, it is the same
                                interpolator = Interpolators.catmullRomSplineVector3Float();
                                break;
                            case "step":
                                interpolator = Interpolators.stepVector3Float();
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported interpolation type: '" + interpolation + "'");
                        }

                        final KeyFrame<Vector3Float> keyFrame = new KeyFrame<>(time, value, interpolator);

                        // load bézier attachments
                        if (keyframeJson.has("bezier_left_time")) {
                            final Vector3Float leftTime = GsonUtil.getVector3FloatFromJson(keyframeJson.get("bezier_left_time"));
                            final Vector3Float leftValue = GsonUtil.getVector3FloatFromJson(keyframeJson.get("bezier_left_value"));
                            final Vector3Float rightTime = GsonUtil.getVector3FloatFromJson(keyframeJson.get("bezier_right_time"));
                            final Vector3Float rightValue = GsonUtil.getVector3FloatFromJson(keyframeJson.get("bezier_right_value"));
                            keyFrame.attachment(KeyFrameBezierAttachment.class, KeyFrameBezierAttachment.of(leftTime, leftValue, rightTime, rightValue));
                        }

                        switch (channel.toLowerCase(Locale.ROOT)) {
                            case "position":
                                positionsTimeline.keyFrame(keyFrame);
                                break;
                            case "rotation":
                                rotationsTimeline.keyFrame(keyFrame);
                                break;
                            case "scale":
                                scalesTimeline.keyFrame(keyFrame);
                                break;
                        }
                    }

                    animators.put(boneName, BoneTimeline.boneTimeline()
                            .positions(positionsTimeline.build())
                            .rotations(rotationsTimeline.build())
                            .scales(scalesTimeline.build())
                            .build()
                    );
                }
            }

            animations.put(name, Animation.animation()
                    .name(name)
                    .length(length)
                    .loopMode(loopMode)
                    .priority(priority)
                    .timelines(animators)
                    .effectsTimeline(effectsTimeline.build())
                    .build());
        }
        return animations;
    }

    private static int getPriority(final JsonObject animationJson) {
        if (!animationJson.has("blend_weight")) {
            return 0;
        }
        final var blendWeightNode = animationJson.get("blend_weight");
        if (!blendWeightNode.isJsonPrimitive() || blendWeightNode.getAsString().isEmpty()) {
            // todo: warn?
            return 0;
        }
        // todo: it seems like Blockbench blend_weight is a Molang script
        return blendWeightNode.getAsInt();
    }

    private static Animation.LoopMode getLoopMode(JsonObject animationJson) {
        if (!animationJson.has("loop")) {
            // PLAY_ONCE by default
            return Animation.LoopMode.ONCE;
        }

        return Animation.LoopMode.valueOf(
                animationJson.get("loop")
                        .getAsString()
                        .toUpperCase(Locale.ROOT)
        );
    }

}
