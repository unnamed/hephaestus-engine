package team.unnamed.molang.binding;

/**
 * Interface for bindings that can be
 * called like functions, some default
 * callable bindings may be the trigonometric
 * functions 'math.cos', 'math.sin', etc...
 */
public interface CallableBinding {

    /**
     * Executes this callable binding
     * using the given {@code arguments}
     */
    Object call(Object... arguments);

}
