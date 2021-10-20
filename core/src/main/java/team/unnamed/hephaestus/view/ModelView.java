package team.unnamed.hephaestus.view;

import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.ModelAnimation;
import team.unnamed.hephaestus.struct.Vector3Double;
import team.unnamed.hephaestus.struct.Vector3Float;

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

    //#region Entire View Handling methods
    /**
     * Colorizes this view using the specified
     * {@code r} (red), {@code g} (green) and
     * {@code b} (blue) color components
     */
    void colorize(int r, int g, int b);
    //#endregion

    //#region Bone Handling methods
    /**
     * Finds the bone with the specified {@code name}
     * and colorizes it using the specified RGB color
     * components
     * @throws NullPointerException If no bone with
     * the given name is found
     */
    void colorizeBone(String name, int r, int g, int b);

    /**
     * Teleports the bone with the specified {@code name}
     * to the given relative {@code position} (added to
     * the global model position to obtain the global bone
     * position)
     * @param name The teleported bone name
     * @param position The relative target position
     * @throws NullPointerException If no bone with
     * the given name is found
     */
    void moveBone(String name, Vector3Float position);

    /**
     * Rotates the bone with the specified {@code name}
     * to the given {@code rotation}
     * @param name The rotated bone name
     * @param rotation The target rotation
     * @throws NullPointerException If no bone with the
     * given name is found
     */
    void rotateBone(String name, Vector3Double rotation);
    //#endregion

    //#region Animation Handling methods
    /**
     * Finds and plays the animation with the
     * specified {@code name} for this model view
     * instance.
     *
     * @param name The animation name
     * @see Model#getAnimations()
     */
    void playAnimation(String name);

    /**
     * Plays the given {@code animation} for
     * this model view instance
     *
     * @param animation The animation to play
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

    /**
     * Ticks animations, makes required bones pass
     * to the next animation frame
     */
    void tickAnimations();
    //#endregion

}
