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
package team.unnamed.hephaestus.animation.interpolation;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.util.Quaternion;

/**
 * Represents an interpolation function,
 * which interpolates between two values of
 * the same type.
 *
 * <p>Can also be understood as a factory of
 * {@link Interpolation} instances</p>
 *
 * <p>Here is an example on linear interpolation
 * for {@link Vector3Float 3d vectors}:</p>
 *
 * <pre>{@code
 * Interpolation<Vector3Float> interpolation = Interpolator.lerpVector3Float()
 *    .interpolation(
 *          new Vector3Float(0, 0, 0),
 *          new Vector3Float(5, 10, 5)
 *    );
 * interpolation.interpolate(0); // (0, 0, 0)
 * interpolation.interpolate(0.5F); // (2.5, 5, 2.5)
 * interpolation.interpolate(0.25F); // (1.25, 2.5, 1.25)
 * interpolation.interpolate(0.75F); // (3.75, 7.5, 3.75)
 * interpolation.interpolate(1); // (5, 10, 5)
 * }</pre>
 *
 * @param <T> The interpolated value type
 * @since 1.0.0
 */
public interface Interpolator<T> {

    /**
     * Creates an interpolation between the given
     * values, using the function of this implementation.
     *
     * @param from The start value
     * @param to The end value
     * @return The interpolation
     * @since 1.0.0
     */
    @NotNull Interpolation<T> interpolation(final @NotNull T from, final @NotNull T to);

    /**
     * Returns an interpolator for lineally interpolating
     * {@link Vector3Float 3d vectors}.
     *
     * @return The interpolator
     * @since 1.0.0
     */
    static @NotNull Interpolator<Vector3Float> lerpVector3Float() {
        return LinearVectorInterpolator.INSTANCE;
    }

    /**
     * Returns an interpolator for spherically interpolating
     * {@link Quaternion quaternions}.
     *
     * @return The interpolator
     * @since 1.0.0
     */
    static @NotNull Interpolator<Quaternion> slerpQuaternion() {
        return SphericalQuaternionInterpolator.INSTANCE;
    }

}
