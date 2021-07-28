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

        bindCallable("abs", args -> Math.abs(toDouble(args[0])));
        bindCallable("acos", args -> Math.acos(toDouble(args[0])));
        bindCallable("asin", args -> Math.asin(toDouble(args[0])));
        bindCallable("atan", args -> Math.atan(toDouble(args[0])));
        bindCallable("atan2", args -> Math.atan2(toDouble(args[0]), toDouble(args[1])));
        bindCallable("ceil", args -> Math.ceil(toDouble(args[0])));
        bindCallable("clamp", args -> Math.max(Math.min(toDouble(args[0]), toDouble(args[2])), toDouble(args[1])));
        bindCallable("cos", args -> Math.cos(toRadians(args[0])));
        bindCallable("die_roll", args -> {
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

        bindCallable("exp", args -> Math.exp(toDouble(args[0])));
        bindCallable("floor", args -> Math.floor(toDouble(args[0])));
        // TODO: hermite_blend, lerp, lerprotate
        bindCallable("ln", args -> Math.log(toDouble(args[0])));
        bindCallable("max", args -> Math.max(toDouble(args[0]), toDouble(args[1])));
        bindCallable("min", args -> Math.min(toDouble(args[0]), toDouble(args[1])));
        bindCallable("mod", args -> toDouble(args[0]) % toDouble(args[1]));
        bindings.put("pi", Math.PI);
        bindCallable("pow", args -> Math.pow(toDouble(args[0]), toDouble(args[1])));
        // TODO: random, random_integer
        bindCallable("round", args -> Math.round(toDouble(args[0])));
        bindCallable("sin", args -> Math.sin(toRadians(args[0])));
        bindCallable("sqrt", args -> Math.sqrt(toDouble(args[0])));
        // TODO: trunc
    }

    private void bindCallable(String name, CallableBinding binding) {
        bindings.put(name, binding);
    }

    @Override
    public Object getProperty(String name) {
        return bindings.getOrDefault(name, 0);
    }

    @Override
    public void setProperty(String name, Object value) {
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
