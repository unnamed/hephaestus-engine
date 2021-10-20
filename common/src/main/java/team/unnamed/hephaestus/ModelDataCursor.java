package team.unnamed.hephaestus;

/**
 * Object holding a {@code cursor} for using unique
 * custom model data for all bones
 */
public class ModelDataCursor {

    // Represents the next custom model
    // data to be returned by next()
    private int cursor;

    public ModelDataCursor(int cursor) {
        this.cursor = cursor;
    }

    /**
     * Returns the next custom model data
     * without modifying it
     */
    public int getNext() {
        return cursor;
    }

    /**
     * Returns the next custom model data
     * and adds one to it for the next custom
     * model data
     */
    public int next() {
        return cursor++;
    }

}
