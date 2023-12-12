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
import team.unnamed.hephaestus.animation.interpolation.Interpolation;
import team.unnamed.hephaestus.animation.interpolation.Interpolators;
import team.unnamed.hephaestus.animation.timeline.KeyFrame;
import team.unnamed.hephaestus.animation.timeline.KeyFrameBezierAttachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static team.unnamed.hephaestus.util.StructureAssertEquals.assertVectorEquals;

class InterpolationTest {

    @Test
    void test_linear_vector_interpolation() {
        Interpolation<Vector3Float> interpolation = Interpolators.lerpVector3Float()
                .interpolation(
                        new Vector3Float(0, 0, 0),
                        new Vector3Float(10, 10, 10)
                );

        assertEquals(new Vector3Float(0, 0, 0), interpolation.interpolate(0));
        assertEquals(new Vector3Float(1, 1, 1), interpolation.interpolate(0.1));
        assertEquals(new Vector3Float(2.5F, 2.5F, 2.5F), interpolation.interpolate(0.25));
        assertEquals(new Vector3Float(5F, 5F, 5F), interpolation.interpolate(0.5));
        assertEquals(new Vector3Float(7.5F, 7.5F, 7.5F), interpolation.interpolate(0.75));
        assertEquals(new Vector3Float(10, 10, 10), interpolation.interpolate(1));
    }

    @Test
    void test_binary_catmullrom_interpolation() {
        Interpolation<Vector3Float> interpolation = Interpolators.catmullRomSplineVector3Float().interpolation(
                new Vector3Float(0, 0, 0),
                new Vector3Float(20, 20, 20)
        );

        assertVectorEquals(new Vector3Float(0, 0, 0), interpolation.interpolate(0), 0.001); // from
        assertVectorEquals(new Vector3Float(4.063F, 4.063F, 4.063F), interpolation.interpolate(0.25), 0.001);
        assertVectorEquals(new Vector3Float(10, 10, 10), interpolation.interpolate(0.5), 0.001);
        assertVectorEquals(new Vector3Float(15.938F, 15.938F, 15.938F), interpolation.interpolate(0.75), 0.001);
        assertVectorEquals(new Vector3Float(19.296F, 19.296F, 19.296F), interpolation.interpolate(0.94), 0.001);
        assertVectorEquals(new Vector3Float(20, 20, 20), interpolation.interpolate(1), 0.001); // to
    }

    @Test
    void test_ternary_catmullrom_interpolation() {
        Interpolation<Vector3Float> interpolation = Interpolators.catmullRomSplineVector3Float().interpolation(
                null,
                new Vector3Float(4.5F, 4.5F, 4.5F),
                new Vector3Float(4F, 4F, 4F),
                new Vector3Float(5F, 5F, 5F)
        );

        assertVectorEquals(new Vector3Float(4.5F, 4.5F, 4.5F), interpolation.interpolate(0), 0.001); // from
        assertVectorEquals(new Vector3Float(4.473F, 4.473F, 4.473F), interpolation.interpolate(0.0769), 0.001);
        assertVectorEquals(new Vector3Float(4.435F, 4.435F, 4.435F), interpolation.interpolate(0.1538), 0.001);
        assertVectorEquals(new Vector3Float(4.388F, 4.388F, 4.388F), interpolation.interpolate(0.2307), 0.001);
        assertVectorEquals(new Vector3Float(4.333F, 4.333F, 4.333F), interpolation.interpolate(0.3076), 0.001);
        assertVectorEquals(new Vector3Float(4.275F, 4.275F, 4.275F), interpolation.interpolate(0.3846), 0.001);
        assertVectorEquals(new Vector3Float(4.216F, 4.216F, 4.216F), interpolation.interpolate(0.4615), 0.001);
        assertVectorEquals(new Vector3Float(4.159F, 4.159F, 4.159F), interpolation.interpolate(0.5384), 0.001);
        assertVectorEquals(new Vector3Float(4.105F, 4.105F, 4.105F), interpolation.interpolate(0.6153), 0.001);
        assertVectorEquals(new Vector3Float(4.059F, 4.059F, 4.059F), interpolation.interpolate(0.6923), 0.001);
        assertVectorEquals(new Vector3Float(4.023F, 4.023F, 4.023F), interpolation.interpolate(0.7692), 0.001);
        assertVectorEquals(new Vector3Float(3.999F, 3.999F, 3.999F), interpolation.interpolate(0.8461), 0.001);
        assertVectorEquals(new Vector3Float(3.990F, 3.990F, 3.990F), interpolation.interpolate(0.9230), 0.001);
        assertVectorEquals(new Vector3Float(4F, 4F, 4F), interpolation.interpolate(1), 0.001); // to
    }

