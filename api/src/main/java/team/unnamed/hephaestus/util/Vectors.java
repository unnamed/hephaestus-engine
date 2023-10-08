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

import team.unnamed.creative.base.Vector3Float;

/**
 * Utility class for working with
 * vectors
 */
public final class Vectors {

    private static final float DEGREE = 0.017453292519943295F;
    private static final float RADIAN = 57.29577951308232F;

    private Vectors() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static Vector3Float toRadians(Vector3Float vector) {
        return vector.multiply(DEGREE);
    }

    public static Vector3Float toDegrees(Vector3Float vector) {
        return vector.multiply(RADIAN);
    }

    /**
     * Rotates the given vector around the Y axis by the given angle
     * in radians, <b>counter-clockwise</b>.
     *
     * @param vector The vector to rotate
     * @param angle The angle in radians
     * @return The rotated vector
     */
    public static Vector3Float rotateAroundYRadians(Vector3Float vector, double angle) {
        double sin =  Math.sin(angle);
        double cos = Math.cos(angle);

        return new Vector3Float(
                (float) (vector.x() * cos - vector.z() * sin),
                vector.y(),
                (float) (vector.x() * sin + vector.z() * cos)
        );
    }

    /**
     * Rotates the given vector by the given 3D rotation vector
     * in degrees, <b>counter-clockwise</b>, <b>XYZ order</b>.
     *
     * @param vector The vector to rotate
     * @param rotation The rotation vector in degrees
     * @return The rotated vector
     */
    public static Vector3Float rotateDegrees(Vector3Float vector, Vector3Float rotation) {
        rotation = toRadians(rotation);

        double x = vector.x();
        double y = vector.y();
        double z = vector.z();

        // rotate around X axis
        // [ 1   0     0   ]
        // [ 0  cosX -sinX ]
        // [ 0  sinX  cosX ]
        // y = y cosx − z sinx
        // z = y sinx + z cosx
        double rx = rotation.x();
        double sinX = Math.sin(rx);
        double cosX = Math.cos(rx);
        double xy = y * cosX - z * sinX;
        double xz = y * sinX + z * cosX;

        // rotate around Y axis
        // [  cosY  0  sinY ]
        // [   0    1   0   ]
        // [ -sinY  0  cosY ]
        // x = x cosy + z siny
        // z = −x siny + z cosy
        double ry = rotation.y();
        double sinY = Math.sin(ry);
        double cosY = Math.cos(ry);
        double yx = x * cosY + xz * sinY;
        double yz = -x * sinY + xz * cosY;

        // rotate around Z axis
        // [ cosZ -sinZ  0 ]
        // [ sinZ  cosZ  0 ]
        // [  0     0    1 ]
        // x = x cosz − y sinz
        // y = x sinz + y cosz
        double rz = rotation.z();
        double sinZ = Math.sin(rz);
        double cosZ = Math.cos(rz);
        double zx = yx * cosZ - xy * sinZ;
        double zy = yx * sinZ + xy * cosZ;

        return new Vector3Float((float) zx, (float) zy, (float) yz);
    }

    public static Vector3Float lerp(Vector3Float start, Vector3Float end, float percent) {
        return start.add(end.subtract(start).multiply(percent));
    }

    public static boolean equals(Vector3Float a, Vector3Float b, double epsilon) {
        return Math.abs(a.x() - b.x()) < epsilon
                && Math.abs(a.y() - b.y()) < epsilon
                && Math.abs(a.z() - b.z()) < epsilon;
    }

}