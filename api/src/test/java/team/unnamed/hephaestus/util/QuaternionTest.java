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
package team.unnamed.hephaestus.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.Vector3Float;

public class QuaternionTest {

    private static final double THRESHOLD = 0.01D;

    @Test
    @DisplayName("Test Euler Angle to Quaternion conversion")
    public void test_quaternion_euler_angle_conversion() {
        assertQuaternionEqualsEuler(0.0000000, 0.0000000, 0.0000000, 1.0000000, Vector3Float.ZERO);
        assertQuaternionEqualsEuler(0.0000000, 0.7071060, 0.0000000, 0.7071060, new Vector3Float(0.0F, 90.0F, 0.0F));
        assertQuaternionEqualsEuler(-0.270598, 0.6532810, 0.2705980, 0.6532810, new Vector3Float(0.0F, 90.0F, 45.0F));
        assertQuaternionEqualsEuler(-0.353553, 0.3535530, 0.1464460, 0.8535530, new Vector3Float(45.0F, 45.0F, 0.0F));
        assertQuaternionEqualsEuler(-0.707106, 0.0000000, 0.7071060, 0.0000000, new Vector3Float(180.0F, 90.0F, 0.0F));
        assertQuaternionEqualsEuler(-0.414497, -0.176811, 0.8888930, -0.082448, new Vector3Float(22.5F, 50.0F, 180.0F));
        assertQuaternionEqualsEuler(0.0000000, -0.173648, 0.0000000, -0.984807, new Vector3Float(0.0F, 20.0F, 360.0F));
        assertQuaternionEqualsEuler(-0.010415, 0.0866390, 0.0698690, 0.9937320, new Vector3Float(0.5F, 10.0F, 8.0F));
    }

    private static void assertQuaternionEqualsEuler(double x, double y, double z, double w, Vector3Float euler) {
        Quaternion expected = new Quaternion(x, y, z, w);
        Quaternion quaternion = Quaternion.fromEulerDegrees(euler);
        StructureAssertEquals.assertQuaternionEquals(expected, quaternion, THRESHOLD);

        // convert quaternion to euler and test too
        Quaternion reconverted = Quaternion.fromEulerDegrees(quaternion.toEulerDegrees());
        StructureAssertEquals.assertQuaternionEquivalent(expected, reconverted, THRESHOLD);
    }

}