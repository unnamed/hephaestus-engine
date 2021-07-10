package team.unnamed.molang.expression;

import team.unnamed.molang.context.EvalContext;

/**
 * A fundamental interface representing every
 * possible expression in the MoLang language
 */
public interface Expression {

    /**
     * Evaluates the expression using
     * the given {@code context}
     */
    Object eval(EvalContext context);

    /**
     * Evaluates the expression using
     * the given {@code context} and
     * trying to convert it to a float,
     * returns zero if not possible
     */
    default float evalAsFloat(EvalContext context) {
        Object result = eval(context);
        if (!(result instanceof Number)) {
            return 0;
        } else {
            return ((Number) result).floatValue();
        }
    }

}
