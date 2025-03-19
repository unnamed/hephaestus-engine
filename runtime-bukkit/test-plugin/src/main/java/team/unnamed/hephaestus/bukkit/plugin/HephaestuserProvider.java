package team.unnamed.hephaestus.bukkit.plugin;

import org.jetbrains.annotations.NotNull;

public final class HephaestuserProvider {
    private static Hephaestuser instance;

    static void set(final @NotNull Hephaestuser instance) {
        if (HephaestuserProvider.instance != null) {
            throw new IllegalStateException("Already initialized!");
        }
        HephaestuserProvider.instance = instance;
    }

    public static @NotNull Hephaestuser get() {
        if (instance == null) {
            throw new IllegalStateException("Not initialized yet!");
        }
        return instance;
    }
}
