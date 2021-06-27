package team.unnamed.hephaestus.reader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import team.unnamed.hephaestus.model.animation.KeyFrame;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelBoneAnimation;
import team.unnamed.hephaestus.struct.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.io.Reader;
import java.util.*;

public class BedrockModelAnimationsReader implements ModelAnimationsReader {

    private final JsonParser jsonParser = new JsonParser();

    @Override
    public List<ModelAnimation> read(Reader reader) {

        List<ModelAnimation> animations = new ArrayList<>();

        JsonObject json = this.jsonParser.parse(reader).getAsJsonObject();
        JsonObject animationsJson = json.get("animations").getAsJsonObject();

        for (Map.Entry<String, JsonElement> animationEntry : animationsJson.entrySet()) {
            String name = animationEntry.getKey();
            JsonObject animationJson = animationEntry.getValue().getAsJsonObject();

            boolean loop = animationJson.get("loop").getAsBoolean();
            float animationLength = animationJson.get("animation_length").getAsFloat();

            JsonObject bonesJson = animationJson.get("bones").getAsJsonObject();
            Map<String, ModelBoneAnimation> boneAnimations = new HashMap<>();

            for (Map.Entry<String, JsonElement> boneEntry : bonesJson.entrySet()) {
                String boneName = boneEntry.getKey();
                JsonObject boneJson = boneEntry.getValue().getAsJsonObject();

                JsonElement rotationElement = boneJson.get("rotation");
                JsonElement positionElement = boneJson.get("position");

                List<KeyFrame> rotationFrames = readKeyFrames(rotationElement);
                List<KeyFrame> positionFrames = readKeyFrames(positionElement);

                boneAnimations.put(
                        boneName,
                        new ModelBoneAnimation(
                                positionFrames,
                                rotationFrames
                        )
                );
            }

            animations.add(new ModelAnimation(
                    name,
                    loop,
                    animationLength,
                    boneAnimations
            ));
        }

        return animations;
    }

    private List<KeyFrame> readKeyFrames(JsonElement element) {
        if (element == null) {
            // pero si no hay rotation o position, nomas no le ponemos keyframes
            return new ArrayList<>();
        } else if (element.isJsonArray()) {
            return new ArrayList<>(Collections.singletonList(new KeyFrame(
                    0,
                    Vectors.getVector3FloatFromJson(element)
            )));
        } else {
            JsonObject json = element.getAsJsonObject();
            List<KeyFrame> frames = new ArrayList<>();

            for (Map.Entry<String, JsonElement> frameEntry : json.entrySet()) {
                float position = Float.parseFloat(frameEntry.getKey());
                Vector3Float value = Vectors.getVector3FloatFromJson(frameEntry.getValue());

                frames.add(new KeyFrame(
                        position,
                        value
                ));
            }

            return frames;
        }
    }

}