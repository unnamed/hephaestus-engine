package team.unnamed.hephaestus.minestom;

import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.reader.ModelReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Models {

    public static final Model REDSTONE_MONSTROSITY;

    static {
        try (InputStream input = Server.class.getClassLoader()
                        .getResourceAsStream("redstone_monstrosity.json")) {
            Objects.requireNonNull(input, "redstone monstrosity");
            REDSTONE_MONSTROSITY = ModelReader.blockbench().read(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

}
