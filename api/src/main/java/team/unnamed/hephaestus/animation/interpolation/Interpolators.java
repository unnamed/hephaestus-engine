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

/**
 * Factory and utility methods for {@link Interpolator} and {@link
 * KeyFrameInterpolator} interfaces defined in this package.
 *
 * @since 1.0.0
 */
public final class Interpolators {
    private Interpolators() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Returns an interpolator for lineally interpolating
     * {@link Vector3Float 3d vectors}.
     *
     * @return The interpolator
     * @since 1.0.0
     */
    public static @NotNull Interpolator<Vector3Float> lerpVector3Float() {
        return LinearVectorInterpolator.INSTANCE;
    }

    /**
     * Returns a "step" interpolator for {@link Vector3Float 3d vectors},
     * which returns the start value until the progress is 1, then
     * returns the end value.
     *
     * @return The interpolator
     * @since 1.0.0
     */
    public static @NotNull Interpolator<Vector3Float> stepVector3Float() {
        return StepVectorInterpolator.INSTANCE;
    }

    /**
     * Returns a Catmull-Rom interpolator for {@link Vector3Float 3d vectors},
     * which interpolates between the given points using the Catmull-Rom
     * spline algorithm.
     *
     * @return The interpolator
     * @since 1.0.0
     */
    public static @NotNull Interpolator<Vector3Float> catmullRomSplineVector3Float() {
        return CatmullRomInterpolator.INSTANCE;
    }

    /**
     * Returns a Bezier interpolator for {@link Vector3Float 3d vectors},
     * which interpolates between the given points using Bézier curves.
     *
     * <p>Input key frames may specify the Bezier curve handles by having a
     * {@link team.unnamed.hephaestus.animation.timeline.KeyFrameBezierAttachment} attachment</p>
     *
     * @param divisions The amount of divisions to use for the Bézier curve,
     *                  the higher the value, the more accurate the interpolation
     *                  will be, but it will also be more expensive to compute.
     * @return The interpolator
     * @since 1.0.0
     */
    public static @NotNull KeyFrameInterpolator<Vector3Float> bezierVector3Float(final int divisions) {
        return new BezierInterpolator(divisions);
    }

    /**
     * Returns an interpolator that will create interpolations that always
     * return the provided {@code interpolated} value and will not perform
     * any other calculation.
     *
     * @param interpolated The value to return
     * @return The interpolator
     * @param <T> The interpolated value type
     * @since 1.0.0
     */
    public static <T> @NotNull Interpolator<T> always(final @NotNull T interpolated) {
        return (from, to) -> (Interpolation<T>) progress -> interpolated;
    }
}
