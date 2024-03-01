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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuaternionTest {

    private static final double THRESHOLD = 0.01D;

    @Test
    @DisplayName("Test Euler Angle to Quaternion conversion")
    public void test_quaternion_euler_angle_conversion() {
        assertQuaternionEqualsEuler(0.0000000, 0.0000000, 0.0000000, 1.0000000, Vector3Float.ZERO);

        // 90-degrees in a single axis
        assertQuaternionEqualsEuler(0.7071060, 0.0000000, 0.0000000, 0.7071060, new Vector3Float(90.0F, 0.0F, 0.0F));
        assertQuaternionEqualsEuler(0.0000000, 0.7071060, 0.0000000, 0.7071060, new Vector3Float(0.0F, 90.0F, 0.0F));
        assertQuaternionEqualsEuler(0.0000000, 0.0000000, 0.7071060, 0.7071060, new Vector3Float(00.0F, 0.0F, 90.0F));

        // multiple axis
        assertQuaternionEqualsEuler(-0.270598, 0.6532810, 0.2705980, 0.6532810, new Vector3Float(0.0F, 90.0F, 45.0F));
        assertQuaternionEqualsEuler(0.3535530, 0.3535530, -0.146446, 0.8535530, new Vector3Float(45.0F, 45.0F, 0.0F));
        assertQuaternionEqualsEuler(0.7071060, 0.0000000, -0.707106, 0.0000000, new Vector3Float(180.0F, 90.0F, 0.0F));
        assertQuaternionEqualsEuler(-0.414497, 0.1768110, 0.8888930, 0.0824480, new Vector3Float(22.5F, 50.0F, 180.0F));
        assertQuaternionEqualsEuler(0.0000000, -0.173648, 0.0000000, -0.984807, new Vector3Float(0.0F, 20.0F, 360.0F));
        assertQuaternionEqualsEuler(-0.001743, 0.0872458, 0.0691110, 0.9937850, new Vector3Float(0.5F, 10.0F, 8.0F));
    }

    @Test
    void test_rotation_in_multiple_axis() {
        // in single axis (X)
        assertRotation(
                new Vector3Float(0, -1, 0),
                new Vector3Float(0, 0, 1),
                new Vector3Float(90, 0, 0)
        );
        // in single axis (Y)
        assertRotation(
                new Vector3Float(0, 0, -1),
                new Vector3Float(1, 0, 0),
                new Vector3Float(0, 90, 0)
        );
        // in single axis (Z)
        assertRotation(
                new Vector3Float(-1, 0, 0),
                new Vector3Float(0, 1, 0),
                new Vector3Float(0, 0, 90)
        );

        // in multiple axis (X,Y)
        assertRotation(
                new Vector3Float(1, 0, 0),
                new Vector3Float(0, 1, 0),
                new Vector3Float(90, 90, 0)
        );
        // in multiple axis (X,Z)
        assertRotation(
                new Vector3Float(0, 0, 1),
                new Vector3Float(0, 1, 0),
                new Vector3Float(90, 0, 90)
        );
        // in multiple axis (Y,Z)
        assertRotation(
                new Vector3Float(0, 1, 0),
                new Vector3Float(0, 0, 1),
                new Vector3Float(0, 90, 90)
        );
        // in multiple axis (X,Y,Z)
        assertRotation(
                new Vector3Float(0, 0, -1),
                new Vector3Float(1, 0, 0),
                new Vector3Float(90, 90, 90)
        );
        assertRotation(
                new Vector3Float(0.5F, 0.5F, -0.70710677F),
                new Vector3Float(1, 0, 0),
                new Vector3Float(45, 45, 45)
        );
    }

    private static void assertQuaternionEqualsEuler(double x, double y, double z, double w, Vector3Float euler) {
        Quaternion expected = new Quaternion(x, y, z, w);
        Quaternion quaternion = Quaternion.fromEulerDegrees(euler);
        StructureAssertEquals.assertQuaternionEquals(expected, quaternion, THRESHOLD);

        // convert quaternion to euler and test too
        Quaternion reconverted = Quaternion.fromEulerDegrees(quaternion.toEulerDegrees());
        StructureAssertEquals.assertQuaternionEquivalent(expected, reconverted, THRESHOLD);
    }

    private static void assertRotation(
            Vector3Float expected,
            Vector3Float vector,
            Vector3Float rotation
    ) {
        // Test using Quaternions too
        final var quaternion = Quaternion.fromEulerDegrees(rotation);
        final var rotatedWithQuaternion = quaternion.transform(vector);
        assertTrue(
                Vectors.equals(expected, rotatedWithQuaternion, 0.000001D),
                "Vectors should be equal, expected:  " + expected + " got: " + rotatedWithQuaternion
        );
    }
}