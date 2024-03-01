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

import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.Vector3Float;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VectorRotationTest {
    private static void assertRotation(
            Vector3Float expected,
            Vector3Float vector,
            Vector3Float rotation
    ) {
        Vector3Float rotated = Vectors.rotateDegrees(vector, rotation);
        assertTrue(
                Vectors.equals(expected, rotated, 0.000001D),
                "Vectors should be equal, expected:  " + expected + " got: " + rotated
        );
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

}
