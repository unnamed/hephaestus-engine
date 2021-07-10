package team.unnamed.molang.expression;

import team.unnamed.molang.context.EvalContext;

import java.util.List;

/**
 * Function call expression implementation
 */
public class CallExpression
        implements Expression {

    private final Expression function;
    private final List<Expression> arguments;

    public CallExpression(Expression function, List<Expression> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    public Expression getFunction() {
        return function;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public Object eval(EvalContext context) {
        return function.call(context, arguments);
    }

    @Override
    public String toString() {
        return "call(" + function + ", " + arguments + ")";
    }
}
