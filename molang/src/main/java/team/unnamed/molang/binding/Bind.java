package team.unnamed.molang.binding;

import team.unnamed.molang.context.EvalContext;
import team.unnamed.molang.expression.Expression;

import java.util.List;

/**
 * Class holding some default bindings and
 * static utility methods for ease working
 * with bindings
 */
public final class Bind {

    /**
     * Default bindings for math
     * @see MathBinding
     */
    public static final ObjectBinding MATH_BINDING = new MathBinding();

    /**
     * Default bindings for queries
     * @see QueryBinding
     */
    public static final ObjectBinding QUERY_BINDING = new QueryBinding();

    private Bind() {
    }

    public static Object callBinding(
            EvalContext context,
            Object binding,
            List<Expression> arguments
    ) {
        if (!(binding instanceof CallableBinding)) {
            // TODO: This isn't fail-fast, check this in specification
            return 0;
        }

        Object[] evaluatedArguments = new Object[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            evaluatedArguments[i] = arguments.get(i).eval(context);
        }
        return ((CallableBinding) binding).call(evaluatedArguments);
    }

}
