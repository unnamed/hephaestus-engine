package team.unnamed.molang.binding;

import team.unnamed.molang.context.EvalContext;
import team.unnamed.molang.expression.Expression;

import java.util.List;

public class Bind {

    public static final ObjectBinding MATH_BINDING = property -> {
        switch (property) {
            case "cos": {
                return (CallableBinding) args -> {
                    if (args.length > 0) {
                        return Math.cos(Math.toRadians(
                                ((Number) args[0]).floatValue()
                        ));
                    }
                    return 0;
                };
            }
            default:
                return 0;
        }
    };

    public static final ObjectBinding QUERY_BINDING = property -> {
        if (property.equalsIgnoreCase("print")) {
            return (CallableBinding) args -> {
                if (args.length > 0) {
                    System.out.println(args[0]);
                }
                return 0;
            };
        }
        return 0;
    };

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
