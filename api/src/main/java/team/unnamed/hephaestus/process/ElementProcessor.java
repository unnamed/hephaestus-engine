/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
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

import team.unnamed.creative.base.Axis3D;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.model.Element;
import team.unnamed.creative.model.ElementRotation;
import team.unnamed.hephaestus.partial.ElementAsset;

import java.util.ArrayList;
import java.util.List;

public class ElementProcessor {

    /**
     * The size of a block for models, this is the number that
     * relates Minecraft blocks to our models
     */
    private static final float BLOCK_SIZE = 16F;
    private static final float HALF_BLOCK_SIZE = BLOCK_SIZE / 2F;

    private static final float MAX_LARGE_SIZE = BLOCK_SIZE * 8F; // 128 pixels (8 blocks)
    private static final float MAX_SMALL_SIZE = BLOCK_SIZE * 5F; // 80 pixels (5 blocks)

    private static final float MAX_ELEMENT_SIZE = Element.MAX_EXTENT - Element.MIN_EXTENT;

    private static final float SMALL_RATIO = MAX_ELEMENT_SIZE / MAX_SMALL_SIZE;
    private static final float LARGE_RATIO = MAX_ELEMENT_SIZE / MAX_LARGE_SIZE;

    public static class Result {
        private final List<ElementAsset> elements;
        private final Vector3Float offset;
        private final boolean small;

        Result(List<ElementAsset> elements, Vector3Float offset, boolean small) {
            this.elements = elements;
            this.offset = offset;
            this.small = small;
        }

        public List<ElementAsset> elements() {
            return elements;
        }

        public Vector3Float offset() {
            return offset;
        }

        public boolean small() {
            return small;
        }
    }

    public static Result process(Vector3Float pivot, List<ElementAsset> elements) {

        List<ElementAsset> smallElements = scale(pivot, elements, SMALL_RATIO);
        Vector3Float smallOffset = computeOffset(smallElements);

        if (applyOffset(smallElements, smallOffset)) {
            // success applying small sizes
            return new Result(smallElements, smallOffset, true);
        } else {
            // try using large sizes
            List<ElementAsset> largeElements = scale(pivot, elements, LARGE_RATIO);
            Vector3Float largeOffset = computeOffset(largeElements);

            if (applyOffset(largeElements, largeOffset)) {
                // success using large sizes
                return new Result(largeElements, largeOffset, false);
            } else {
                throw new IllegalStateException("Cubes out of bounds");
            }
        }
    }

    private static List<ElementAsset> scale(
            Vector3Float pivot,
            List<ElementAsset> elements,
            float ratio
    ) {
        float deltaX = pivot.x() - HALF_BLOCK_SIZE;
        float deltaY = pivot.y() - HALF_BLOCK_SIZE;
        float deltaZ = pivot.z() - HALF_BLOCK_SIZE;
        List<ElementAsset> scaledElements = new ArrayList<>(elements.size());

        for (ElementAsset cube : elements) {

            Vector3Float origin = cube.from();
            Vector3Float to = cube.to();

            ElementRotation rotation = cube.rotation();
            Vector3Float rotationOrigin = rotation.origin();
            rotationOrigin = new Vector3Float(
                    scale(-rotationOrigin.x() + pivot.x() + HALF_BLOCK_SIZE, ratio),
                    scale(rotationOrigin.y() - pivot.y() + HALF_BLOCK_SIZE, ratio),
                    scale(rotationOrigin.z() - pivot.z() + HALF_BLOCK_SIZE, ratio)
            );

            scaledElements.add(new ElementAsset(
                    // from
                    new Vector3Float(
                            scale(BLOCK_SIZE + deltaX - to.x(), ratio),
                            scale(origin.y() - deltaY, ratio),
                            scale(origin.z() - deltaZ, ratio)
                    ),
                    // to
                    new Vector3Float(
                            scale(BLOCK_SIZE + deltaX - origin.x(), ratio),
                            scale(to.y() - deltaY, ratio),
                            scale(to.z() - deltaZ, ratio)
                    ),
                    rotation.origin(rotationOrigin),
                    cube.faces()
            ));
        }

        return scaledElements;
    }

    /**
     * Computes the offset for the given cube elements,
     * does not modify the provided list
     *
     * @param elements The elements to compute
     * @return The resulting offset
     */
    private static Vector3Float computeOffset(List<ElementAsset> elements) {
        Vector3Float offset = Vector3Float.ZERO;
        for (ElementAsset cube : elements) {
            Vector3Float from = cube.from();
            Vector3Float to = cube.to();

            for (Axis3D axis : Axis3D.values()) {
                offset = computeOffset(offset, axis, from);
                offset = computeOffset(offset, axis, to);
            }
        }
        return offset;
    }

    private static Vector3Float computeOffset(
            Vector3Float offset,
            Axis3D axis,
            Vector3Float from
    ) {
        float off = offset.get(axis);
        float value = from.get(axis);

        if (value + off > Element.MAX_EXTENT) {
            off -= value + off - Element.MAX_EXTENT;
        }
        if (value + off < Element.MIN_EXTENT) {
            off -= value + off - Element.MIN_EXTENT;
        }

        return offset.with(axis, off);
    }

    /**
     * Applies an offset to a given list of elements
     *
     * @param elements The element list to modify
     * @param offset The applied offset
     * @return True if successfully applied, false otherwise
     * (is out of MIN_EXTENT and MAX_EXTENT)
     */
    private static boolean applyOffset(List<ElementAsset> elements, Vector3Float offset) {
        // compute offset
        for (int i = 0; i < elements.size(); i++) {
            ElementAsset cube = elements.get(i);
            Vector3Float from = cube.from().add(offset);
            Vector3Float to = cube.to().add(offset);

            if (isOutOfBounds(from) || isOutOfBounds(to)) {
                // fail
                return false;
            }

            ElementRotation rotation = cube.rotation();

            Vector3Float origin = rotation.origin();
            rotation = rotation.origin(origin.add(offset));

            elements.set(i, new ElementAsset(from, to, rotation, cube.faces()));
        }

        // success
        return true;
    }

    private static boolean isOutOfBounds(Vector3Float location) {
        return location.x() < Element.MIN_EXTENT
                || location.y() < Element.MIN_EXTENT
                || location.z() < Element.MIN_EXTENT
                || location.x() > Element.MAX_EXTENT
                || location.y() > Element.MAX_EXTENT
                || location.z() > Element.MAX_EXTENT;
    }

    private static float scale(float value, float ratio) {
        return HALF_BLOCK_SIZE - (ratio * (HALF_BLOCK_SIZE - value));
    }

}
