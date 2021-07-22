package team.unnamed.hephaestus.reader.blockbench;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import team.unnamed.hephaestus.model.animation.KeyFrame;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelBoneAnimation;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Serialization;

import java.util.*;

/**
 * Converts JSON to {@link ModelAnimation}
 * <p>The Blockbench format is explicitly supported
 *  by the Blockbench model editor</p>
 */
public class BlockbenchModelAnimationsReader {

    public Map<String, ModelAnimation> read(JsonObject json) {

        Map<String, ModelAnimation> animations = new HashMap<>();

        JsonElement animationsElement = json.get("animations");
        if (animationsElement == null) {
            return animations;
        }

        for (JsonElement animationElement : animationsElement.getAsJsonArray()) {
            JsonObject animationJson = animationElement.getAsJsonObject();

            String name = animationJson.get("name").getAsString();
            boolean loop = animationJson.get("loop").getAsString().equals("loop");
            float length = Math.round(animationJson.get("length").getAsFloat()*20);

            Map<String, ModelBoneAnimation> boneAnimations = new HashMap<>();
            for (Map.Entry<String, JsonElement> boneAnimationEntry : animationJson.get("animators")
                    .getAsJsonObject()
                    .entrySet()) {

                JsonObject boneAnimationJson = boneAnimationEntry.getValue().getAsJsonObject();
                String boneName = boneAnimationJson.get("name").getAsString();

                List<KeyFrame> rotationFrames = new ArrayList<>();
                List<KeyFrame> positionFrames = new ArrayList<>();
                List<KeyFrame> sizeFrames = new ArrayList<>();

                for (JsonElement keyFrameElement : boneAnimationJson.get("keyframes").getAsJsonArray()) {
                    JsonObject keyframeJson = keyFrameElement.getAsJsonObject();
                    JsonObject dataPoint = keyframeJson.get("data_points").getAsJsonArray().get(0).getAsJsonObject();

                    float x = Serialization.parseLenientFloat(dataPoint.get("x"));
                    float y = Serialization.parseLenientFloat(dataPoint.get("y"));
                    float z = Serialization.parseLenientFloat(dataPoint.get("z"));

                    Vector3Float value = new Vector3Float(x, y, z);

                    String channel = keyframeJson.get("channel").getAsString();
                    float time = Math.round(keyframeJson.get("time").getAsFloat()*20);
                    KeyFrame keyFrame = new KeyFrame(
                            time,
                            value
                    );

                    switch (channel) {
                        case "scale":
                            sizeFrames.add(keyFrame);
                            break;
                        case "rotation":
                            rotationFrames.add(keyFrame);
                            break;
                        case "position":
                            positionFrames.add(keyFrame);
                            break;
                    }
                }

                boneAnimations.put(boneName, new ModelBoneAnimation(positionFrames, rotationFrames, sizeFrames));
            }

            animations.put(name, new ModelAnimation(name, loop, length, boneAnimations));
        }

        return animations;
    }
}