/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.hephaestus.animation;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.util.Vectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DynamicKeyFrameList implements KeyFrameList {

    private static final int CHANNEL_COUNT = Channel.values().length;

    @SuppressWarnings("unchecked")
    private final List<AnimationEntry>[] entries = new List[CHANNEL_COUNT];

    @Override
    public void put(int position, Channel channel, Vector3Float value) {
        int index = channel.ordinal();
        List<AnimationEntry> list = entries[index];
        if (list == null) {
            list = entries[index] = new ArrayList<>();
        }
        list.add(new AnimationEntry(position, value));
    }

    private static final class AnimationEntry {

        private final int pos;
        private final Vector3Float value;

        public AnimationEntry(
                int pos,
                Vector3Float value
        ) {
            this.pos = pos;
            this.value = value;
        }

    }

    @Override
    public @NotNull Iterator<KeyFrame> iterator() {
        @SuppressWarnings("unchecked")
        Iterator<AnimationEntry>[] iterators = new Iterator[CHANNEL_COUNT];
        for (Channel channel : Channel.values()) {
            int index = channel.ordinal();
            List<AnimationEntry> entryList = entries[index];
            if (entryList == null) {
                entryList = Collections.emptyList();
            }
            iterators[index] = entryList.iterator();
        }
        return new DynamicKeyFrameIterator(iterators);
    }

    private static class DynamicKeyFrameIterator
            implements Iterator<KeyFrame> {

        /**
         * Underlying iterator for {@link AnimationEntry}, they
         * specify channel, position and value
         *
         * <strong>They must be ordered!</strong>
         */
        private final Iterator<AnimationEntry>[] iterators;

        /**
         * Represents the current tick of the iteration,
         * it increments in 1 in every next() call when
         * using the default implementation
         */
        private int tick = 0;

        private final int[] previousPositions = new int[CHANNEL_COUNT];
        private final Vector3Float[] previousValues = new Vector3Float[CHANNEL_COUNT];

        private final int[] nextPositions = new int[CHANNEL_COUNT];
        private final Vector3Float[] nextValues = new Vector3Float[CHANNEL_COUNT];

        public DynamicKeyFrameIterator(Iterator<AnimationEntry>[] iterators) {
            this.iterators = iterators;

            // initialize previous and next values
            for (Channel channel : Channel.values()) {
                int index = channel.ordinal();

                previousValues[index] = channel == Channel.SCALE
                        ? Vector3Float.ONE
                        : Vector3Float.ZERO;

                Iterator<AnimationEntry> iterator = iterators[index];

                if (iterator.hasNext()) {
                    AnimationEntry entry = iterator.next();
                    nextPositions[index] = entry.pos;
                    nextValues[index] = entry.value;

                    if (entry.pos == 0F) {
                        // if position is zero, set to previous
                        previousValues[index] = entry.value;
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            // if there are next frames, there must be non-explored
            // entries or currently exploring entries must not be
            // finishing
            for (Channel channel : Channel.values()) {
                int index = channel.ordinal();
                if (tick <= nextPositions[index]
                        || iterators[index].hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public KeyFrame next() {

            Vector3Float position = Vector3Float.ZERO;
            Vector3Float rotation = Vector3Float.ZERO;
            Vector3Float scale = Vector3Float.ONE;

            for (Channel channel : Channel.values()) {
                int index = channel.ordinal();

                Vector3Float next = nextValues[index];
                int nextPos = nextPositions[index];

                Vector3Float previous;
                int previousPos;

                if (next != null && tick > nextPos) {

                    // current tick is greater than next position
                    // in this channel, now 'previous' is 'next'
                    previous = previousValues[index] = next;
                    previousPos = previousPositions[index] = nextPositions[index];

                    // consume 'next'
                    Iterator<AnimationEntry> iterator = iterators[index];
                    if (iterator.hasNext()) {
                        AnimationEntry entry = iterator.next();
                        next = nextValues[index] = entry.value;
                        nextPos = nextPositions[index] = entry.pos;
                    } else {
                        next = nextValues[index] = null;
                    }
                } else {
                    previousPos = previousPositions[index];
                    previous = previousValues[index];
                }

                Vector3Float value;
                if (next == null) {
                    // if there is no next frame to lerp,
                    // use the previous value
                    value = previous;
                } else if (tick == previousPos) {
                    value = previous;
                } else if (tick == nextPos) {
                    value = next;
                } else {
                    float ratio = (float) (tick - previousPos)
                            / (float) (nextPos - previousPos);
                    value = Vectors.lerp(
                            previous,
                            next,
                            ratio
                    );
                }

                switch (channel) {
                    case POSITION: {
                        position = value;
                        break;
                    }
                    case ROTATION: {
                        rotation = value;
                        break;
                    }
                    case SCALE: {
                        scale = value;
                        break;
                    }
                }
            }

            // increment tick
            tick++;

            return new KeyFrame(position, rotation, scale);
        }
    }

}
