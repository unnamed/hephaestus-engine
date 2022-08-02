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
package team.unnamed.hephaestus.reader.blockbench;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.animation.Timeline;
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
                animations.put(name, new Animation(name, loopMode, Collections.emptyMap()));
                continue;
            }

            Map<String, Timeline> animators = new HashMap<>();

            for (Map.Entry<String, JsonElement> animatorEntry : animationJson.get("animators")
                    .getAsJsonObject()
                    .entrySet()) {

                JsonObject animatorJson = animatorEntry.getValue().getAsJsonObject();
                String boneName = animatorJson.get("name").getAsString();

                Timeline frames = Timeline.dynamic();

                for (JsonElement keyFrameElement : animatorJson.get("keyframes").getAsJsonArray()) {

                    JsonObject keyframeJson = keyFrameElement.getAsJsonObject();
                    JsonObject dataPoint = keyframeJson.get("data_points").getAsJsonArray().get(0).getAsJsonObject();

                    float x = GsonUtil.parseLenientFloat(dataPoint.get("x"));
                    float y = GsonUtil.parseLenientFloat(dataPoint.get("y"));
                    float z = GsonUtil.parseLenientFloat(dataPoint.get("z"));

                    Vector3Float value = new Vector3Float(x, y, z);

                    String channel = keyframeJson.get("channel").getAsString();
                    int time = Math.round(GsonUtil.parseLenientFloat(keyframeJson.get("time")) * TICKS_PER_SECOND);

                    if (channel.equals("scale")) {
                        // TODO: support scale frames
                        throw new IOException("Scale frames aren't supported yet." +
                                " Check animation " + name + " and bone " + boneName);
                    }

                    if (channel.equals("position")) {
                        value = value.divide(ElementScale.BLOCK_SIZE, ElementScale.BLOCK_SIZE, -ElementScale.BLOCK_SIZE);
                    }

                    frames.put(time, Timeline.Channel.valueOf(channel.toUpperCase()), value);
                }

                animators.put(boneName, frames.sorted());
            }

            animations.put(name, new Animation(name, loopMode, animators));
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
