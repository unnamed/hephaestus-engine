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
package team.unnamed.hephaestus.reader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.partial.BoneAsset;
import team.unnamed.hephaestus.partial.ElementAsset;
import team.unnamed.hephaestus.partial.ModelAsset;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BBModelReaderTest {

    @Test
    @DisplayName("Test that a small model is correctly read as a single small bone")
    public void test_small() throws IOException {

        ModelReader reader = new BBModelReader();

        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("cube.bbmodel")) {
            Model model = reader.read(resource);

            assertEquals("cube", model.name());
            assertEquals(1, model.bones().size());

            ModelAsset asset = model.asset();
            assertNotNull(asset);
            assertEquals(1, asset.bones().size());

            BoneAsset root = asset.boneMap().get("root");
            assertNotNull(root);
            assertEquals("root", root.name());
            assertEquals(Vector3Float.ZERO, root.pivot());
            assertEquals(1, root.cubes().size());

            ElementAsset element = root.cubes().get(0);
            assertNotNull(element);
            assertEquals(new Vector3Float(-8F, 0F, -8F), element.from());
            assertEquals(new Vector3Float(8F, 16F, 8F), element.to());
        }
    }

}
