package team.unnamed.hephaestus.minestom;

import net.minestom.server.network.socket.Server;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.reader.BBModelReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class Models {

    public static final Model REDSTONE_MONSTROSITY;

    static {
        try (InputStream input = Server.class.getClassLoader()
                        .getResourceAsStream("redstone_monstrosity.bbmodel")) {
            Objects.requireNonNull(input, "redstone monstrosity");
            REDSTONE_MONSTROSITY = new BBModelReader().read(new InputStreamReader(input));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

}
