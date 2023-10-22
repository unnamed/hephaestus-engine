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
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.animation.interpolation.Interpolator;
import team.unnamed.hephaestus.animation.timeline.bone.BoneTimeline;
import team.unnamed.hephaestus.animation.timeline.Timeline;
import team.unnamed.hephaestus.animation.timeline.effect.EffectsTimeline;
import team.unnamed.hephaestus.process.ElementScale;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

final class AnimationReader {

    private static final int TICKS_PER_SECOND = Integer.getInteger("hephaestus.tps", 20);

    /**
     * Reads {@link Animation} from the given {@code json}
     * object and puts them into the specified {@code animations}
     * map
     */
    static void readAnimations(
            JsonObject json,
            Map<String, Animation> animations
    ) throws IOException {

        if (!json.has("animations")) {
            // Model doesn't have animations
            return;
        }

        for (JsonElement animationElement : json.get("animations").getAsJsonArray()) {

            JsonObject animationJson = animationElement.getAsJsonObject();

            String name = animationJson.get("name").getAsString();
            Animation.LoopMode loopMode = getLoopMode(animationJson);
            int length = Math.round(GsonUtil.parseLenientFloat(animationJson.get("length")) * TICKS_PER_SECOND);

            if (GsonUtil.isNullOrAbsent(animationJson, "animators")) {
                // empty animation, no keyframes of any kind
                animations.put(name, Animation.animation(
                        name,
                        length,
                        loopMode,
                        Collections.emptyMap(),
                        EffectsTimeline.empty().build()
                ));
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
                    Map<Integer, Sound[]> soundsTimeline = new HashMap<>();

                    for (JsonElement keyFrameElement : animatorJson.get("keyframes").getAsJsonArray()) {
                        JsonObject keyframeJson = keyFrameElement.getAsJsonObject();
                        JsonArray dataPoints = keyframeJson.get("data_points").getAsJsonArray();
                        String channel = keyframeJson.get("channel").getAsString();
                        int time = Math.round(GsonUtil.parseLenientFloat(keyframeJson.get("time")) * TICKS_PER_SECOND);

                        switch (channel) {
                            case "sound":
                                Sound[] sounds = new Sound[dataPoints.size()];

                                for (int i = 0; i < sounds.length; i++) {
                                    JsonObject dataPoint = dataPoints.get(i).getAsJsonObject();
                                    String soundName = dataPoint.get("effect").getAsString();

                                    sounds[i] = Sound.sound(
                                            Key.key("hephaestus", soundName),
                                            Sound.Source.AMBIENT,
                                            1,
                                            1
                                    );
                                }

                                soundsTimeline.put(time, sounds);
                                break;
                        }
                    }

                    effectsTimeline.sounds(soundsTimeline);
                } else if (type.equals("bone")) {
                    Timeline.Builder<Vector3Float> positionsTimeline = Timeline.<Vector3Float>timeline()
                            .initial(Vector3Float.ZERO)
                            .defaultInterpolator(Interpolator.lerpVector3Float());
                    Timeline.Builder<Vector3Float> rotationsTimeline = Timeline.<Vector3Float>timeline()
                            .initial(Vector3Float.ZERO)
                            .defaultInterpolator(Interpolator.lerpVector3Float());
                    Timeline.Builder<Vector3Float> scalesTimeline = Timeline.<Vector3Float>timeline()
                            .initial(Vector3Float.ONE)
                            .defaultInterpolator(Interpolator.lerpVector3Float());

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
                            value = value.divide(Blockbench.BLOCK_SIZE, Blockbench.BLOCK_SIZE, -Blockbench.BLOCK_SIZE);
                        }

                        String interpolation = keyframeJson.has("interpolation")
                                ? keyframeJson.get("interpolation").getAsString()
                                : "linear";
                        Interpolator<Vector3Float> interpolator;
                        switch (interpolation.toLowerCase()) {
                            case "bezier": // <-- parse bezier as linear
                            case "linear":
                                interpolator = Interpolator.lerpVector3Float();
                                break;
                            case "catmullrom":
                            case "smooth": // <-- smooth is the displayed name of catmullrom, it is the same
                                interpolator = Interpolator.catmullRomSplineVector3Float();
                                break;
                            case "step":
                                interpolator = Interpolator.stepVector3Float();
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported interpolation type: '" + interpolation + "'");
                        }

                        switch (channel.toLowerCase(Locale.ROOT)) {
                            case "position":
                                positionsTimeline.keyFrame(time, value, interpolator);
                                break;
                            case "rotation":
                                rotationsTimeline.keyFrame(time, value, interpolator);
                                break;
                            case "scale":
                                scalesTimeline.keyFrame(time, value, interpolator);
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

            animations.put(name, Animation.animation(
                    name,
                    length,
                    loopMode,
                    animators,
                    effectsTimeline.build()
            ));
        }
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
