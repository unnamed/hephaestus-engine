package team.unnamed.molang;

import team.unnamed.molang.binding.Bind;
import team.unnamed.molang.context.EvalContext;
import team.unnamed.molang.expression.Expression;
import team.unnamed.molang.parser.MoLangParser;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class MoLangScriptEngine
        extends AbstractScriptEngine
        implements ScriptEngine {

    private final ScriptEngineFactory factory;
    private final MoLangParser parser;

    public MoLangScriptEngine(ScriptEngineFactory factory, MoLangParser parser) {
        this.factory = factory;
        this.parser = parser;
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return eval(new StringReader(script), context);
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        try {
            Bindings bindings = createBindings();
            // bindings.put("query", Bind.QUERY_BINDING);
            bindings.put("math", Bind.MATH_BINDING);

            // temporal
            List<Expression> expressions = parser.parse(reader);

            EvalContext evalContext = new EvalContext(bindings);
            Object lastResult = 0;

            for (Expression expression : expressions) {
                lastResult = expression.eval(evalContext);
            }

            return lastResult;
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return factory;
    }

}
