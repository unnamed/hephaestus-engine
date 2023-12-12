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

import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.animation.interpolation.Interpolator;
import team.unnamed.hephaestus.animation.interpolation.Interpolators;
import team.unnamed.hephaestus.animation.timeline.playhead.Playhead;
import team.unnamed.hephaestus.animation.timeline.Timeline;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static team.unnamed.hephaestus.util.StructureAssertEquals.assertVectorEquals;

class TimelineTest {

    private static void testTimeline(
            Consumer<Timeline.Builder<Vector3Float>> configurer,
            Vector3Float... expected
    ) {
        Timeline.Builder<Vector3Float> builder = Timeline.<Vector3Float>timeline()
                .initial(new Vector3Float(0, 0, 0))
                .defaultInterpolator(Interpolators.lerpVector3Float());
        configurer.accept(builder);
        Timeline<Vector3Float> timeline = builder.build();
        Playhead<Vector3Float> it = timeline.createPlayhead();
        Vector3Float lastValue = null;
        for (Vector3Float expectedValue : expected) {
            assertVectorEquals(expectedValue, it.next(), 0.001);
            lastValue = expectedValue;
        }

        if (lastValue != null) {
            for (int i = 0; i < 10; i++) {
                // should be kept on last value
                assertVectorEquals(lastValue, it.next(), 0.001);
            }
        }
    }

    @Test
    void test_simple() {
        testTimeline(
                timeline -> {
                    timeline.keyFrame(0, new Vector3Float(0, 0, 0));
                    timeline.keyFrame(10, new Vector3Float(20, 20, 20));
                },
                new Vector3Float(0, 0, 0), // keyframe
                new Vector3Float(2, 2, 2),
                new Vector3Float(4, 4, 4),
                new Vector3Float(6, 6, 6),
                new Vector3Float(8, 8, 8),
                new Vector3Float(10, 10, 10),
                new Vector3Float(12, 12, 12),
                new Vector3Float(14, 14, 14),
                new Vector3Float(16, 16, 16),
                new Vector3Float(18, 18, 18),
                new Vector3Float(20, 20, 20) // keyframe
        );
    }

    @Test
    void test_simple_2() {
        testTimeline(
                timeline -> {
                    timeline.keyFrame(0, new Vector3Float(0, 0, 0));
                    timeline.keyFrame(8, new Vector3Float(4, 4, 4));
                },
                new Vector3Float(0, 0, 0), // keyframe
                new Vector3Float(0.5F, 0.5F, 0.5F),
                new Vector3Float(1, 1, 1),
                new Vector3Float(1.5F, 1.5F, 1.5F),
                new Vector3Float(2, 2, 2),
                new Vector3Float(2.5F, 2.5F, 2.5F),
                new Vector3Float(3, 3, 3),
                new Vector3Float(3.5F, 3.5F, 3.5F),
                new Vector3Float(4, 4, 4) // keyframe
        );
    }

    @Test
    void test_different() {
        testTimeline(
                timeline -> {
                    timeline.keyFrame(0, new Vector3Float(0, 0, 0));
                    timeline.keyFrame(8, new Vector3Float(4, 2, 0));
                },
                new Vector3Float(0, 0, 0), // keyframe
                new Vector3Float(0.5F, 0.25F, 0),
                new Vector3Float(1, 0.5F, 0),
                new Vector3Float(1.5F, 0.75F, 0),
                new Vector3Float(2, 1, 0),
                new Vector3Float(2.5F, 1.25F, 0),
                new Vector3Float(3, 1.5F, 0),
                new Vector3Float(3.5F, 1.75F, 0),
                new Vector3Float(4, 2, 0) // keyframe
        );
    }

    @Test
    void test_multiple_keyframes() {
        testTimeline(
                timeline -> {
                    timeline.keyFrame(0, new Vector3Float(0, 0, 0));
                    timeline.keyFrame(2, new Vector3Float(4, 2, 0));
                    timeline.keyFrame(6, new Vector3Float(0, 4, 1));
                },
                new Vector3Float(0, 0, 0), // keyframe
                new Vector3Float(2, 1, 0),
                new Vector3Float(4, 2, 0), // keyframe
                new Vector3Float(3, 2.5F, 0.25F),
                new Vector3Float(2, 3, 0.5F),
                new Vector3Float(1, 3.5F, 0.75F),
                new Vector3Float(0, 4, 1) // keyframe
        );
    }

    @Test
    void test_multiple_keyframes_2() {
        testTimeline(
                timeline -> {
                    timeline.keyFrame(0, new Vector3Float(0, 0, 0)); // "normal speed"
                    timeline.keyFrame(4, new Vector3Float(4, 4, 4)); // "normal speed"
                    timeline.keyFrame(8, new Vector3Float(0, 0, 0)); // "normal speed"
                    timeline.keyFrame(12, new Vector3Float(8, 8, 8)); // "double speed"
                    timeline.keyFrame(16, new Vector3Float(6, 6, 6)); // "half speed"
                },
                new Vector3Float(0, 0, 0), // keyframe
                new Vector3Float(1, 1, 1),
                new Vector3Float(2, 2, 2),
                new Vector3Float(3, 3, 3),
                new Vector3Float(4, 4, 4), // keyframe
                new Vector3Float(3, 3, 3),
                new Vector3Float(2, 2, 2),
                new Vector3Float(1, 1, 1),
                new Vector3Float(0, 0, 0), // keyframe
                new Vector3Float(2, 2, 2),
                new Vector3Float(4, 4, 4),
                new Vector3Float(6, 6, 6),
                new Vector3Float(8, 8, 8), // keyframe
                new Vector3Float(7.5F, 7.5F, 7.5F),
                new Vector3Float(7, 7, 7),
                new Vector3Float(6.5F, 6.5F, 6.5F),
                new Vector3Float(6, 6, 6) // keyframe
        );
    }

