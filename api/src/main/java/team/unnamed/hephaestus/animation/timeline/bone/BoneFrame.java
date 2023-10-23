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
package team.unnamed.hephaestus.animation.timeline.bone;

import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;

import java.util.Objects;
import java.util.stream.Stream;

public final class BoneFrame implements Examinable {

    public static final BoneFrame INITIAL = new BoneFrame(
            Vector3Float.ZERO,
            Vector3Float.ZERO,
            Vector3Float.ONE
    );

    private final Vector3Float position;
    private final Vector3Float rotation;
    private final Vector3Float scale;

    public BoneFrame(
            Vector3Float position,
            Vector3Float rotation,
            Vector3Float scale
    ) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3Float position() {
        return position;
    }

    public Vector3Float rotation() {
        return rotation;
    }

    public Vector3Float scale() {
        return scale;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("position", position),
                ExaminableProperty.of("rotation", rotation),
                ExaminableProperty.of("scale", scale)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoneFrame boneFrame = (BoneFrame) o;
        return position.equals(boneFrame.position)
                && rotation.equals(boneFrame.rotation)
                && scale.equals(boneFrame.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, rotation, scale);
    }

    @Override
    public String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

}