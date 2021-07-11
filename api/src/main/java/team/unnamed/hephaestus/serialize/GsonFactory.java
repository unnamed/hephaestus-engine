package team.unnamed.hephaestus.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import team.unnamed.hephaestus.struct.Vector2Int;
import team.unnamed.hephaestus.struct.Vector3Float;

/**
 * Static factory for creating
 * {@link Gson} instances with
 * specific configurations
 */
public final class GsonFactory {

    private GsonFactory() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Creates a new {@link Gson} instance
     * with specific configuration, registering
     * (de)serializers for math classes
     * {@link Vector2Int} and {@link Vector3Float}
     */
    public static Gson createDefault() {
        return new GsonBuilder()
                .registerTypeAdapter(Vector2Int.class, new Vector2IntCodec())
                .registerTypeAdapter(Vector3Float.class, new Vector3FloatCodec())
                .setPrettyPrinting()
                .create();
    }

}
