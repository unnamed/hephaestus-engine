package team.unnamed.molang.binding;

/**
 * Represents an object-like binding,
 * these objects can have properties
 * (or fields) that can be read and
 * sometimes written
 */
public interface ObjectBinding {

    /**
     * Gets the property value in this
     * object with the given {@code name}
     */
    Object getProperty(String name);

    /**
     * Sets the property with the given
     * {@code name} to the specified {@code value},
     * may not be supported
     */
    void setProperty(String name, Object value);

}
