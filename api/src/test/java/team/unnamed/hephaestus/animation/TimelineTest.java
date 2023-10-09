/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.Vector3Float;

import java.util.Iterator;

public class TimelineTest {

    private static final int DEPTH = 100;

    @Test
    @DisplayName("Test that dynamic timeline keyframe linear interpolation works")
    public void test_linear_interpolation() {
        Timeline timeline = Timeline.dynamic(DEPTH);

        timeline.put(0, Timeline.Channel.POSITION, Vector3Float.ZERO);
        timeline.put(DEPTH, Timeline.Channel.POSITION, Vector3Float.ONE);

        Iterator<KeyFrame> iterator = timeline.iterator();

        for (int i = 0; i <= DEPTH; i++) {
            float expected = (float) i / DEPTH;
            KeyFrame frame = iterator.next();
            Assertions.assertEquals(
                    new Vector3Float(expected, expected, expected),
                    frame.position()
            );
        }
    }

}
