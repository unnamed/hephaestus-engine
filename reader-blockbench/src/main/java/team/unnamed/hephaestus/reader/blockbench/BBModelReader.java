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
package team.unnamed.hephaestus.reader.blockbench;

import org.jetbrains.annotations.ApiStatus;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.ModelDataCursor;
import team.unnamed.hephaestus.reader.ModelReader;

/**
 * Represents a reader that reads {@link Model} instances
 * from <a href="https://blockbench.net">Blockbench</a>'s
 * <b>BBMODEL</b> format.
 *
 * @since 1.0.0
 */
@ApiStatus.NonExtendable
public interface BBModelReader extends ModelReader {

    /**
     * Creates a new {@link BBModelReader} instance.
     *
     * @return A new model reader
     * @since 1.0.0
     */
    static ModelReader blockbench() {
        return new BBModelReaderImpl(new ModelDataCursor(1));
    }

    /**
     * Creates a new {@link BBModelReader} instance with
     * the given model data cursor.
     *
     * <p>Model data cursor keeps track of the used custom
     * model data so we can use a unique custom model data
     * value for every model.</p>
     *
     * @param cursor The custom model data cursor reference
     * @return A new model reader
     * @since 1.0.0
     */
    static ModelReader blockbench(ModelDataCursor cursor) {
        return new BBModelReaderImpl(cursor);
    }

}