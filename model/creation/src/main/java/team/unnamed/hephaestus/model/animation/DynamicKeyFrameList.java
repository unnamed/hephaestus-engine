package team.unnamed.hephaestus.model.animation;

import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.util.Vectors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DynamicKeyFrameList implements KeyFrameList {

    private final Map<Integer, KeyFrame> frames = new HashMap<>();

    @Override
    public void put(int position, KeyFrame frame) {
        frames.put(position, frame);
    }

    @Override
    public @NotNull Iterator<KeyFrame> iterator() {
        return new Iterator<KeyFrame>() {

            private final Iterator<Map.Entry<Integer, KeyFrame>> iterator
                    = frames.entrySet().iterator();

            private int previousPos = 0;
            private KeyFrame previous = KeyFrame.INITIAL;

            private int nextPos;
            private KeyFrame next;

            private int tick = 0;

            {
                // initial set for nextPos and next
                consumeNextKeyFrame();
            }

            private void consumeNextKeyFrame() {
                Map.Entry<Integer, KeyFrame> entry = iterator.next();
                nextPos = entry.getKey();
                next = entry.getValue();
            }

            @Override
            public boolean hasNext() {
                // if there are still next frames,
                // tick must be less than its current
                // next keyframe, or there must be more
                // non-explored entries
                return tick < nextPos || iterator.hasNext();
            }

            private KeyFrame lerp() {
                float ratio = (float) (tick - previousPos)
                        / (float) (nextPos - previousPos);
                return new KeyFrame(
                        Vectors.lerp(previous.getPosition(), next.getPosition(), ratio),
                        Vectors.lerp(previous.getRotation(), next.getRotation(), ratio),
                        Vectors.lerp(previous.getScale(), next.getScale(), ratio)
                );
            }

            @Override
            public KeyFrame next() {

                if (tick > nextPos) {
                    // current tick is greater than the next position, set
                    // previous to 'next', check if previous is current, then
                    // return previous
                    previous = next;
                    previousPos = nextPos;

                    consumeNextKeyFrame();
                    tick++;
                }

                if (tick == previousPos) {
                    // current is previous, update tick
                    // to fix this for next 'next()' call
                    KeyFrame frame = previous;
                    tick++;
                    return frame;
                }

                // invariant: previous < tick < next
                // this computes intermediary keyframes
                KeyFrame frame = lerp();
                tick++;
                return frame;
            }
        };
    }

}
