package team.unnamed.molang.binding;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Math function bindings inside an object
 * binding, commonly named 'math'
 */
public class MathBinding
        implements ObjectBinding {

    private static final Random RANDOM = new Random();
    private static final int DECIMAL_PART = 4;

    private final Map<String, Object> bindings = new HashMap<>();

    public MathBinding() {

        bindings.put("abs", (CallableBinding) args -> Math.abs(toDouble(args[0])));
        bindings.put("acos", (CallableBinding) args -> Math.acos(toDouble(args[0])));
        bindings.put("asin", (CallableBinding) args -> Math.asin(toDouble(args[0])));
        bindings.put("atan", (CallableBinding) args -> Math.atan(toDouble(args[0])));
        bindings.put("atan2", (CallableBinding) args -> Math.atan2(toDouble(args[0]), toDouble(args[1])));
        bindings.put("ceil", (CallableBinding) args -> Math.ceil(toDouble(args[0])));
        bindings.put("clamp", (CallableBinding) args -> Math.max(Math.min(toDouble(args[0]), toDouble(args[2])), toDouble(args[1])));
        bindings.put("cos", (CallableBinding) args -> Math.cos(toRadians(args[0])));
        bindings.put("die_roll", (CallableBinding) args -> {
            int amount = (int) toDouble(args[0]);
            int low = (int) (toDouble(args[1]) * DECIMAL_PART);
            int high = (int) (toDouble(args[2]) * DECIMAL_PART) - low;
            double result = 0;
            for (int i = 0; i < amount; i++) {
                result += RANDOM.nextInt(high) + low;
            }
            return result / DECIMAL_PART;
        });
        // TODO: die_roll_integer

        bindings.put("exp", (CallableBinding) args -> Math.exp(toDouble(args[0])));
        bindings.put("floor", (CallableBinding) args -> Math.floor(toDouble(args[0])));
        // TODO: hermite_blend, lerp, lerprotate
        bindings.put("ln", (CallableBinding) args -> Math.log(toDouble(args[0])));
        bindings.put("max", (CallableBinding) args -> Math.max(toDouble(args[0]), toDouble(args[1])));
        bindings.put("min", (CallableBinding) args -> Math.min(toDouble(args[0]), toDouble(args[1])));
        bindings.put("mod", (CallableBinding) args -> toDouble(args[0]) % toDouble(args[1]));
        bindings.put("pi", Math.PI);
        bindings.put("pow", (CallableBinding) args -> Math.pow(toDouble(args[0]), toDouble(args[1])));
        // TODO: random, random_integer
        bindings.put("round", (CallableBinding) args -> Math.round(toDouble(args[0])));
        bindings.put("sin", (CallableBinding) args -> Math.sin(toRadians(args[0])));
        bindings.put("sqrt", (CallableBinding) args -> Math.sqrt(toDouble(args[0])));
        // TODO: trunc
    }

    @Override
    public Object getProperty(String name) {
        return bindings.getOrDefault(name, 0);
    }

    private static double toDouble(Object object) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        } else {
            return 0D;
        }
    }

    private static double toRadians(Object object) {
        if (object instanceof Number) {
            return Math.toRadians(((Number) object).doubleValue());
        } else {
            // not fail-fast
            return 0D;
        }
    }

}
