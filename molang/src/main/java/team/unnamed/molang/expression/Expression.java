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

}
