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
package team.unnamed.hephaestus;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class Minecraft {

    public static final float PLAYER_CREATIVE_PICK_RANGE = 5.0F;
    public static final float PLAYER_DEFAULT_PICK_RANGE = 4.5F;

    public static final Key PLAYER_HEAD_ITEM_KEY = Key.key("minecraft", "player_head");

    public static final String DISPLAY_TAG = "display";
    public static final String CUSTOM_MODEL_DATA_TAG = "CustomModelData";
    public static final String COLOR_TAG = "color";
    public static final String SKULL_OWNER_TAG = "SkullOwner";

    private Minecraft() {
    }

}
