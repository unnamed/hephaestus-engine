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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

final class EffectsTimelineImpl implements EffectsTimeline {

    private final Map<Integer, List<Sound>> sounds;
    private final Map<Integer, List<String>> instructions;

    EffectsTimelineImpl(Map<Integer, List<Sound>> sounds, Map<Integer, List<String>> instructions) {
        this.sounds = requireNonNull(sounds, "sounds");
        this.instructions = requireNonNull(instructions, "instructions");
    }

    @Override
    public @NotNull Map<Integer, List<Sound>> sounds() {
        return sounds;
    }

    @Override
    public @NotNull Map<Integer, List<String>> instructions() {
        return instructions;
    }

    @Override
    public String toString() {
        return "EffectsTimelineImpl{" +
                "sounds=" + sounds +
                '}';
    }

    static final class BuilderImpl implements Builder {
        private Map<Integer, List<Sound>> sounds;
        private Map<Integer, List<String>> instructions;

        BuilderImpl() {
        }

        @Override
        public @NotNull Builder sounds(@NotNull Map<Integer, List<Sound>> sounds) {
            this.sounds = requireNonNull(sounds, "sounds");
            return this;
        }

        @Override
        public @NotNull Builder instructions(@NotNull Map<Integer, List<String>> instructions) {
            this.instructions = requireNonNull(instructions, "instructions");
            return this;
        }

        @Override
        public @NotNull EffectsTimeline build() {
            return new EffectsTimelineImpl(sounds, instructions);
        }
    }
}
