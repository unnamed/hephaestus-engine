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

/**
 * hephaestus-engine!
 *
 * <p>This class holds some constants specific to the
 * hephaestus-engine library such as the hephaestus-engine
 * namespace.</p>
 *
 * <p>This class can be used to ensure the presence of the
 * library in the runtime classpath.</p>
 *
 * @since 1.0.0
 */
public final class Hephaestus {

    /**
     * The hephaestus-engine namespace.
     *
     * <p>This namespace should be used by default if the
     * developer (user of this library) doesn't specify
     * one.</p>
     *
     * @since 1.0.0
     */
    public static final String NAMESPACE = "hephaestus";

    public static final Key BONE_ITEM_KEY = Key.key("minecraft", "leather_horse_armor");

    // maybe hold a version?

    private Hephaestus() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
