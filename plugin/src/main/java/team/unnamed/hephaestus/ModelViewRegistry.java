package team.unnamed.hephaestus;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.view.BukkitModelView;

/**
 * Registry for global model views, views
 * registered here will be show for every
 * player in the server in the range of
 * the model view.
 */
public class ModelViewRegistry {

    /**
     * View-range for views, if distance between
     * a player and a view is less than the value
     * of this constant, the view is shown
     */
    private static final float RANGE = 20F;

    /**
     * Map containing a relation of model
     * identifier and its registered views
     */
    private final Multimap<String, BukkitModelView> views
            = HashMultimap.create();

    /**
     * Registers the given {@code view} into
     * the {@code views} map and creates it
     * for all the nearby players
     */
    public void register(BukkitModelView view) {
        this.views.put(view.getModel().getName(), view);
        Location location = view.getLocation();
        World world = location.getWorld();

        Preconditions.checkNotNull(world, "world is null!");

        // TODO: temporal, synchronization must be done by caller
        Bukkit.getScheduler().runTask(AnimationEnginePlugin.getPlugin(AnimationEnginePlugin.class), () -> {
            for (Entity entity : world.getNearbyEntities(location, RANGE, RANGE, RANGE)) {
                if (!(entity instanceof Player)) {
                    // not a player, continue
                    continue;
                }

                view.addViewer((Player) entity);
            }
        });
    }

}
