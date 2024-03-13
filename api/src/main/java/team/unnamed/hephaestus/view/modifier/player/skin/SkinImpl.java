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
package team.unnamed.hephaestus.view.modifier.player.skin;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

final class SkinImpl implements Skin {
    private final String signature;
    private final String value;
    private final Type type;
    private final CompoundBinaryTag asNBT;

    SkinImpl(final @NotNull String signature, final @NotNull String value, final @NotNull Type type) {
        this.signature = requireNonNull(signature, "signature");
        this.value = requireNonNull(value, "value");
        this.type = requireNonNull(type, "type");
        this.asNBT = CompoundBinaryTag.builder()
                .putString("Value", value)
                .putString("Signature", signature)
                .build();
    }

    @Override
    public @NotNull String signature() {
        return signature;
    }

    @Override
    public @NotNull String value() {
        return value;
    }

    @Override
    public @NotNull Type type() {
        return type;
    }

    @Override
    public @NotNull CompoundBinaryTag asNBT() {
        return asNBT;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("signature", signature),
                ExaminableProperty.of("value", value),
                ExaminableProperty.of("type", type)
        );
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SkinImpl skin = (SkinImpl) o;
        if (!signature.equals(skin.signature)) return false;
        if (!value.equals(skin.value)) return false;
        return type == skin.type;
    }

    @Override
    public int hashCode() {
        int result = signature.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
