package team.unnamed.molang.binding;

public class StandardBindings {

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
                // me fui a comer XD
                return 0;
            };
        }
        return 0;
    };

}
