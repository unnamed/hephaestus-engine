package team.unnamed.molang.parser;

/**
 * Utility class holding utility static
 * methods for working with character
 * tokens
 */
public final class Tokens {

    public static final char UNDERSCORE = '_';
    public static final char HYPHEN = '-';
    public static final char DOT = '.';

    private Tokens() {
    }

    public static boolean isLetter(int c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }

    public static boolean isValidForIdentifier(int c) {
        return isLetter(c) || c == UNDERSCORE;
    }

}
