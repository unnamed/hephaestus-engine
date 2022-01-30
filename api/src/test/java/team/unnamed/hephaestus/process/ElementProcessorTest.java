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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.Axis3D;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.model.ElementRotation;
import team.unnamed.hephaestus.partial.ElementAsset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementProcessorTest {

    @Test
    public void test() {
        List<ElementAsset> elements = new ArrayList<>();
        elements.add(new ElementAsset(
                // 16x16x16 element
                new Vector3Float(-8, -8, -8),
                new Vector3Float(8, 8, 8),
                ElementRotation.of(Vector3Float.ZERO, Axis3D.X, 0, false),
                Collections.emptyMap()
        ));
        ElementProcessor.Result result = ElementProcessor.process(Vector3Float.ZERO, elements);

        ElementAsset scaledElement = result.elements().get(0);

        // 9.6x9.6x9.6 cube, which is then multiplied
        // by the scale (4, max scale) by the client,
        // creating a 38.4x38.4x38.4 cube, which is the
        // size of a block when using an item in a
        // small armor stand head
        Assertions.assertEquals(new Vector3Float(3.2F, 3.2F, 3.2F), scaledElement.from());
        Assertions.assertEquals(new Vector3Float(12.8F, 12.8F, 12.8F), scaledElement.to());

        Assertions.assertEquals(Vector3Float.ZERO, result.offset());
        Assertions.assertTrue(result.small());
    }

}
