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
package team.unnamed.hephaestus.asset;


import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.model.ElementFace;
import team.unnamed.creative.model.ElementRotation;

import java.util.Map;
import java.util.stream.Stream;

public class ElementAsset implements Examinable {

    private final Vector3Float from;
    private final Vector3Float to;
    private final ElementRotation rotation;
    private final Map<CubeFace, ElementFace> faces;

    public ElementAsset(
            Vector3Float from,
            Vector3Float to,
            ElementRotation rotation,
            Map<CubeFace, ElementFace> faces
    ) {
        this.from = from;
        this.to = to;
        this.rotation = rotation;
        this.faces = faces;
    }

    public Vector3Float from() {
        return from;
    }

    public Vector3Float to() {
        return to;
    }

    public ElementRotation rotation() {
        return rotation;
    }

    public Map<CubeFace, ElementFace> faces() {
        return faces;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("from", from),
                ExaminableProperty.of("to", to),
                ExaminableProperty.of("rotation", rotation),
                ExaminableProperty.of("faces", faces)
        );
    }

    @Override
    public String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

}
