package team.unnamed.hephaestus.animation;

import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.struct.Vector3Float;

import java.util.Iterator;

/**
 * Data structure for holding and iterating {@link KeyFrame}
 * instances to perform model animations
 */
public interface KeyFrameList extends Iterable<KeyFrame> {

    /**
     * Adds the given {@code value} to the timeline in
     * the specified {@code tick} and {@code channel}
     */
    void put(int position, Channel channel, Vector3Float value);

    /**
     * Creates an iterator that iterates over
     * keyframes stored in this keyframe list.
     */
    @NotNull
    @Override
    Iterator<KeyFrame> iterator();

    enum Channel {
        POSITION,
        ROTATION,
        SCALE
    }

}
