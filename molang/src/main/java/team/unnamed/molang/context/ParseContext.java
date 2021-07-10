package team.unnamed.molang.context;

import team.unnamed.molang.parser.ParseException;
import team.unnamed.molang.parser.Tokens;

import java.io.IOException;
import java.io.Reader;

/**
 * Represents a data object for tracking
 * information across all the parsed expressions
 */
public class ParseContext {

    private final ScriptCursor cursor = new ScriptCursor();
    private final Reader reader;

    private int current;

    public ParseContext(Reader reader) {
        this.reader = reader;
    }

    /**
     * Wrapper for {@link Reader#read()}, will throw
     * a {@link ParseException} instead of a {@link IOException}
     * if an error occurs
     */
    public int next() throws ParseException {
        try {
            int value = current = reader.read();
            if (value != -1) {
                cursor.add(value);
            }
            return value;
        } catch (IOException e) {
            throw new ParseException(
                    "Error while reading characters from stream",
                    e,
                    cursor
            );
        }
    }

    /**
     * Calls {@link ParseContext#next()} and calls it
     * again while the read char is whitespace
     * @see Tokens#isWhitespace(int)
     */
    public int nextNoWhitespace() throws ParseException {
        int value;
        do {
            value = next();
        } while (Tokens.isWhitespace(value));
        return value;
    }

    /**
     * Similar to {@link ParseContext#nextNoWhitespace()}
     * but it won't call {@link ParseContext#next()} if
     * current character isn't whitespace
     */
    public int skipWhitespace() throws ParseException {
        int localCurrent = current;
        while (Tokens.isWhitespace(localCurrent)) {
            localCurrent = next();
        }
        return localCurrent;
    }

    public int getCurrent() {
        return current;
    }

    public ScriptCursor getCursor() {
        return cursor;
    }

}
