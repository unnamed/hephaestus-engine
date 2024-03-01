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

import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Immutable implementation of Quaternions, a mathematical
 * number system that extends the complex numbers
 *
 * <p>Quaternions are particularly applied for calculations
 * involving three-dimensional rotations, can be used with,
 * and as alternative of, euler angles and rotation matrices</p>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Quaternion">Quaternion on Wikipedia</a>
 * @since 1.0.0
 */
public final class Quaternion implements Examinable {

    /**
     * An identity quaternion constant. An identity quaternion is a quaternion
     * that doesn't change any quaternion it is multiplied with. An identity
     * quaternion is thus a rotation of nothing.
     */
    public static final Quaternion IDENTITY = new Quaternion(0, 0, 0, 1);

    private final double x;
    private final double y;
    private final double z;
    private final double w;

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Returns the real part of the first component
     * of this quaternion
     *
     * @return The first component real part
     * @since 1.0.0
     */
    public double x() {
        return x;
    }

    /**
     * Returns the real part of the second component
     * of this quaternion
     *
     * @return The second component real part
     * @since 1.0.0
     */
    public double y() {
        return y;
    }

    /**
     * Returns the real part of the third component
     * of this quaternion
     *
     * @return The third component real part
     * @since 1.0.0
     */
    public double z() {
        return z;
    }

    /**
     * Returns the fourth component of this quaternion
     *
     * @return The fourth component
     * @since 1.0.0
     */
    public double w() {
        return w;
    }

    /**
     * Converts this quaternion to a {@code double} array
     * with fixed-size of 4, containing all the real parts
     * of the quaternion, in order
     *
     * @return The created array representing this quaternion
     * @since 1.0.0
     */
    @Contract("-> new")
    public double[] toArray() {
        return new double[] { x, y, z, w };
    }

    /**
     * Converts this quaternion to a {@code float} array
     * with fixed-size of 4, containing all the real parts
     * of the quaternion, in order, cast to float
     *
     * @return The created array representing this quaternion
     * @since 1.0.0
     */
    @Contract("-> new")
    public float[] toFloatArray() {
        return new float[] { (float) x, (float) y, (float) z, (float) w };
    }

    /**
     * Multiplies this quaternion by the given {@code scalar}
     * number.
     *
     * @param scalar The factor
     * @return The quaternion, with all of its components
     * multiplied by the factor
     * @since 1.0.0
     */
    public Quaternion multiply(double scalar) {
        return new Quaternion(
                x * scalar,
                y * scalar,
                z * scalar,
                w * scalar
        );
    }

    /**
     * Calculates the dot product between {@code this} quaternion
     * and the given {@code other} quaternion.
     *
     * @param other The other quaternion
     * @return The dot product between the two quaternions
     * @since 1.0.0
     */
    public double dot(Quaternion other) {
        return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
    }

    /**
     * Negates {@code this} quaternion by multiplying all
     * of its components by {@code -1}. Equivalent to
     * using {@link Quaternion#multiply(double)} and
     * passing {@code -1}.
     *
     * @return The negated quaternion
     * @since 1.0.0
     */
    public Quaternion negate() {
        return new Quaternion(
                x * -1D,
                y * -1D,
                z * -1D,
                w * -1D
        );
    }

    /**
     * Test if {@code this} quaternion is equivalent to the given
     * {@code other} quaternion.
     *
     * <p>This method checks if the quaternion is approximately
     * equal, component-by-component, to either "other" or
     * "other.negate()"</p>
     *
     * @param other The other quaternion
     * @param threshold The comparison threshold
     * @return True if they are equivalent
     * @since 1.0.0
     */
    public boolean isEquivalentTo(Quaternion other, double threshold) {
        // Two quaternions (a, b) represent the same rotation if:
        //   1.- "a" is component wise approximately equal to "b" OR
        //   2.- "a" is component wise approximately equal to "-b"
        // https://gamedev.stackexchange.com/questions/75072/
        return equals(other, threshold) || equals(other.negate(), threshold);
    }

    public @NotNull Vector3Float transform(final @NotNull Vector3Float vector) {
        final var vx = vector.x();
        final var vy = vector.y();
        final var vz = vector.z();

        final var xx = x * x;
        final var xy = x * y;
        final var xz = x * z;
        final var xw = x * w;
        final var yy = y * y;
        final var yz = y * z;
        final var yw = y * w;
        final var zz = z * z;
        final var zw = z * w;

        // From JOML under the MIT License
        // https://github.com/JOML-CI/JOML/blob/main/src/main/java/org/joml/Quaternionf.java#L1317
        return new Vector3Float(
                (float) Math.fma(Math.fma(-2, yy + zz, 1), vx, Math.fma(2 * (xy - zw), vy, (2 * (xz + yw)) * vz)),
                (float) Math.fma(2 * (xy + zw), vx, Math.fma(Math.fma(-2, xx + zz, 1), vy, (2 * (yz - xw)) * vz)),
                (float) Math.fma(2 * (xz - yw), vx, Math.fma(2 * (yz + xw), vy, Math.fma(-2, xx + yy, 1) * vz))
        );
    }

