package team.unnamed.hephaestus.model.animation;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class AnimationQueue {

    private final Map<String, KeyFrame> lastFrames = new HashMap<>();
    private final Map<String, Iterator<KeyFrame>> iterators = new HashMap<>();
    private int noNext;

    private final Deque<ModelAnimation> animations = new LinkedList<>();
    private ModelAnimation animation;

    private void createIterators(ModelAnimation animation) {
        iterators.clear();
        animation.getAnimationsByBoneName().forEach((name, list) ->
                iterators.put(name, list.iterator()));
    }

    public void pushAnimation(ModelAnimation animation) {
        animations.addFirst(animation);
    }

    private void nextAnimation() {
        animation = animations.pollLast();
    }

    public KeyFrame next(String boneName) {
        if (animation == null) {
            return KeyFrame.INITIAL;
        }
        Iterator<KeyFrame> iterator = iterators.get(boneName);
        if (iterator == null) {
            return KeyFrame.INITIAL;
        } else if (iterator.hasNext()) {
            KeyFrame frame = iterator.next();
            if (!iterator.hasNext()) {
                if (++noNext >= iterators.size()) {
                    // all iterators fully-consumed
                    if (animation.isLoop()) {
                        createIterators(animation);
                        noNext = 0;
                    } else {
                        nextAnimation();
                    }
                }
            }
            lastFrames.put(boneName, frame);
            return frame;
        } else {
            return lastFrames.getOrDefault(boneName, KeyFrame.INITIAL);
        }
    }

}
