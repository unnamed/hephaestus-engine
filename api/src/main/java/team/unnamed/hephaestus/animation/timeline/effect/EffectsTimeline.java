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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @since 1.0.0
 */
public interface EffectsTimeline {

    static @NotNull EffectsTimeline.Builder effectsTimeline() {
        return new EffectsTimelineImpl.BuilderImpl();
    }

    static @NotNull EffectsTimeline.Builder empty() {
        return effectsTimeline()
                .sounds(Collections.emptyMap())
                .instructions(Collections.emptyMap());
    }

    default @NotNull EffectsTimelinePlayhead createPlayhead() {
        return new EffectsTimelinePlayhead(this);
    }

    @NotNull Map<Integer, List<Sound>> sounds();

    @NotNull Map<Integer, List<String>> instructions();

    interface Builder {

        /**
         * Set the sounds timeline
         *
         * @param sounds The sounds timeline
         * @return This builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        @NotNull Builder sounds(final @NotNull Map<Integer, List<Sound>> sounds);

        /**
         * Set the instructions timeline
         *
         * @param instructions The instructions timeline
         * @return This builder
         * @since 1.0.0
         */
        @Contract("_ -> this")
        @NotNull Builder instructions(final @NotNull Map<Integer, List<String>> instructions);

        @NotNull EffectsTimeline build();

    }

}
