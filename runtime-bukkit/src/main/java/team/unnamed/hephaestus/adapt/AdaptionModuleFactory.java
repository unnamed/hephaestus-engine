package team.unnamed.hephaestus.adapt;

import org.bukkit.Bukkit;

/**
 * Responsible for instantiating the {@link AdaptionModule}
 * depending on the current server version
 */
public final class AdaptionModuleFactory {

    // extracts the version from names like 'org.bukkit.craftbukkit.SERVER_VERSION.CraftServer'
    private static final String VERSION = Bukkit.getServer().getClass()
            .getName()
            .split("\\.")[3];

    private static final String CLASS_NAME = AdaptionModuleFactory.class.getPackage().getName()
            + '.' + VERSION + ".AdaptionModule_" + VERSION;

    private AdaptionModuleFactory() {
    }

    /**
     * Instantiates the proper {@link AdaptionModule}
     * for current server version
     * @throws IllegalStateException If module wasn't found,
     * instantiation failed or module is invalid
     */
    public static AdaptionModule create() {
        try {
            Class<?> clazz = Class.forName(CLASS_NAME);
            Object module = clazz.newInstance();
            if (!(module instanceof AdaptionModule)) {
                throw new IllegalStateException("Invalid adaption module: '"
                        + CLASS_NAME + "'. It doesn't implement " + AdaptionModule.class);
            }
            return (AdaptionModule) module;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Adaption module not found: '" + CLASS_NAME + '.');
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to instantiate adaption module", e);
        }
    }

}
