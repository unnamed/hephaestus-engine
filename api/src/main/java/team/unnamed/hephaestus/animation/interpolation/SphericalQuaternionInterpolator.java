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
import team.unnamed.hephaestus.util.Quaternion;

final class SphericalQuaternionInterpolator implements Interpolator<Quaternion> {

    static final Interpolator<Quaternion> INSTANCE = new SphericalQuaternionInterpolator();

    private SphericalQuaternionInterpolator() {
    }

    @Override
    public @NotNull Interpolation<Quaternion> interpolation(final @NotNull Quaternion from, final @NotNull Quaternion to) {
        Quaternion positiveTo = to;
        double dot = from.dot(to);

        if (dot < 0.0F) {
            positiveTo = positiveTo.negate();
            dot = -dot;
        }

        if (dot > 0.9995F) {
            // quaternions are very close, just use linear interpolation
            return new LinearQuaternionInterpolation(from, positiveTo);
        }

        return new SphericalQuaternionInterpolation(from, positiveTo, dot);
    }

    static final class LinearQuaternionInterpolation implements Interpolation<Quaternion> {

        private final Quaternion from;
        private final Quaternion to;

        LinearQuaternionInterpolation(
                final @NotNull Quaternion from,
                final @NotNull Quaternion to
        ) {
            this.from = from;
            this.to = to;
        }

        @Override
        public @NotNull Quaternion interpolate(double progress) {
            final double complement = 1 - progress;
            return new Quaternion(
                    complement * from.x() + progress * to.x(),
                    complement * from.y() + progress * to.y(),
                    complement * from.z() + progress * to.z(),
                    complement * from.w() + progress * to.w()
            );
        }

    }

    static final class SphericalQuaternionInterpolation implements Interpolation<Quaternion> {

        private final Quaternion from;
        private final Quaternion to;

        private final double theta;
        private final double sinTheta;

        SphericalQuaternionInterpolation(
                final @NotNull Quaternion from,
                final @NotNull Quaternion to,
                double dot
        ) {
            this.from = from;
            this.to = to;
            this.theta = Math.acos(dot);
            this.sinTheta = Math.sin(theta);
        }

        @Override
        public @NotNull Quaternion interpolate(final double progress) {
            final double f1 = Math.sin((1 - progress) * theta) / sinTheta;
            final double f2 = Math.sin(progress * theta) / sinTheta;

            return new Quaternion(
                    f1 * from.x() + f2 * to.x(),
                    f1 * from.y() + f2 * to.y(),
                    f1 * from.z() + f2 * to.z(),
                    f1 * from.w() + f2 * to.w()
            );
        }

    }

}
