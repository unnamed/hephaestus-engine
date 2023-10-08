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

    private static void assertRotationAroundY(
            Vector3Float expected,
            Vector3Float vector,
            double angle
    ) {
        Vector3Float rotated = Vectors.rotateAroundYRadians(vector, angle);
        assertTrue(
                Vectors.equals(expected, rotated, 0.000001D),
                "Vectors should be equal, expected:  " + expected + " got: " + rotated
        );
    }

    @Test
    void test_rotation_around_y() {
        assertRotationAroundY(
                new Vector3Float(0, 0, 1),
                new Vector3Float(1, 0, 0),
                Math.PI / 2
        );
        assertRotationAroundY(
                new Vector3Float(0, 0, -1),
                new Vector3Float(1, 0, 0),
                -Math.PI / 2
        );
        assertRotationAroundY(
                new Vector3Float(1, 0, 0),
                new Vector3Float(1, 0, 0),
                Math.PI * 2
        );
        assertRotationAroundY(
                new Vector3Float(-5, 2, -20),
                new Vector3Float(5, 2, 20),
                Math.PI
        );
        assertRotationAroundY(
                new Vector3Float(-5, 2, -20),
                new Vector3Float(5, 2, 20),
                -Math.PI
        );
    }

}
