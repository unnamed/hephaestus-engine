package team.unnamed.hephaestus.model.animation;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of {@link KeyFrameList} that computes
 * intermediary keyframes at modify-time (saves them)
 * instead of read-time. This implementation consumes
 * more memory than other non-cached implementations
 */
public class MemoizedKeyFrameList implements KeyFrameList {

    private final List<KeyFrame> frames = new LinkedList<>();

    private int lastPosition;
    private KeyFrame lastFrame;

    @Override
    public void put(int position, KeyFrame frame) {

    }

    @Override
    public @NotNull Iterator<KeyFrame> iterator() {
        return frames.iterator();
    }

}
