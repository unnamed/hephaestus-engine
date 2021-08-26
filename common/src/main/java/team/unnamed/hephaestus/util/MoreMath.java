package team.unnamed.hephaestus.util;

/**
 * Utility class for extending the
 * default Java's Math methods
 */
public final class MoreMath {

    private MoreMath() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Clamps the given {@code value} between
     * the given {@code min} and {@code max}
     * bounds
     */
    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float shrink(float p) {
        return 3.2F + 0.6F * p;
    }

}