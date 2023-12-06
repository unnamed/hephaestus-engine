package team.unnamed.hephaestus.player;

import net.kyori.examination.Examinable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a player skin, which is composed of a
 * signature and a value, both of them are Base64
 * encoded strings.
 *
 * @since 1.0.0
 */
@ApiStatus.NonExtendable
public interface Skin extends Examinable {
    @Contract("_, _, _ -> new")
    static @NotNull Skin skin(final @NotNull String signature, final @NotNull String value, final @NotNull Type type) {
        return new SkinImpl(signature, value, type);
    }

    @NotNull String signature();

    @NotNull String value();

    @NotNull Type type();

    enum Type {
        NORMAL,
        SLIM
    }
}