    @Test
    void test_step_interpolation() {
        Interpolation<Vector3Float> interpolation = Interpolators.stepVector3Float().interpolation(
                new Vector3Float(0, 0, 0),
                new Vector3Float(10, 10, 10)
        );

        assertVectorEquals(new Vector3Float(0, 0, 0), interpolation.interpolate(0), 0.001);
        assertVectorEquals(new Vector3Float(0, 0, 0), interpolation.interpolate(0.1), 0.001);
        assertVectorEquals(new Vector3Float(0, 0, 0), interpolation.interpolate(0.25), 0.001);
        assertVectorEquals(new Vector3Float(0, 0, 0), interpolation.interpolate(0.5), 0.001);
        assertVectorEquals(new Vector3Float(0, 0, 0), interpolation.interpolate(0.75), 0.001);
        assertVectorEquals(new Vector3Float(10, 10, 10), interpolation.interpolate(1), 0.001);
    }

    @Test
    void test_bezier_interpolation() {
        final KeyFrame<Vector3Float> fromKeyFrame = new KeyFrame<>(0, new Vector3Float(0, 0, 0), null);
        fromKeyFrame.attachment(KeyFrameBezierAttachment.class, KeyFrameBezierAttachment.of(
                new Vector3Float(-0.2566666F,-0.703333F,-1.623333F),
                new Vector3Float(-14.909398191235454F, -0.6892359600908713F, -2.595727799247298F),
                new Vector3Float(0.2566666666666667F, 0.7033333333333334F, 1.6233333333333333F),
                new Vector3Float(14.909398191235454F, 0.6892359600908713F, 2.595727799247298F)
        ));

        final KeyFrame<Vector3Float> toKeyFrame = new KeyFrame<>(1, new Vector3Float(10, 10, 10), null);
        toKeyFrame.attachment(KeyFrameBezierAttachment.class, KeyFrameBezierAttachment.of(
                new Vector3Float(-0.2533333333333333F,-0.5566666666666666F,-0.4866666666666668F),
                new Vector3Float(-12.543104192600394F,-0.20536444997940118F,-0.2577319781876972F),
                new Vector3Float(0.2533333F,0.556666F,0.486666F),
                new Vector3Float(12.543104F,0.205364F,0.257731F)
        ));

        final Interpolation<Vector3Float> interpolation = Interpolators.bezierVector3Float(200).interpolation(fromKeyFrame, toKeyFrame);

        assertVectorEquals(new Vector3Float(0, 0, 0), interpolation.interpolate(0), 0.01);
        assertVectorEquals(new Vector3Float(6.23F, 0.77F, 0.87F), interpolation.interpolate(0.25), 0.01);
        assertVectorEquals(new Vector3Float(6.26F, 2.36F, 1.83F), interpolation.interpolate(0.4167), 0.01);
        assertVectorEquals(new Vector3Float(5.89F, 3.86F, 2.53F), interpolation.interpolate(0.5), 0.01);
        assertVectorEquals(new Vector3Float(5.17F, 8.95F, 7.97F), interpolation.interpolate(0.75), 0.01);
        assertVectorEquals(new Vector3Float(7.06F, 9.89F, 9.87F), interpolation.interpolate(0.9167F), 0.01);
        assertVectorEquals(new Vector3Float(10F, 10F, 10F), interpolation.interpolate(1), 0.01);
    }
}
