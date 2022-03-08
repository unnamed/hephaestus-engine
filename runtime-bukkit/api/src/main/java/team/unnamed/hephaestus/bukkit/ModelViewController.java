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
package team.unnamed.hephaestus.bukkit;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.hephaestus.Bone;

/**
 * The main abstraction to adapt the API to a specific
 * server version, users should not directly interact
 * with this interface, use {@link ModelViewRenderer}
 * instead
 */
@ApiStatus.Internal
public interface ModelViewController {

    /**
     * Creates a new, version-specific bone view
     * for the given {@link ModelView} and
     * {@link Bone}
     *
     * <p>This method is part of the construction
     * phase, where we do not care about viewers
     * yet, we just create our objects</p>
     *
     * @param view The parent model view
     * @param bone The wrapped bone
     * @return The created bone view
     * @since 1.0.0
     */
    BoneView createBone(ModelView view, Bone bone);

    /**
     * Shows the given {@link ModelView} to
     * the specified {@link Player} instance, but
     * does not update the internal viewers list
     * 
     * <p>This method should be called by
     * {@link ModelView#addViewer}, which
     * should take care of its internal viewers list</p>
     *
     * @param view The shown model view
     * @param player The viewer player
     */
    void show(ModelView view, Player player);

    /**
     * Hides/destroys the given {@link ModelView}
     * for the specified {@link Player} instance, but
     * does not update the internal viewers list
     *
     * <p>This method should be called by
     * {@link ModelView#removeViewer}, which
     * should take care of the internal viewers list</p>
     *
     * @param view The hidden model view
     * @param player The viewer player
     */
    void hide(ModelView view, Player player);

}