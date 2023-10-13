package team.unnamed.hephaestus.playermodel;

public class Skin {

    public enum Type {
        NORMAL,
        SLIM
    }

    private final String signature;
    private final String value;
    private final Type type;

    public Skin(String signature, String value, Type type) {
        this.signature = signature;
        this.value = value;
        this.type = type;
    }

    public String signature() {
        return this.signature;
    }

    public String value() {
        return this.value;
    }

    public Type type() {
        return this.type;
    }
}