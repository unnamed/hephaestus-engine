package team.unnamed.molang.expression;

import team.unnamed.molang.context.EvalContext;

public class NegationExpression
        implements Expression {

    private final Expression expression;

    public NegationExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public float evalAsFloat(EvalContext context) {
        return -expression.evalAsFloat(context);
    }

    @Override
    public Object eval(EvalContext context) {
        return evalAsFloat(context);
    }

}
