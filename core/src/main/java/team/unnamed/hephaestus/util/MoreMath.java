package team.unnamed.hephaestus.util;

/**
 * Utility class for extending the
 * default Java's Math methods
 */
public final class MoreMath {

    private MoreMath() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static float shrink(float p) {
        return 3.2F + 0.6F * p;
    }

}