    @Test
    void test_catmullrom_interpolated_timeline() {
        testTimeline(
                timeline -> {
                    timeline.defaultInterpolator(Interpolators.catmullRomSplineVector3Float());
                    timeline.keyFrame(0, new Vector3Float(0, 0, 0));
                    timeline.keyFrame(4, new Vector3Float(-4, -4, -4));
                    timeline.keyFrame(8, new Vector3Float(4, 4, 4));
                    timeline.keyFrame(12, new Vector3Float(0, 0, 0));
                    timeline.keyFrame(16, new Vector3Float(-4, -4, -4));
                },
                new Vector3Float(0, 0, 0), // keyframe
                new Vector3Float(-1, -1, -1),
                new Vector3Float(-2.5F, -2.5F, -2.5F),
                new Vector3Float(-3.75F, -3.75F, -3.75F),
                new Vector3Float(-4, -4, -4), // keyframe
                new Vector3Float(-2.5625F, -2.5625F, -2.5625F),
                new Vector3Float(0, 0, 0),
                new Vector3Float(2.5625F, 2.5625F, 2.5625F),
                new Vector3Float(4, 4, 4), // keyframe
                new Vector3Float(3.84375F, 3.84375F, 3.84375F),
                new Vector3Float(2.75F, 2.75F, 2.75F),
                new Vector3Float(1.28125F, 1.28125F, 1.28125F),
                new Vector3Float(0, 0, 0), // keyframe
                new Vector3Float(-1.09375F, -1.09375F, -1.09375F),
                new Vector3Float(-2.25F, -2.25F, -2.25F),
                new Vector3Float(-3.28125F, -3.28125F, -3.28125F),
                new Vector3Float(-4, -4, -4) // keyframe
        );
    }

    @Test
    public void test_combine_lerp_and_smooth() {
        testTimeline(
                timeline -> {
                    // linear + smooth = smooth
                    timeline.keyFrame(0, new Vector3Float(0, 0, 0), Interpolators.lerpVector3Float());
                    timeline.keyFrame(4, new Vector3Float(20, 20, 20), Interpolators.catmullRomSplineVector3Float());
                },
                new Vector3Float(0, 0, 0),
                new Vector3Float(4.063F, 4.063F, 4.063F),
                new Vector3Float(10, 10, 10),
                new Vector3Float(15.938F, 15.938F, 15.938F),
                new Vector3Float(20, 20, 20)
        );
    }

    @Test
    public void test_combine_step_and_any() {
        List<Interpolator<Vector3Float>> interpolators = Arrays.asList(
                Interpolators.stepVector3Float(),
                Interpolators.lerpVector3Float(),
                Interpolators.catmullRomSplineVector3Float(),
                Interpolators.always(new Vector3Float(84, 84, 84))
        );
        for (Interpolator<Vector3Float> interpolator : interpolators) {
            testTimeline(
                    timeline -> {
                        // step + any = step
                        timeline.keyFrame(0, new Vector3Float(0, 0, 0), Interpolators.stepVector3Float());
                        timeline.keyFrame(4, new Vector3Float(20, 20, 20), interpolator);
                    },
                    new Vector3Float(0, 0, 0),
                    new Vector3Float(0, 0, 0),
                    new Vector3Float(0, 0, 0),
                    new Vector3Float(0, 0, 0),
                    new Vector3Float(20, 20, 20)
            );
        }
    }

    @Test
    public void test_combine_smooth_and_any() {
        List<Interpolator<Vector3Float>> interpolators = Arrays.asList(
                Interpolators.stepVector3Float(),
                Interpolators.lerpVector3Float(),
                Interpolators.catmullRomSplineVector3Float(),
                Interpolators.always(new Vector3Float(84, 84, 84))
        );
        for (Interpolator<Vector3Float> interpolator : interpolators) {
            testTimeline(
                    timeline -> {
                        // smooth + any = smooth
                        timeline.keyFrame(0, new Vector3Float(0, 0, 0), Interpolators.catmullRomSplineVector3Float());
                        timeline.keyFrame(4, new Vector3Float(20, 20, 20), interpolator);
                    },
                    new Vector3Float(0, 0, 0),
                    new Vector3Float(4.063F, 4.063F, 4.063F),
                    new Vector3Float(10, 10, 10),
                    new Vector3Float(15.938F, 15.938F, 15.938F),
                    new Vector3Float(20, 20, 20)
            );
        }
    }

    @Test
    public void test_keyframes_with_same_time_are_removed() {
        testTimeline(
                timeline -> {
                    timeline.keyFrame(0, new Vector3Float(0, 0, 0));
                    timeline.keyFrame(0, new Vector3Float(20, 20, 20));
                },
                new Vector3Float(0, 0, 0)
        );

        testTimeline(
                timeline -> {
                    timeline.keyFrame(0, new Vector3Float(0, 0, 0));
                    timeline.keyFrame(1, new Vector3Float(10, 10, 10));
                    timeline.keyFrame(1, new Vector3Float(20, 20, 20));
                    timeline.keyFrame(1, new Vector3Float(30, 30, 30));
                    timeline.keyFrame(2, new Vector3Float(40, 40, 40));
                },
                new Vector3Float(0, 0, 0),
                new Vector3Float(10, 10, 10),
                new Vector3Float(40, 40, 40)
        );
    }

}
