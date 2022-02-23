package team.unnamed.hephaestus.view;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.hephaestus.Bone;

/**
 * The main abstraction to adapt the API to a specific
 * server version, users should not directly interact
 * with this interface, use {@link ModelViewRenderer}
 * instead
 */
@ApiStatus.Internal
public interface ModelViewController {

    /**
     * Creates a new, version-specific bone view
     * for the given {@link BukkitModelView} and
     * {@link Bone}
     *
     * <p>This method is part of the construction
     * phase, where we do not care about viewers
     * yet, we just create our objects</p>
     *
     * @param view The parent model view
     * @param bone The wrapped bone
     * @return The created bone view
     * @since 1.0.0
     */
    BukkitBoneView createBone(BukkitModelView view, Bone bone);

    /**
     * Shows the given {@link BukkitModelView} to
     * the specified {@link Player} instance, but
     * does not update the internal viewers list
     * 
     * <p>This method should be called by
     * {@link BukkitModelView#addViewer}, which
     * should take care of its internal viewers list</p>
     *
     * @param view The shown model view
     * @param player The viewer player
     */
    void show(BukkitModelView view, Player player);

    /**
     * Hides/destroys the given {@link BukkitModelView}
     * for the specified {@link Player} instance, but
     * does not update the internal viewers list
     *
     * <p>This method should be called by
     * {@link BukkitModelView#removeViewer}, which
     * should take care of the internal viewers list</p>
     *
     * @param view The hidden model view
     * @param player The viewer player
     */
    void hide(BukkitModelView view, Player player);

}