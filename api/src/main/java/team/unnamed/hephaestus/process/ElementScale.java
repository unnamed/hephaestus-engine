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
package team.unnamed.hephaestus.process;

import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.asset.ElementAsset;

import java.util.Arrays;
import java.util.List;

@ApiStatus.Internal
public class ElementScale {

    // Minecraft Models have a maximum size of 48x48x48 pixels, where normally,
    // with scale = 1, the size of 16x16x16 pixels is equal to one Minecraft block.
    //
    // For some reason, the rotation origin of the model is located at (8,8,8),
    // with scale 1, the minimum point is (-16, -16, -16) and the maximum point
    // is (32, 32, 32).
    //
    // We can normalize this by subtracting 8 from the rotation origin and from
    // the minimum and maximum point, so that our rotation origin is located at
    // (0, 0, 0) and the minimum point is (-24, -24, -24) and the maximum point
    // is (24, 24, 24).

    private static final Vector3Float MINECRAFT_ORIGIN = new Vector3Float(8F, 8F, 8F);

    private static final float MAXIMUM_ABSOLUTE_COORDINATE = 24F;

    public static Result process(Vector3Float origin, List<ElementAsset> originalElements) {
        //
        // The semi length of the model cube. By default, it is 24. the computation
        // of the max coordinate is to check for elements that are outside the default
        // cube, and if they are, we will need to increase the semi length of the cube
        // so that the element is inside the cube.
        //
        // Minecraft doesn't accept cubes bigger than 24, so we will have to resize
        // everything again, and then, in game, set a compensation scale to the model
        //
        float maxAbsoluteCoordinate = MAXIMUM_ABSOLUTE_COORDINATE;

        final int len = originalElements.size();
        final ElementAsset[] elements = new ElementAsset[len];

        for (int i = 0; i < len; ++i) {
            final ElementAsset element = originalElements.get(i);

            // subtract the provided origin from the element coordinates,
            // makes the origin of the element to be (0, 0, 0)
            final Vector3Float from = element.from().subtract(origin);
            final Vector3Float to = element.to().subtract(origin);
            elements[i] = new ElementAsset(
                    from,
                    to,
                    element.rotation().origin(element.rotation().origin().subtract(origin)),
                    element.faces()
            );

            // calculate the maximum semi length of the model cube
            maxAbsoluteCoordinate = max(
                    maxAbsoluteCoordinate,
                    Math.abs(from.x()),
                    Math.abs(from.y()),
                    Math.abs(from.z()),
                    Math.abs(to.x()),
                    Math.abs(to.y()),
                    Math.abs(to.z())
            );
        }

        // the scale that we will use to resize the model
        // so that it fits in the 24x24x24 cube, note that
        // Minecraft accepts a maximum scale of 4, so if our
        // scale exceeds that number, we must set scale 4 and
        // apply the compensation scale from the game
        final float scale = maxAbsoluteCoordinate / MAXIMUM_ABSOLUTE_COORDINATE;

        // divide all coordinates by the scale and add the
        // Minecraft origin to the coordinates, so that the
        // origin of the model is located at (8, 8, 8), just
        // like Minecraft likes.
        for (int i = 0; i < len; ++i) {
            final ElementAsset element = elements[i];
            elements[i] = new ElementAsset(
                    clampCoordinate(element.from().divide(scale).add(MINECRAFT_ORIGIN)),
                    clampCoordinate(element.to().divide(scale).add(MINECRAFT_ORIGIN)),
                    element.rotation().origin(
                            element.rotation().origin()
                                    .divide(scale)
                                    .add(MINECRAFT_ORIGIN)
                    ),
                    element.faces()
            );
        }

        return new Result(
                Arrays.asList(elements),
                scale
        );
    }

    // todo: remove clamp, this is "just in case", because some float operations leave remaining, that make coordinates be out of bounds, e.g. -16.00000002
    private static Vector3Float clampCoordinate(Vector3Float v) {
        return new Vector3Float(
                Math.min(Math.max(v.x(), -16F), 32F),
                Math.min(Math.max(v.y(), -16F), 32F),
                Math.min(Math.max(v.z(), -16F), 32F)
        );
    }

    private static float max(float f1, float f2, float f3, float f4, float f5, float f6, float f7) {
        return Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(f1, f2), f3), f4), f5), f6), f7);
    }

    public static class Result {
        private final List<ElementAsset> elements;
        private final float scale;

        Result(List<ElementAsset> elements, float scale) {
            this.elements = elements;
            this.scale = scale;
        }

        public List<ElementAsset> elements() {
            return elements;
        }

        public float scale() {
            return scale;
        }

    }

}
