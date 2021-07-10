package team.unnamed.molang.context;

import javax.script.Bindings;

public class EvalContext {

    private final Bindings bindings;

    public EvalContext(Bindings bindings) {
        this.bindings = bindings;
    }

    public Object getBinding(String name) {
        return bindings.get(name);
    }

}
