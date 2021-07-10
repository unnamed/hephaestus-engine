package team.unnamed.molang;

import team.unnamed.molang.parser.MoLangParser;
import team.unnamed.molang.parser.StandardMoLangParser;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MoLangScriptEngineFactory
        implements ScriptEngineFactory {

    private static final List<String> NAMES = Arrays.asList("molang", "hephaestus-molang");
    private static final List<String> EXTENSIONS = Collections.singletonList("uhml");
    private static final List<String> MIME_TYPES = Arrays.asList("application/molang", "text/molang");

    private static final String ENGINE_VERSION = "0.1.0";
    private static final String MOLANG_VERSION = "1.17.0.2";

    private final MoLangParser parser = new StandardMoLangParser();

    @Override
    public String getEngineName() {
        return "Unnamed Hephaestus MoLang";
    }

    @Override
    public String getEngineVersion() {
        return ENGINE_VERSION;
    }

    @Override
    public List<String> getExtensions() {
        return EXTENSIONS;
    }

    @Override
    public List<String> getMimeTypes() {
        return MIME_TYPES;
    }

    @Override
    public List<String> getNames() {
        return NAMES;
    }

    @Override
    public String getLanguageName() {
        return "MoLang";
    }

    @Override
    public String getLanguageVersion() {
        return MOLANG_VERSION;
    }

    @Override
    public Object getParameter(String key) {
        switch (key) {
            case "javax.script.engine_version":
                return getEngineVersion();
            case "javax.script.engine":
                return getEngineName();
            case "javax.script.language":
                return getLanguageName();
            case "javax.script.language_version":
                return getLanguageVersion();
            case "javax.script.name":
                return getNames().get(0);
            default:
                return null;
        }
    }

    @Override
    public String getMethodCallSyntax(String object, String method, String... args) {
        // object.method(arg0, arg1, arg2)
        StringBuilder builder = new StringBuilder()
                .append(object).append('.')
                .append(method).append('(');
        for (int i = 0; i < args.length; i++) {
            if (i != 0) {
                builder.append(", ");
            }
            builder.append(args[i]);
        }
        builder.append(')');
        return builder.toString();
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return getMethodCallSyntax("query", "print", toDisplay);
    }

    @Override
    public String getProgram(String... statements) {
        StringBuilder builder = new StringBuilder();
        for (String statement : statements) {
            builder.append(statement).append(';');
        }
        return builder.toString();
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new MoLangScriptEngine(this, parser);
    }
}
