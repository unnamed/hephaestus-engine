package team.unnamed.hephaestus.resourcepack.java;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaItem {

    private final String parent = "item/handheld";
    private final Map<String, String> textures;
    private final List<Override> overrides;

    public JavaItem(List<Override> overrides) {
        this.textures = new HashMap<>();
        this.overrides = overrides;

        this.textures.put("layer0", "item/bone");
    }

    public static class Override {

        private final Predicate predicate;
        private final String model;

        public Override(int data, String model) {
            this.predicate = new Predicate(data);
            this.model = model;
        }

        public static class Predicate {

            @SerializedName("custom_model_data")
            private final int customModelData;

            public Predicate(int customModelData) {
                this.customModelData = customModelData;
            }
        }
    }

}