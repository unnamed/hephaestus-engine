package team.unnamed.molang.context;

import team.unnamed.molang.parser.ParseException;

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

    public int getCurrent() {
        return current;
    }

    public ScriptCursor getCursor() {
        return cursor;
    }

}
