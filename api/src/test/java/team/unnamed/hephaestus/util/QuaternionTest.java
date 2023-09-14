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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.Vector3Float;

public class QuaternionTest {

    private static final double THRESHOLD = 0.00001D;

    @Test
    @DisplayName("Test Euler Angle to Quaternion conversion")
    public void test_quaternion_from_euler() {
        assertQuaternionFromEuler(0, 0, 0, 1.0, Vector3Float.ZERO);
        assertQuaternionFromEuler(0.0, 0.7071067966408575, 0.0, 0.7071067657322372, new Vector3Float(0.0F, 90.0F, 0.0F));
        assertQuaternionFromEuler(-0.2705980631261674, 0.6532814937590546, 0.27059805129795017, 0.6532814652032131, new Vector3Float(0.0F, 90.0F, 45.0F));
        assertQuaternionFromEuler(0.3535533983204287, 0.3535533983204287, -0.14644661713388138, 0.8535533828661185, new Vector3Float(45.0F, 45.0F, 0.0F));
        assertQuaternionFromEuler(0.7071067657322365, -3.0908620960936135E-8, -0.7071067966408568, -3.090861960987738E-8, new Vector3Float(180.0F, 90.0F, 0.0F));
        assertQuaternionFromEuler(-0.41449777958570483, 0.17681186457523382, 0.8888933387630448, 0.0824486965733013, new Vector3Float(22.5F, 50.0F, 180.0F));
        assertQuaternionFromEuler(1.5180805997096662E-8, -0.1736481726667787, -8.609463161462606E-8, -0.9848077538938659, new Vector3Float(0.0F, 20.0F, 360.0F));
        assertQuaternionFromEuler(-0.0017435019962324843, 0.0872458164291457, 0.06911100618850595, 0.9937850856900667, new Vector3Float(0.5F, 10.0F, 8.0F));
    }

    private static void assertQuaternionFromEuler(double x, double y, double z, double w, Vector3Float euler) {
        Quaternion expected = new Quaternion(x, y, z, w);
        Quaternion quaternion = Quaternion.fromEulerDegrees(euler);
        Assertions.assertTrue(
                expected.equals(quaternion, THRESHOLD),
                "Unexpected quaternion values for EulerAngle: " + euler
        );
    }

    @Test
    @DisplayName("Test Quaternion to Euler Angle conversion")
    public void test_quaternion_to_euler() {
        assertQuaternionToEuler(0, 0, 0, 1.0, Vector3Float.ZERO);
        assertQuaternionToEuler(0.0, 0.7071067966408575, 0.0, 0.7071067657322372, new Vector3Float(0.0F, 90.0F, 0.0F));
        assertQuaternionToEuler(-0.2705980631261674, 0.6532814937590546, 0.27059805129795017, 0.6532814652032131, new Vector3Float(0.0F, 90.0F, 45.0F));
        assertQuaternionToEuler(0.3535533983204287, 0.3535533983204287, -0.14644661713388138, 0.8535533828661185, new Vector3Float(45.0F, 45.0F, 0.0F));
        assertQuaternionToEuler(0.7071067657322365, -3.0908620960936135E-8, -0.7071067966408568, -3.090861960987738E-8, new Vector3Float(180.0F, 90.0F, 0.0F));
        assertQuaternionToEuler(-0.41449777958570483, 0.17681186457523382, 0.8888933387630448, 0.0824486965733013, new Vector3Float(22.5F, 50.0F, 180.0F));
        assertQuaternionToEuler(1.5180805997096662E-8, -0.1736481726667787, -8.609463161462606E-8, -0.9848077538938659, new Vector3Float(0.0F, 20.0F, 360.0F));
        assertQuaternionToEuler(-0.0017435019962324843, 0.0872458164291457, 0.06911100618850595, 0.9937850856900667, new Vector3Float(0.5F, 10.0F, 8.0F));
    }

    private static void assertQuaternionToEuler(double x, double y, double z, double w, Vector3Float expected) {
        // convert quaternion to euler
        Quaternion original = new Quaternion(x, y, z, w);
        Vector3Float got = original.toEulerDegrees(); // <--- the tested function

        // convert euler to quaternion (so we forget about equivalent Euler Angles)
        Quaternion quaternion = Quaternion.fromEulerDegrees(got); // <---- from the tested function
        Quaternion expectation = Quaternion.fromEulerDegrees(expected); // <---- expectation

        Assertions.assertTrue(
                expectation.isEquivalentTo(quaternion, THRESHOLD),
                "Quaternion (" + x + ", " + y + ", " + z + ", " + w + ")\n"
                + "   converted to Euler (" + got.x() + ", " + got.y() + ", " + got.z() + ")\n"
                + "   and re-converted to " + quaternion + "\nis not equivalent to expected Euler"
                + " (" + expected.x() + ", " + expected.y() + ", " + expected.z() + "),\n    which is"
                + " also equivalent to " + expectation
        );
    }

}