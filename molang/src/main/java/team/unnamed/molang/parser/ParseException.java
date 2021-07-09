package team.unnamed.molang.parser;

import team.unnamed.molang.context.ScriptCursor;

import java.io.IOException;

/**
 * Exception that can be thrown during the
 * parsing phase
 */
public class ParseException extends IOException {

    private final ScriptCursor cursor;

    public ParseException(ScriptCursor cursor) {
        this.cursor = cursor;
    }

    public ParseException(String message, ScriptCursor cursor) {
        super(appendCursor(message, cursor));
        this.cursor = cursor;
    }

    public ParseException(Throwable cause, ScriptCursor cursor) {
        super(cause);
        this.cursor = cursor;
    }

    public ParseException(String message, Throwable cause, ScriptCursor cursor) {
        super(appendCursor(message, cursor), cause);
        this.cursor = cursor;
    }

    public ScriptCursor getCursor() {
        return cursor;
    }

    private static String appendCursor(String message, ScriptCursor cursor) {
        // default format for exception messages, i.e.
        // "unexpected token: '%'"
        // "    at line 2, column 6"
        return message + "\n\tat " + cursor.toString();
    }

}
