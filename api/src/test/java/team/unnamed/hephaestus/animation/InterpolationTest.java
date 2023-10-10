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
import team.unnamed.hephaestus.animation.interpolation.Interpolator;
import team.unnamed.hephaestus.util.Quaternion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpolationTest {

    @Test
    void test_linear_vector_interpolation() {
        Interpolation<Vector3Float> interpolation = Interpolator.lerpVector3Float()
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
    void test_spherical_quaternion_interpolation() {
        Interpolation<Quaternion> interpolation = Interpolator.slerpQuaternion().interpolation(
                new Quaternion(0, 0, 0, 1),        //  0 deg
                new Quaternion(0, 0.707, 0, 0.707) // 90 deg (Y only)
        );

        assertEquals(new Quaternion(0, 0, 0, 1), interpolation.interpolate(0));
        assertTrue(interpolation.interpolate(0.25).equals(new Quaternion(0, 0.195, 0, 0.981), 0.001));
        assertTrue(interpolation.interpolate(0.5).equals(new Quaternion(0, 0.383, 0, 0.924), 0.001));
        assertTrue(interpolation.interpolate(0.75).equals(new Quaternion(0, 0.556, 0, 0.831), 0.001));
        assertEquals(new Quaternion(0, 0.707, 0, 0.707), interpolation.interpolate(1));
    }

    @Test
    void test_spherical_quaternion_interpolation_in_multiple_axis() {
        Interpolation<Quaternion> interpolation = Interpolator.slerpQuaternion().interpolation(
                new Quaternion(0, 0, 0, 1),        //  0
                new Quaternion(0.5, 0.5, 0.5, 0.5) // [ 90, 90, 0 ]
        );

        assertEquals(new Quaternion(0, 0, 0, 1), interpolation.interpolate(0));
        assertTrue(interpolation.interpolate(0.25).equals(new Quaternion(0.149, 0.149, 0.149, 0.965), 0.001));
        assertTrue(interpolation.interpolate(0.5).equals(new Quaternion(0.289, 0.289, 0.289, 0.866), 0.001));
        assertTrue(interpolation.interpolate(0.75).equals(new Quaternion(0.408, 0.408, 0.408, 0.707), 0.001));
        assertEquals(new Quaternion(0.5, 0.5, 0.5, 0.5), interpolation.interpolate(1));
    }

    @Test
    void test() {
        Interpolation<Vector3Float> interpolation = Interpolator.catmullRomSplineVector3Float().interpolation(
                null,
                new Vector3Float(72, 72, 72),
                new Vector3Float(64, 64, 64),
                new Vector3Float(80, 80, 80)
        );

        double step = 0.01;
        double t = 0;
        while (t <= 1) {
            System.out.println("(" + (t * 20-40) + ", " + interpolation.interpolate(t).y() + ")");
            t+= step;
        }
    }

}
