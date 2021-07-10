package team.unnamed.molang.binding;

import java.util.HashMap;
import java.util.Map;

public class QueryBinding implements ObjectBinding {

    private final Map<String, Object> bindings = new HashMap<>();

    public QueryBinding() {
        bindCallable("print", args -> {
            System.out.println(args[0]);
            return 0;
        });
    }

    private void bindCallable(String name, CallableBinding binding) {
        bindings.put(name, binding);
    }

    @Override
    public Object getProperty(String name) {
        return bindings.get(name);
    }

    @Override
    public void setProperty(String name, Object value) {
    }

}
