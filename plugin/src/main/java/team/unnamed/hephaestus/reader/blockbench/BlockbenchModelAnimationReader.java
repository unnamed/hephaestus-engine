package team.unnamed.hephaestus.reader.blockbench;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import team.unnamed.hephaestus.model.animation.KeyFrame;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelBoneAnimation;
import team.unnamed.hephaestus.reader.ModelAnimationsReader;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class BlockbenchModelAnimationReader implements ModelAnimationsReader {

    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "3.6"
    );

    @Override
    public Map<String, ModelAnimation> read(Reader reader) throws IOException {
        JsonObject json = JSON_PARSER.parse(reader).getAsJsonObject();
        JsonElement formatVersionElement = json.get("meta").getAsJsonObject().get("format_version");

        if (
                formatVersionElement == null
                        || !SUPPORTED_FORMATS.contains(formatVersionElement.getAsString())
        ) {
            throw new IOException("Provided JSON doesn't have a valid format version");
        }

        Map<String, ModelAnimation>  animations = new HashMap<>();
        json.get("animations").getAsJsonArray().forEach(animationElement -> {
            JsonObject animationJson = animationElement.getAsJsonObject();

            String name = animationJson.get("name").getAsString();
            boolean loop = animationJson.get("loop").getAsString().equals("loop");
            float length = animationJson.get("length").getAsFloat();

            Map<String, ModelBoneAnimation> boneAnimations = new HashMap<>();
            for (Map.Entry<String, JsonElement> boneAnimationEntry : animationJson.get("animators").getAsJsonObject().entrySet()) {
                JsonObject boneAnimationJson = boneAnimationEntry.getValue().getAsJsonObject();
                String boneName = boneAnimationJson.get("name").getAsString();

                List<KeyFrame> rotationFrames = new ArrayList<>();
                List<KeyFrame> positionFrames = new ArrayList<>();

                boneAnimationJson.get("keyframes").getAsJsonArray().forEach(keyframeElement -> {
                    JsonObject keyframeJson = keyframeElement.getAsJsonObject();
                    JsonObject dataPoint = keyframeJson.get("data_points").getAsJsonArray().get(0).getAsJsonObject();

                    float x = dataPoint.get("x").getAsJsonPrimitive().isString() ?
                            Float.parseFloat(dataPoint.get("x").getAsString().replace(",",  "."))
                            :
                            dataPoint.get("x").getAsFloat();

                    float y = dataPoint.get("y").getAsJsonPrimitive().isString() ?
                            Float.parseFloat(dataPoint.get("y").getAsString().replace(",",  "."))
                            :
                            dataPoint.get("y").getAsFloat();

                    float z = dataPoint.get("z").getAsJsonPrimitive().isString() ?
                            Float.parseFloat(dataPoint.get("z").getAsString().replace(",",  "."))
                            :
                            dataPoint.get("z").getAsFloat();

                    Vector3Float value = new Vector3Float(
                            x,
                            y,
                            z
                    );

                    String channel = keyframeJson.get("channel").getAsString();
                    float time = keyframeJson.get("time").getAsFloat();
                    KeyFrame keyFrame = new KeyFrame(
                            time,
                            value
                    );

                    switch (channel) {
                        case "rotation":
                            rotationFrames.add(keyFrame);
                            break;
                        case "position":
                            positionFrames.add(keyFrame);
                            break;
                    }
                });

                boneAnimations.put(boneName, new ModelBoneAnimation(positionFrames, rotationFrames));
            }

            animations.put(name, new ModelAnimation(name, loop, length, boneAnimations));
        });


        return animations;
    }
}