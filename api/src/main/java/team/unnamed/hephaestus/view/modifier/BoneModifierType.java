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
package team.unnamed.hephaestus.view.modifier;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.examination.Examinable;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.view.AbstractBoneView;
import team.unnamed.hephaestus.view.modifier.player.PlayerBoneModifier;
import team.unnamed.hephaestus.view.modifier.player.PlayerBoneModifierImpl;

import java.util.function.Function;

public final class BoneModifierType<T extends BoneModifier> implements Keyed, Examinable {
    public static final BoneModifierType<PlayerBoneModifier> PLAYER_PART = new BoneModifierType<>(
            Key.key("hephaestus", "player_part"),
            PlayerBoneModifier.class,
            PlayerBoneModifierImpl::new
    );

    private final Key key;
    private final Class<T> type;
    private final Function<AbstractBoneView, T> factory;

    private BoneModifierType(final @NotNull Key key, final @NotNull Class<T> type, final @NotNull Function<AbstractBoneView, T> factory) {
        this.key = key;
        this.type = type;
        this.factory = factory;
    }

    @Override
    public @NotNull Key key() {
        return key;
    }

    public @NotNull Class<T> type() {
        return type;
    }

    public @NotNull T create(final @NotNull AbstractBoneView bone) {
        return factory.apply(bone);
    }
}
