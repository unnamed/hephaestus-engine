# Hephaestus MoLang Engine
Hephaestus-MoLang is a MoLang scripting engine for Java 8+<br>
See the MoLang specification [here](https://bedrock.dev/docs/stable/MoLang)

## Usage
The Hephaestus-MoLang engine implements interfaces in `javax.script` so
you can use MoLang as any other registered `ScriptEngine`

### Basic Usage:
```java
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

class MyProgram {

    public void run() throws ScriptException {
        ScriptEngineManager scriptEngineManager
                = new ScriptEngineManager();
        ScriptEngine engine
                = scriptEngineManager.getEngineByName("molang");
        
        System.out.println(engine.eval("math.cos(90) * 16"));
    }

}
```