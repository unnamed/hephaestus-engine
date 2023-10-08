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

    public static Vector3Float rotateAroundYRadians(Vector3Float vector, double angle) {
        double sin =  Math.sin(angle);
        double cos = Math.cos(angle);

        return new Vector3Float(
                (float) (vector.x() * cos - vector.z() * sin),
                vector.y(),
                (float) (vector.x() * sin + vector.z() * cos)
        );
    }

    public static Vector3Float rotateDegrees(Vector3Float vector, Vector3Float rotation) {
        rotation = toRadians(rotation);

        double cosX = Math.cos(rotation.x()), sinX = Math.sin(rotation.x());
        double cosY = Math.cos(rotation.y()), sinY = Math.sin(rotation.y());
        double cosZ = Math.cos(rotation.z()), sinZ = Math.sin(rotation.z());

        double x = vector.x();
        double y = vector.y();
        double z = vector.z();


        // rotate around Z axis
        double zx = x * cosZ + y * sinZ;
        double zy = -x * sinZ + y * cosZ;

        // rotate around Y axis
        double yx = zx * cosY - z * sinY;
        double yz = zx * sinY + z * cosY;

        // rotate around X axis
        double xy = zy * cosX - yz * sinX;
        double xz = zy * sinX + yz * cosX;

        return new Vector3Float((float) yx, (float) xy, (float) xz);
    }

    public static Vector3Float lerp(Vector3Float start, Vector3Float end, float percent) {
        return start.add(end.subtract(start).multiply(percent));
    }

}