    /**
     * Multiplies this quaternion rotations with the given
     * {@code other} quaternion rotations
     *
     * <p>This method doesn't modify the current {@code this}
     * quaternion instance, since it's immutable, it returns
     * a new fresh quaternion instance as a result of the
     * operation</p>
     *
     * <p>See 'Multiplication of basis elements' section in
     * <a href="https://en.wikipedia.org/wiki/Quaternion">
     *     Wikipedia</a></p>
     */
    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
                x * other.w + w * other.x + y * other.z - z * other.y,
                y * other.w + w * other.y + z * other.x - x * other.z,
                z * other.w + w * other.z + x * other.y - y * other.x,
                w * other.w - x * other.x - y * other.y - z * other.z
        );
    }

    /**
     * Converts {@code this} quaternion to an Euler Angle representation
     * in radians.
     *
     * @return An Euler Angle representation for this Quaternion, in radians
     * @since 1.0.0
     */
    public Vector3Float toEulerRadians() {
        // originally saw this on WorldSeedEntityEngine's Quaternion implementation
        // https://github.com/AtlasEngineCa/WorldSeedEntityEngine/blob/master/src/main/java/net/worldseed/multipart/Quaternion.java#L53
        // originally from http://marc-b-reynolds.github.io/math/2017/04/18/TaitEuler.html written in C, also originally
        // adapted to Java by iam4722202468

        double t0 = (x + z) * (x - z);     // x^2 - z^2
        double t1 = (w + y) * (w - y);     // w^2 - y^2
        double xx = 0.5 * (t0 + t1);       // 1/2 x of x'
        double xy = x * y + w * z;         // 1/2 y of x'
        double xz = w * y - x * z;         // 1/2 z of x'
        double t  = xx * xx + xy * xy;     // cos(theta)^2
        double yz = 2.0 * (y * z + w * x); // z of y'

        double vz = (float) Math.atan2(xy, xx);           // yaw (psi)
        // todo(yusshu): how about using fast inverse square root?
        double vy = (float) Math.atan(xz / Math.sqrt(t)); // pitch (theta)
        double vx;

        if (t != 0) {
            vx = (float) Math.atan2(yz, t1 - t0);
        } else {
            vx = (float) (2.0 * Math.atan2(x, w) - Math.signum(xz) * vz);
        }

        return new Vector3Float((float) vx, (float) vy, (float) vz);
    }

    /**
     * Converts {@code this} quaternion to an Euler Angle representation
     * in degrees.
     *
     * @return An Euler Angle representation for this Quaternion, in degrees
     * @since 1.0.0
     */
    public Vector3Float toEulerDegrees() {
        return Vectors.toDegrees(toEulerRadians());
    }

    /**
     * Creates a new {@link Quaternion} instance equivalent to the
     * given euler angle (rotation in X, Y, Z), which should be
     * specified in radians.
     *
     * <p>The rotation is calculated in <b>XYZ order</b>, using a
     * <b>Y-Up right-handed</b> coordinate system.</p>
     *
     * @param x The rotation around the X axis
     * @param y The rotation around the Y axis
     * @param z The rotation around the Z axis
     * @return The quaternion representing the euler angle
     * @since 1.0.0
     */
    public static @NotNull Quaternion fromEulerRadians(final double x, final double y, final double z) {
        // common values
        final double halfX = x * 0.5D;
        final double halfY = y * 0.5D;
        final double halfZ = z * 0.5D;

        // compute cos
        final double cosX = Math.cos(halfX);
        final double cosY = Math.cos(halfY);
        final double cosZ = Math.cos(halfZ);

        // compute sin
        final double sinX = Math.sin(halfX);
        final double sinY = Math.sin(halfY);
        final double sinZ = Math.sin(halfZ);

        // common products
        final double sinXCosY = sinX * cosY;
        final double cosXSinY = cosX * sinY;
        final double cosXCosY = cosX * cosY;
        final double sinXSinY = sinX * sinY;

        return new Quaternion(
                sinXCosY * cosZ - cosXSinY * sinZ,
                cosXSinY * cosZ + sinXCosY * sinZ,
                cosXCosY * sinZ - sinXSinY * cosZ,
                cosXCosY * cosZ + sinXSinY * sinZ
        );
    }

    /**
     * Creates a new {@link Quaternion} instance equivalent to the
     * given euler angle (rotation in X, Y, Z), which should be
     * specified in radians.
     *
     * <p>The rotation is calculated in <b>XYZ order</b>, using a
     * <b>Y-Up right-handed</b> coordinate system.</p>
     *
     * @param euler The euler angle to represent
     * @return The quaternion representing the euler angle
     * @since 1.0.0
     */
    @Contract("_ -> new")
    public static @NotNull Quaternion fromEulerRadians(final @NotNull Vector3Float euler) {
        return fromEulerRadians(euler.x(), euler.y(), euler.z());
    }

    /**
     * Creates a new {@link Quaternion} instance equivalent to the
     * given euler angle (rotation in X, Y, Z), which should be
     * specified in degrees
     *
     * @param euler The euler angle to represent
     * @return The quaternion representing the euler angle
     * @since 1.0.0
     */
    public static Quaternion fromEulerDegrees(Vector3Float euler) {
        return fromEulerRadians(Math.toRadians(euler.x()), Math.toRadians(euler.y()), Math.toRadians(euler.z()));
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("x", x),
                ExaminableProperty.of("y", y),
                ExaminableProperty.of("z", z),
                ExaminableProperty.of("w", w)
        );
    }

    /**
     * Tests if {@code this} quaternion is equal to the given
     * {@code other} quaternion, having an error range or
     * threshold (because floating-point operations are not
     * the best)
     *
     * @param other The compared quaternion
     * @param threshold The test threshold
     * @return True if they are equal
     * @since 1.0.0
     */
    public boolean equals(Quaternion other, double threshold) {
        if (other == null) return false;
        return Math.abs(x - other.x) < threshold
                && Math.abs(y - other.y) < threshold
                && Math.abs(z - other.z) < threshold
                && Math.abs(w - other.w) < threshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quaternion that = (Quaternion) o;
        return Double.compare(that.x, x) == 0
                && Double.compare(that.y, y) == 0
                && Double.compare(that.z, z) == 0
                && Double.compare(that.w, w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return "Quaternion (" + x + ", " + y + ", " + z + ", " + w + ')';
    }

}
