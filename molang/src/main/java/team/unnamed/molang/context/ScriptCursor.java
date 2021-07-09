package team.unnamed.molang.context;

import java.util.Objects;

/**
 * Mutable class for tracking the script
 * cursor, it's currently used for indicating
 * error locations
 */
public final class ScriptCursor
        implements Cloneable {

    private int line;
    private int column;

    public ScriptCursor(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public ScriptCursor() {
        this.line = 1;
        this.column = 1;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public void incrementColumn() {
        column++;
    }

    public void add(int character) {
        if (character == '\n') {
            // if it's a line break,
            // reset the column
            line++;
            column = 1;
        } else {
            column++;
        }
    }

    @Override
    public ScriptCursor clone() {
        return new ScriptCursor(line, column);
    }

    @Override
    public String toString() {
        return "line " + line + ", column " + column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptCursor that = (ScriptCursor) o;
        return line == that.line
                && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, column);
    }

}
