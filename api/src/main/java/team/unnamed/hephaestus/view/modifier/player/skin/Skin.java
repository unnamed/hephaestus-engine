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
import net.kyori.examination.Examinable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a player skin, which is composed of a
 * signature and a value, both of them are Base64
 * encoded strings.
 *
 * @since 1.0.0
 */
@ApiStatus.NonExtendable
public interface Skin extends Examinable {
    @Contract("_, _, _ -> new")
    static @NotNull Skin skin(final @NotNull String signature, final @NotNull String value, final @NotNull Type type) {
        return new SkinImpl(signature, value, type);
    }

    @Contract("_, _ -> new")
    static @NotNull Skin skin(final @NotNull String signature, final @NotNull String value) {
        return skin(signature, value, Type.NORMAL);
    }

    @NotNull String signature();

    @NotNull String value();

    @NotNull Type type();

    @NotNull CompoundBinaryTag asNBT();

    enum Type {
        NORMAL,
        SLIM
    }
}