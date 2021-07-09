package team.unnamed.molang.expression;

import team.unnamed.molang.context.EvalContext;

import java.util.List;

/**
 * Function call expression implementation
 */
public class CallExpression
        implements Expression {

    private final Expression thiz;
    private final List<Expression> arguments;

    public CallExpression(Expression thiz, List<Expression> arguments) {
        this.thiz = thiz;
        this.arguments = arguments;
    }

    @Override
    public Object eval(EvalContext context) {
        return thiz.eval(context);
    }

    @Override
    public String toString() {
        return "call(" + thiz + ", " + arguments + ")";
    }
}
