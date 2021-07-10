package team.unnamed.molang.expression;

import team.unnamed.molang.binding.Bind;
import team.unnamed.molang.binding.CallableBinding;
import team.unnamed.molang.binding.ObjectBinding;
import team.unnamed.molang.context.EvalContext;

import java.util.List;

public class IdentifierExpression
        implements Expression {

    private final String identifier;

    public IdentifierExpression(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Object eval(EvalContext context) {
        Object binding = context.getBinding(identifier);
        if (binding instanceof CallableBinding) {
            // MoLang specification declares that
            // parenthesis are optional to call a
            // function, so...
            return ((CallableBinding) binding).call();
        }
        // unknown, return null
        return null;
    }

    @Override
    public Object evalProperty(EvalContext context, Expression property) {
        if (!(property instanceof IdentifierExpression)) {
            // TODO: This is invalid, should we throw an error?
            return null;
        }
        Object binding = context.getBinding(identifier);
        if (binding instanceof ObjectBinding) {
            String propertyName = ((IdentifierExpression) property).getIdentifier();
            return ((ObjectBinding) binding).getProperty(propertyName);
        }
        return null;
    }

    @Override
    public Object call(EvalContext context, List<Expression> arguments) {
        Object binding = context.getBinding(identifier);
        // not callable, return null
        return Bind.callBinding(context, binding, arguments);
    }

    @Override
    public String toString() {
        return "identifier(" + identifier + ")";
    }

}
