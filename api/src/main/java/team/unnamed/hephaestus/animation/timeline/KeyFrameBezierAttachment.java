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
package team.unnamed.hephaestus.animation.timeline;

import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * A {@link KeyFrameAttachment} containing information for building Bézier curves and
 * performing interpolation.
 *
 * @since 1.0.0
 */
public final class KeyFrameBezierAttachment implements KeyFrameAttachment, Examinable {
    private static final KeyFrameBezierAttachment INITIAL = new KeyFrameBezierAttachment(
            new Vector3Float(-0.1F, -0.1F, -0.1F),
            Vector3Float.ZERO,
            new Vector3Float(0.1F, 0.1F, 0.1F),
            Vector3Float.ZERO
    );

    private final Vector3Float leftTime;
    private final Vector3Float leftValue;
    private final Vector3Float rightTime;
    private final Vector3Float rightValue;

    private KeyFrameBezierAttachment(final @NotNull Vector3Float leftTime, final @NotNull Vector3Float leftValue, final @NotNull Vector3Float rightTime, final @NotNull Vector3Float rightValue) {
        this.leftTime = requireNonNull(leftTime, "leftTime");
        this.leftValue = requireNonNull(leftValue, "leftValue");
        this.rightTime = requireNonNull(rightTime, "rightTime");
        this.rightValue = requireNonNull(rightValue, "rightValue");
    }

    public @NotNull Vector3Float leftTime() {
        return leftTime;
    }

    public @NotNull Vector3Float leftValue() {
        return leftValue;
    }

    public @NotNull Vector3Float rightTime() {
        return rightTime;
    }

    public @NotNull Vector3Float rightValue() {
        return rightValue;
    }

    public static @NotNull KeyFrameBezierAttachment of(final @NotNull Vector3Float leftTime, final @NotNull Vector3Float leftValue, final @NotNull Vector3Float rightTime, final @NotNull Vector3Float rightValue) {
        return new KeyFrameBezierAttachment(leftTime, leftValue, rightTime, rightValue);
    }

    public static @NotNull KeyFrameBezierAttachment initial() {
        return INITIAL;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("leftTime", leftTime),
                ExaminableProperty.of("leftValue", leftValue),
                ExaminableProperty.of("rightTime", rightTime),
                ExaminableProperty.of("rightValue", rightValue)
        );
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final KeyFrameBezierAttachment that = (KeyFrameBezierAttachment) o;
        if (!leftTime.equals(that.leftTime)) return false;
        if (!leftValue.equals(that.leftValue)) return false;
        if (!rightTime.equals(that.rightTime)) return false;
        return rightValue.equals(that.rightValue);
    }

    @Override
    public int hashCode() {
        int result = leftTime.hashCode();
        result = 31 * result + leftValue.hashCode();
        result = 31 * result + rightTime.hashCode();
        result = 31 * result + rightValue.hashCode();
        return result;
    }
}
