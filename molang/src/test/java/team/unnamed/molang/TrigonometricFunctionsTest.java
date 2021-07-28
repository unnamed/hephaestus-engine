package team.unnamed.molang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TrigonometricFunctionsTest {

    private static ScriptEngine engine;

    @BeforeAll
    public static void setEngine() {
        engine = new ScriptEngineManager().getEngineByName("molang");;
    }

    private void assertSin(double angleDeg) throws ScriptException {
        double expected = Math.sin(Math.toRadians(angleDeg));
        double given = (double) engine.eval("math.sin(" + angleDeg + ")");
        Assertions.assertEquals(expected, given);
    }

    @Test
    public void testSin() {
        try {
            for (double angle = 0; angle <= 360; angle += 0.5) {
                assertSin(angle);
            }
        } catch (ScriptException e) {
            Assertions.fail(e);
        }
    }

    private void assertCos(double angleDeg) throws ScriptException {
        double expected = Math.cos(Math.toRadians(angleDeg));
        double given = (double) engine.eval("math.cos(" + angleDeg + ")");
        Assertions.assertEquals(expected, given);
    }

    @Test
    public void testCos() {
        try {
            for (double angle = 0; angle <= 360; angle += 0.5) {
                assertCos(angle);
            }
        } catch (ScriptException e) {
            Assertions.fail(e);
        }
    }

}
