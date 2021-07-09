package team.unnamed.molang.expression;

import team.unnamed.molang.context.EvalContext;

/**
 * Literal {@link Expression} implementation,
 * the parsed value is the same as the
 * evaluated
 */
public class LiteralExpression
        implements Expression {

    private final Object value;

    public LiteralExpression(Object value) {
        this.value = value;
    }

    @Override
    public Object eval(EvalContext context) {
        return value;
    }

    @Override
    public String toString() {
        return "literal(" + value.toString() + ")";
    }

}
