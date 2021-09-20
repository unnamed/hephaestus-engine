package team.unnamed.hephaestus.model.view;

import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimation;

/**
 * Base abstraction for representing a {@link Model}
 * view, allows multiple viewers and animation playing.
 *
 * Platform un-specific, to use more specific properties,
 * see their implementations in runtime-* projects
 */
public interface ModelView {

    /**
     * Returns the model being viewed
     * from this view instance
     */
    Model getModel();

    /**
     * Colorizes this view using the specified
     * {@code r} (red), {@code g} (green) and
     * {@code b} (blue) color components
     */
    void colorize(int r, int g, int b);

    /**
     * Finds the bone with the specified {@code name}
     * and colorizes it using the specified RGB color
     * components
     * @throws IllegalArgumentException If no bone with
     * the given name is found
     */
    void colorizeBone(String name, int r, int g, int b);

    //#region Animation Handling methods
    /**
     * Finds and plays the animation with the
     * specified {@code name} for this model view
     * instance.
     *
     * @param name The animation name
     * @throws IllegalArgumentException If the given
     * animation isn't registered for this model
     * @see Model#getAnimations()
     */
    void playAnimation(String name);

    /**
     * Plays the given {@code animation} for
     * this model view instance
     *
     * @param animation The animation to play
     * @throws IllegalArgumentException If the given
     * animation isn't registered for this model
     * @see Model#getAnimations()
     */
    void playAnimation(ModelAnimation animation);

    /**
     * Stops playing the animation with the
     * specified {@code name} for this model view
     * @param name The animation to stop
     * @return True if animation was stopped, false
     * otherwise
     */
    boolean stopAnimation(String name);

    /**
     * Stops all animations in the queue
     * for this model view
     */
    void stopAllAnimations();
    //#endregion

}
