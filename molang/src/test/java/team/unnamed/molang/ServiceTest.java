package team.unnamed.molang;

import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ServiceTest {

    @Test
    public void test() {

        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("molang");

        try {
            System.out.println(engine.eval(
                    "math.pi"
            ));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

}
