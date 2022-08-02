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
package team.unnamed.hephaestus.partial;

import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BoneAsset implements Examinable {

    private final String name;
    private final Vector3Float pivot;
    private final int customModelData;
    private final Vector3Float offset;
    private final List<ElementAsset> cubes;
    private final boolean small;
    private final Map<String, BoneAsset> children;

    public BoneAsset(
            String name,
            Vector3Float pivot,
            int customModelData,
            Vector3Float offset,
            List<ElementAsset> cubes,
            boolean small,
            Map<String, BoneAsset> children
    ) {
        this.name = name;
        this.pivot = pivot;
        this.customModelData = customModelData;
        this.offset = offset;
        this.cubes = cubes;
        this.small = small;
        this.children = children;
    }

    public String name() {
        return name;
    }

    public Vector3Float pivot() {
        return pivot;
    }

    public int customModelData() {
        return customModelData;
    }

    public Vector3Float offset() {
        return offset;
    }

    public List<ElementAsset> cubes() {
        return cubes;
    }

    public boolean small() {
        return small;
    }

    public Collection<BoneAsset> children() {
        return children.values();
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("name", name),
                ExaminableProperty.of("pivot", pivot),
                ExaminableProperty.of("customModelData", customModelData),
                ExaminableProperty.of("cubes", cubes),
                ExaminableProperty.of("children", children)
        );
    }

    @Override
    public String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

}
