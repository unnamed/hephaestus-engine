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
import team.unnamed.creative.base.Axis3D;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.animation.timeline.KeyFrame;
import team.unnamed.hephaestus.animation.timeline.KeyFrameBezierAttachment;

import static java.util.Objects.requireNonNull;

final class BezierInterpolator implements KeyFrameInterpolator<Vector3Float> {
    private static final Axis3D[] AXES = Axis3D.values();

    private final int divisions;

    BezierInterpolator(final int divisions) {
        this.divisions = divisions;
    }

    @Override
    public @NotNull Interpolation<Vector3Float> interpolation(final @NotNull KeyFrame<Vector3Float> from, final @NotNull KeyFrame<Vector3Float> to) {
        KeyFrameBezierAttachment fromBezier = from.attachment(KeyFrameBezierAttachment.class);
        KeyFrameBezierAttachment toBezier = to.attachment(KeyFrameBezierAttachment.class);

        if (fromBezier == null) {
            fromBezier = KeyFrameBezierAttachment.initial();
        }
        if (toBezier == null) {
            toBezier = KeyFrameBezierAttachment.initial();
        }

        final float timeGap = to.time() - from.time();

        // points[division][axis]
        final Vector2Float[][] points = new Vector2Float[divisions][AXES.length];

        for (final Axis3D axis : AXES) {
            final float timePoint0 = from.time();
            final float timePoint1 = timePoint0 + clamp(fromBezier.rightTime().get(axis), 0, timeGap);
            final float timePoint3 = to.time();
            final float timePoint2 = timePoint3 + clamp(toBezier.leftTime().get(axis), -timeGap, 0);

            final float valuePoint0 = from.value().get(axis);
            final float valuePoint1 = valuePoint0 + fromBezier.rightValue().get(axis);
            final float valuePoint3 = to.value().get(axis);
            final float valuePoint2 = valuePoint3 + toBezier.leftValue().get(axis);

            for (int i = 0; i < divisions; i++) {
                final double t = (double) i / (divisions - 1);
                final double s2 = t * t;
                final double s3 = t * s2;

                final double k = 1 - t;
                final double k2 = k * k;
                final double k3 = k * k2;

                final double gotTime = k3 * timePoint0 + 3 * k2 * t * timePoint1 + 3 * k * s2 * timePoint2 + s3 * timePoint3;
                final double value = k3 * valuePoint0 + 3 * k2 * t * valuePoint1 + 3 * k * s2 * valuePoint2 + s3 * valuePoint3;

                points[i][axis.ordinal()] = new Vector2Float((float) gotTime, (float) value);
            }
        }

        return t -> {
            final double k = 1 - t;
            final float time = (float) (from.time() * k + to.time() * t);

            float x = findClosestAndLerp(points, time, Axis3D.X);
            float y = findClosestAndLerp(points, time, Axis3D.Y);
            float z = findClosestAndLerp(points, time, Axis3D.Z);

            return new Vector3Float(x, y, z);
        };
    }

    @Override
    public @NotNull KeyFrameInterpolator<Vector3Float> combineRight(final @NotNull KeyFrameInterpolator<Vector3Float> right) {
        requireNonNull(right, "right");
        // Bézier + (any except smooth) = Bézier
        // Bézier + Smooth = Smooth
        if (right instanceof CatmullRomInterpolator) {
            return right;
        } else {
            return this;
        }
    }

    private static float clamp(final float value, final float min, final float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float findClosestAndLerp(final Vector2Float[][] points, final float time, final Axis3D axis) {
        float closestDistance = Float.MAX_VALUE;

        float closest = Float.MAX_VALUE;
        float closestTime = Float.MAX_VALUE;

        for (final Vector2Float[] byAxis : points) {
            Vector2Float vector = byAxis[axis.ordinal()];
            final float t = vector.x(); // time for this point
            final float value = vector.y(); // value for this point

            final float distance = Math.abs(time - t);
            if (distance < closestDistance) {
                closest = value;
                closestTime = t;
                closestDistance = distance;
            }
        }

        // find second closest
        closestDistance = Float.MAX_VALUE;
        float secondClosest = Float.MAX_VALUE;
        float secondClosestTime = Float.MAX_VALUE;

        for (final Vector2Float[] byAxis : points) {
            Vector2Float vector = byAxis[axis.ordinal()];
            final float t = vector.x(); // time for this point
            final float value = vector.y(); // value for this point

            final float distance = Math.abs(time - t);
            if (distance < closestDistance && t != closestTime) {
                secondClosest = value;
                secondClosestTime = t;
                closestDistance = distance;
            }
        }

        // linear lerp between these closest points (lerp method doesn't exist)
        final float t = clamp((time - closestTime) / (secondClosestTime - closestTime), 0, 1);
        return closest + (secondClosest - closest) * t;
    }
}
