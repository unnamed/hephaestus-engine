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

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class Minecraft {

    public static final float PLAYER_CREATIVE_PICK_RANGE = 5.0F;
    public static final float PLAYER_DEFAULT_PICK_RANGE = 4.5F;

    public static final String CUSTOM_MODEL_DATA_TAG = "minecraft:custom_model_data";
    public static final String COLOR_TAG = "minecraft:dyed_color";
    public static final String PROFILE_TAG = "minecraft:profile";

    //Skin
    public static final String PROPERTIES_TAG = "properties";
    public static final String SIGNATURE_TAG = "signature";
    public static final String VALUE_TAG = "value";
    public static final String PROPERTY_NAME_TAG = "name";
    public static final String PROPERTY_NAME_VALUE = "textures";

    private Minecraft() {
    }

}
