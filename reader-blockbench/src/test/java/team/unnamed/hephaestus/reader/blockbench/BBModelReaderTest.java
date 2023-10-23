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
package team.unnamed.hephaestus.reader.blockbench;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.base.Vector4Float;
import team.unnamed.creative.model.ElementFace;
import team.unnamed.creative.model.ElementRotation;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.asset.BoneAsset;
import team.unnamed.hephaestus.asset.ElementAsset;
import team.unnamed.hephaestus.asset.ModelAsset;
import team.unnamed.hephaestus.asset.TextureAsset;
import team.unnamed.hephaestus.reader.ModelReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BBModelReaderTest {

    @Test
    @DisplayName("Test that a blockbench small cube is correctly read")
    public void test_small() throws IOException {

        ModelReader reader = BBModelReader.blockbench();

        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("cube.bbmodel")) {
            Model model = reader.read(resource);

            assertEquals("cube", model.name());
            assertEquals(1, model.bones().size());

            ModelAsset asset = model.asset();
            assertNotNull(asset);
            assertEquals(1, asset.bones().size());

            Map<String, TextureAsset> textures = asset.textures();
            assertEquals(1, textures.size());
            TextureAsset texture = textures.get("0"); // "0" is the texture identifier
            assertNotNull(texture);
            assertEquals("cube_default.png", texture.name());

            BoneAsset root = asset.boneMap().get("root");
            assertNotNull(root);
            assertEquals("root", root.name());
            assertEquals(1, root.cubes().size());

            ElementAsset element = root.cubes().get(0);
            assertNotNull(element);
            System.out.println(element.from());
            System.out.println(element.to());
            assertEquals(new Vector3Float(0F, 8F, 0F), element.from());
            assertEquals(new Vector3Float(16F, 24F, 16F), element.to());

            ElementRotation rotation = element.rotation();
            System.out.println(rotation.origin());
            assertEquals(new Vector3Float(8F, 8F, 8F), rotation.origin());
            assertEquals(0F, rotation.angle());

            Map<CubeFace, ElementFace> faces = element.faces();

            ElementFace south = faces.get(CubeFace.SOUTH);
            assertNotNull(south);
            assertEquals(new Vector4Float(0.25F, 0F, 0.5F, 0.25F), south.uv());

            ElementFace up = faces.get(CubeFace.UP);
            assertNotNull(up);
            assertEquals(new Vector4Float(0.25F, 0.75F, 0F, 0.5F), up.uv());

            ElementFace north = faces.get(CubeFace.NORTH);
            assertNotNull(north);
            assertEquals(new Vector4Float(0F, 0F, 0.25F, 0.25F), north.uv());

            ElementFace east = faces.get(CubeFace.EAST);
            assertNotNull(east);
            assertEquals(new Vector4Float(0F, 0.25F, 0.25F, 0.5F), east.uv());

            ElementFace west = faces.get(CubeFace.WEST);
            assertNotNull(west);
            assertEquals(new Vector4Float(0.25F, 0.25F, 0.5F, 0.5F), west.uv());

            ElementFace down = faces.get(CubeFace.DOWN);
            assertNotNull(down);
            assertEquals(new Vector4Float(0.75F, 0F, 0.5F, 0.25F), down.uv());
        }
    }

}
