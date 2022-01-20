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
package team.unnamed.hephaestus.util;

import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.struct.Vector3Double;

/**
 * Utility class for working with
 * vectors
 */
public final class Vectors {

    private Vectors() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static Vector3Double toRadians(Vector3Float vector) {
        return new Vector3Double(
                Math.toRadians(vector.x()),
                Math.toRadians(vector.y()),
                Math.toRadians(vector.z())
        );
    }

    public static Vector3Float rotateAroundY(Vector3Float vector, double angle) {
        double sin =  Math.sin(angle);
        double cos = Math.cos(angle);

        return new Vector3Float(
                (float) (vector.x() * cos - vector.z() * sin),
                vector.y(),
                (float) (vector.x() * sin + vector.z() * cos)
        );
    }

    public static Vector3Float rotate(Vector3Float vector, Vector3Double rotation) {

        double cosX = Math.cos(rotation.getX()), sinX = Math.sin(rotation.getX());
        double cosY = Math.cos(rotation.getY()), sinY = Math.sin(rotation.getY());
        double cosZ = Math.cos(rotation.getZ()), sinZ = Math.sin(rotation.getZ());

        double x = vector.x();
        double y = vector.y();
        double z = vector.z();

        // rotate around X axis
        double xy = y * cosX - z * sinX;
        double xz = y * sinX + z * cosX;

        // rotate around Y axis
        double yx = x * cosY - xz * sinY;
        double yz = x * sinY + xz * cosY;

        // rotate around Z axis
        double zx = yx * cosZ + xy * sinZ;
        double zy = -yx * sinZ + xy * cosZ;

        return new Vector3Float((float) zx, (float) zy, (float) yz);
    }

    public static Vector3Float lerp(Vector3Float start, Vector3Float end, float percent) {
        return start.add(end.subtract(start).multiply(percent));
    }
}