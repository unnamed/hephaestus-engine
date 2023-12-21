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
package team.unnamed.hephaestus.animation.timeline.effect;

import net.kyori.adventure.sound.Sound;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class EffectsFrame implements Examinable {
    public static EffectsFrame INITIAL = new EffectsFrame(Collections.emptyList(), Collections.emptyList());

    private final List<Sound> sounds;
    private final List<String> instructions;

    public EffectsFrame(final @NotNull List<Sound> sounds, final @NotNull List<String> instructions) {
        this.sounds = requireNonNull(sounds, "sounds");
        this.instructions = requireNonNull(instructions, "instructions");
    }

    public @NotNull @Unmodifiable List<Sound> sounds() {
        return sounds;
    }

    public @NotNull @Unmodifiable List<String> instructions() {
        return instructions;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("sounds", sounds),
                ExaminableProperty.of("instructions", instructions)
        );
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EffectsFrame that = (EffectsFrame) o;
        return sounds.equals(that.sounds) && instructions.equals(that.instructions);
    }

    @Override
    public int hashCode() {
        int result = sounds.hashCode();
        result = 31 * result + instructions.hashCode();
        return result;
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }
}