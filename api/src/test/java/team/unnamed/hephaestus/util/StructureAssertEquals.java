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

import org.jetbrains.annotations.Nullable;
import org.opentest4j.AssertionFailedError;
import team.unnamed.creative.base.Vector3Float;

public final class StructureAssertEquals {

    private StructureAssertEquals() {
    }

    public static void assertVectorEquals(
            final @Nullable Vector3Float expected,
            final @Nullable Vector3Float actual,
            final double threshold
    ) {
        boolean similarEnough;
        if (expected == null) {
            similarEnough = actual == null;
        } else {
            similarEnough = actual != null
                    && Math.abs(expected.x() - actual.x()) < threshold
                    && Math.abs(expected.y() - actual.y()) < threshold
                    && Math.abs(expected.z() - actual.z()) < threshold;
        }
        if (!similarEnough) {
            throw new AssertionFailedError(
                    String.format("Expected: <%s> but was: <%s>", expected, actual),
                    expected,
                    actual
            );
        }
    }

    public static void assertQuaternionEquals(
            final @Nullable Quaternion expected,
            final @Nullable Quaternion actual,
            final double threshold
    ) {
        boolean similarEnough;
        if (expected == null) {
            similarEnough = actual == null;
        } else {
            similarEnough = expected.equals(actual, threshold);
        }
        if (!similarEnough) {
            throw new AssertionFailedError(
                    String.format("Expected: <%s> but was: <%s>", expected, actual),
                    expected,
                    actual
            );
        }
    }

    public static void assertQuaternionEquivalent(
            final @Nullable Quaternion expected,
            final @Nullable Quaternion actual,
            final double threshold
    ) {
        boolean similarEnough;
        if (expected == null) {
            similarEnough = actual == null;
        } else {
            similarEnough = expected.isEquivalentTo(actual, threshold);
        }
        if (!similarEnough) {
            throw new AssertionFailedError(
                    String.format("Equivalent expected: <%s> but was: <%s>", expected, actual),
                    expected,
                    actual
            );
        }
    }

}
