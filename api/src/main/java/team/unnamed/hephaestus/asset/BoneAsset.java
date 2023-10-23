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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BoneAsset implements Examinable {

    private final String name;
    private final int customModelData;
    private final List<ElementAsset> cubes;
    private final Map<String, BoneAsset> children;
    private final float scale;

    public BoneAsset(
            String name,
            int customModelData,
            List<ElementAsset> cubes,
            Map<String, BoneAsset> children,
            float scale
    ) {
        this.name = name;
        this.customModelData = customModelData;
        this.cubes = cubes;
        this.children = children;
        this.scale = scale;
    }

    public String name() {
        return name;
    }

    public int customModelData() {
        return customModelData;
    }

    public List<ElementAsset> cubes() {
        return cubes;
    }

    public Collection<BoneAsset> children() {
        return children.values();
    }

    /**
     * Returns the bone's model scale to be
     * written in the resource-pack.
     *
     * <p>If this scale is 4, it is probable that
     * we should use {@link team.unnamed.hephaestus.Bone#scale()}
     * too to compensate the remaining scale.</p>
     *
     * <p>The returned value is always 4, or less.</p>
     *
     * @return The bone's model scale
     * @since 1.0.0
     */
    public float scale() {
        return scale;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("name", name),
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
