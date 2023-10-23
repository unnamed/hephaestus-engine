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

import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Writable;

import static java.util.Objects.requireNonNull;

/**
 * Represents a texture asset, which is a texture
 * with an id and a name.
 *
 * <p>Texture assets are loaded along with the
 * {@link ModelAsset} and they represent a texture
 * that will be written in the generated resource-pack.</p>
 *
 * @since 1.0.0
 */
public final class TextureAsset {

    private final String id;
    private final String name;
    private final Writable data;

    private TextureAsset(
            final @NotNull String id,
            @Subst("texture.png") @Pattern("[a-z0-9_\\-./]+") final @NotNull String name,
            final @NotNull Writable data
    ) {
        this.id = requireNonNull(id, "id");
        this.name = requireNonNull(name, "name");
        this.data = requireNonNull(data, "data");

        // validate the name, will throw InvalidKeyException if invalid
        Key.key(Key.MINECRAFT_NAMESPACE, name);
    }

    /**
     * Returns the id of this texture.
     *
     * <p>Texture IDs are are used by element
     * faces to reference actual textures.</p>
     *
     * @return The id of this texture
     * @since 1.0.0
     */
    public @NotNull String id() {
        return id;
    }

    /**
     * Returns the name of this texture.
     *
     * <p>The names are used to determine the
     * file name or key of the texture to be
     * written in the resource-pack. For example:
     * {@code texture.png}, {@code left_arm.png},
     * etc.</p>
     *
     * <p>It is ensured that the return value is
     * a valid key value. ({@link Key#value()})</p>
     *
     * @return The name of this texture
     * @since 1.0.0
     */
    @Subst("texture.png")
    public @NotNull String name() {
        return name;
    }

    /**
     * Returns the actual PNG texture data as
     * a {@link Writable} instance.
     *
     * @return The texture data
     * @since 1.0.0
     */
    public @NotNull Writable data() {
        return data;
    }

    /**
     * Creates a new {@link TextureAsset} instance.
     *
     * @param id   The texture ID
     * @param name The texture name
     * @param data The texture data
     * @return A new texture asset
     * @throws net.kyori.adventure.key.InvalidKeyException If the
     * name doesn't match the key value pattern
     * @since 1.0.0
     */
    public static @NotNull TextureAsset textureAsset(
            final @NotNull String id,
            @Subst("texture.png") @Pattern("[a-z0-9_\\-./]+") final @NotNull String name,
            final @NotNull Writable data
    ) {
        return new TextureAsset(id, name, data);
    }
}
