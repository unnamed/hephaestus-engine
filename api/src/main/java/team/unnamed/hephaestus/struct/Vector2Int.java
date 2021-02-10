package team.unnamed.hephaestus.struct;

import java.util.Objects;

/**
 * A simple vector that uses 32-bit
 * integers to represents the coordinates
 *
 * <p>Note that this class is immutable,
 * some operations create a new instance</p>
 */
public class Vector2Int {

    private final int x;
    private final int y;

    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2Int that = (Vector2Int) o;
        return x == that.x
                && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Vector2Int{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
