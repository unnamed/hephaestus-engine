package team.unnamed.hephaestus.util;

/**
 * Utility class for maths
 * in models
 */
public final class ModelMath {

    private ModelMath() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static float shrink(float p) {
        return 3.2F + 0.6F * p;
    }

}