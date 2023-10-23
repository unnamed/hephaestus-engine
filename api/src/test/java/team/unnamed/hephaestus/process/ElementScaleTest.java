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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.Axis3D;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.model.ElementRotation;
import team.unnamed.hephaestus.asset.ElementAsset;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ElementScaleTest {

    @Test
    @DisplayName("Test that a 16x16x16 block cube is correctly processed")
    public void test_single_small_block() {
        MonoResult monoResult = processSingleCube(new ElementAsset(
                // 16x16x16 block
                new Vector3Float(-8, -8, -8),
                new Vector3Float(8, 8, 8),
                ElementRotation.of(Vector3Float.ZERO, Axis3D.X, 0, false),
                Collections.emptyMap()
        ));

        assertEquals(1F, monoResult.scale);

        ElementAsset scaledElement = monoResult.element;
        assertEquals(new Vector3Float(0F, 0F, 0F), scaledElement.from(), "from");
        assertEquals(new Vector3Float(16F, 16F, 16F), scaledElement.to(), "to");
    }

    @Test
    @DisplayName("Test that a 128x128x128 block cube is processed")
    public void test_single_large_block_mid() {
        MonoResult result = processSingleCube(new ElementAsset(
                // 128x128x128 block
                new Vector3Float(-64, -64, -64),
                new Vector3Float(64, 64, 64),
                ElementRotation.of(Vector3Float.ZERO, Axis3D.X, 0, false),
                Collections.emptyMap()
        ));

        assertEquals(2.6666667F, result.scale);

        ElementAsset scaledElement = result.element;
        assertEquals(new Vector3Float(-16F, -16F, -16F), scaledElement.from(), "from");
        assertEquals(new Vector3Float(32F, 32F, 32F), scaledElement.to(), "to");
    }

    @Test
    @DisplayName("Test that a 192x192x192 block cube is processed")
    public void test_single_large_block_max() {
        MonoResult result = processSingleCube(new ElementAsset(
                // 128x128x128 block
                new Vector3Float(-96, -96, -96),
                new Vector3Float(96, 96, 96),
                ElementRotation.of(Vector3Float.ZERO, Axis3D.X, 0, false),
                Collections.emptyMap()
        ));

        // scale is maximum here!
        assertEquals(4F, result.scale);

        ElementAsset scaledElement = result.element;
        assertEquals(new Vector3Float(-16F, -16F, -16F), scaledElement.from(), "from");
        assertEquals(new Vector3Float(32F, 32F, 32F), scaledElement.to(), "to");
    }

    @Test
    @DisplayName("Test bigass block cube")
    public void test_bigass_block_cube() {
        MonoResult result = processSingleCube(new ElementAsset(
                // 1024x1024x1024 cube (64x64x64 minecraft blocks! big as fuck!)
                new Vector3Float(-512, -512, -512),
                new Vector3Float(512, 512, 512),
                ElementRotation.of(Vector3Float.ZERO, Axis3D.X, 0, false),
                Collections.emptyMap()
        ));

        // scale is big as fuck too! and will be split into two scales, one applied
        // by the resource-pack (max 4), and another, applied in-game (5.33333)
        assertEquals(21.333334F, result.scale);

        ElementAsset scaledElement = result.element;
        assertEquals(new Vector3Float(-16F, -16F, -16F), scaledElement.from(), "from");
        assertEquals(new Vector3Float(32F, 32F, 32F), scaledElement.to(), "to");
    }

    private static MonoResult processSingleCube(ElementAsset cube) {
        ElementScale.Result result = ElementScale.process(Vector3Float.ZERO, Collections.singletonList(cube));
        return new MonoResult(result.elements().get(0), result.scale());
    }

    private static class MonoResult {

        private final ElementAsset element;
        private final float scale;

        private MonoResult(ElementAsset element, float scale) {
            this.element = element;
            this.scale = scale;
        }
    }

}
