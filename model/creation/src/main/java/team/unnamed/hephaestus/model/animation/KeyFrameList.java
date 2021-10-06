package team.unnamed.hephaestus.model.animation;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Data structure for holding and iterating {@link KeyFrame}
 * instances to perform model animations
 */
public interface KeyFrameList extends Iterable<KeyFrame> {

    /**
     * Adds the given {@code frame} to the
     * specified {@code position} in this
     * keyframe list
     */
    void put(int position, KeyFrame frame);

    /**
     * Creates an iterator that iterates over
     * keyframes stored in this keyframe list.
     */
    @NotNull
    @Override
    Iterator<KeyFrame> iterator();

}
