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
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;

import static java.util.Objects.requireNonNull;

final class CatmullRomInterpolator implements Interpolator<Vector3Float> {
    static final Interpolator<Vector3Float> INSTANCE = new CatmullRomInterpolator();

    private CatmullRomInterpolator() {
    }

    @Override
    public @NotNull Interpolation<Vector3Float> interpolation(final @NotNull Vector3Float from, final @NotNull Vector3Float to) {
        return new BinaryCatmullRomInterpolation(from, to);
    }

    @Override
    public @NotNull Interpolation<Vector3Float> interpolation(final @Nullable Vector3Float before, final @NotNull Vector3Float from, final @NotNull Vector3Float to, final @Nullable Vector3Float after) {
        return new CatmullRomInterpolation(before, from, to, after);
    }

    @Override
    public @NotNull KeyFrameInterpolator<Vector3Float> combineRight(final @NotNull KeyFrameInterpolator<Vector3Float> right) {
        requireNonNull(right, "right");
        // no matter what "right" interpolator is, combining a catmull-rom
        // interpolator and any other interpolator, results in a catmull-rom
        // interpolation
        return this;
    }

    // based on Blockbench and Three.js implementations
    static final class CatmullRomInterpolation implements Interpolation<Vector3Float> {

        private final @Nullable Vector3Float before;
        private final Vector3Float from;
        private final Vector3Float to;
        private final @Nullable Vector3Float after;

        CatmullRomInterpolation(
                final @Nullable Vector3Float before,
                final @NotNull Vector3Float from,
                final @NotNull Vector3Float to,
                final @Nullable Vector3Float after
        ) {
            this.before = before;
            this.from = from;
            this.to = to;
            this.after = after;
        }

        @Override
        public @NotNull Vector3Float interpolate(final double progress) {
            int len = 2;
            if (before != null) len++;
            if (after != null) len++;
            final Vector3Float[] points = new Vector3Float[len];

            int i = 0;
            if (before != null) points[i++] = before;
            points[i++] = from;
            points[i++] = to;
            if (after != null) points[i] = after;

            return catmullRom(
                    (progress + (before != null ? 1 : 0)) / (len - 1),
                    points
            );
        }

        private static Vector3Float catmullRom(double progress, Vector3Float... points) {
            final double p = (points.length - 1) * progress;

            int intPoint = (int) Math.floor(p);

            Vector3Float p0 = points[intPoint == 0 ? intPoint : intPoint - 1];
            Vector3Float p1 = points[intPoint];
            Vector3Float p2 = points[intPoint > points.length - 2 ? points.length - 1 : intPoint + 1];
            Vector3Float p3 = points[intPoint > points.length - 3 ? points.length - 1 : intPoint + 2];

            double weight = p - intPoint; // decimal part only
            double t2 = weight * weight;
            double t3 = weight * t2;

            return new Vector3Float(
                    (float) catmullRom(weight, t2, t3, p0.x(), p1.x(), p2.x(), p3.x()),
                    (float) catmullRom(weight, t2, t3, p0.y(), p1.y(), p2.y(), p3.y()),
                    (float) catmullRom(weight, t2, t3, p0.z(), p1.z(), p2.z(), p3.z())
            );
        }

        private static double catmullRom(double t, double t2, double t3, double p0, double p1, double p2, double p3) {
            double v0 = (p2 - p0) * 0.5;
            double v1 = (p3 - p1) * 0.5;
            double a = 2*p1 - 2*p2 + v0 + v1;
            double b = -3*p1 + 3*p2 - 2*v0 - v1;
            return a*t3 + b*t2 + v0*t + p1;
        }
    }

    // same as CatmullRomInterpolation, but optimized for only two points
    static final class BinaryCatmullRomInterpolation implements Interpolation<Vector3Float> {

        private final Vector3Float from;

        private final double ax, bx, cx;
        private final double ay, by, cy;
        private final double az, bz, cz;

        BinaryCatmullRomInterpolation(
                final @NotNull Vector3Float from,
                final @NotNull Vector3Float to
        ) {
            this.from = from;

            // calculate coefficients for X
            ax = from.x() - to.x();
            bx = -1.5D * ax;
            cx = -0.5D * ax;

            // calculate coefficients for Y
            ay = from.y() - to.y();
            by = -1.5D * ay;
            cy = -0.5D * ay;

            // calculate coefficients for Z
            az = from.z() - to.z();
            bz = -1.5D * az;
            cz = -0.5D * az;
        }

        @Override
        public @NotNull Vector3Float interpolate(final double t) {
            double t2 = t * t;
            double t3 = t * t2;
            return new Vector3Float(
                    (float) (ax*t3 + bx*t2 + cx*t + from.x()),
                    (float) (ay*t3 + by*t2 + cy*t + from.y()),
                    (float) (az*t3 + bz*t2 + cz*t + from.z())
            );
        }
    }

}
