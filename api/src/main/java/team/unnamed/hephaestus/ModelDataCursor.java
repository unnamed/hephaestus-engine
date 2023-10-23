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

import org.jetbrains.annotations.NotNull;

/**
 * Object holding a {@code cursor} for using unique
 * custom model data for all bones
 */
public final class ModelDataCursor {

    private static final ModelDataCursor GLOBAL = new ModelDataCursor(1);

    // Represents the next custom model
    // data to be returned by next()
    private int cursor;

    public ModelDataCursor(int cursor) {
        this.cursor = cursor;
    }

    /**
     * Returns the next custom model data
     * without modifying it
     */
    public int getNext() {
        return cursor;
    }

    /**
     * Returns the next custom model data
     * and adds one to it for the next custom
     * model data
     */
    public int next() {
        return cursor++;
    }

    public static @NotNull ModelDataCursor global() {
        return GLOBAL;
    }
}
