package team.unnamed.molang.expression;

import team.unnamed.molang.context.EvalContext;

public class IdentifierExpression
        implements Expression {

    private final String identifier;

    public IdentifierExpression(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public Object eval(EvalContext context) {
        return identifier;
    }

    @Override
    public String toString() {
        return "identifier(" + identifier + ")";
    }

}
