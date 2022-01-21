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
package team.unnamed.hephaestus.reader;

import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.ModelDataCursor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Responsible for reading and parsing {@link Model} from a
 * sequence of bytes, structure of models depend on
 * implementation
 *
 * @since 1.0.0
 */
@FunctionalInterface
public interface ModelReader {

    /**
     * Reads a model from the given {@code reader}
     *
     * @param input The byte input stream used to read
     *              and parse the {@link Model} instance
     * @return The parsed model
     * @throws IOException If parsing fails
     * @since 1.0.0
     */
    Model read(InputStream input) throws IOException;

    /**
     * Creates a new instance of the implementation of this
     * interface to create {@link Model} instances from
     * <a href="https://blockbench.net">Blockbench</a>'s
     * <b>.bbmodel</b> files, which are just JSON files with a
     * special structure
     *
     * @since 1.0.0
     */
    static ModelReader blockbench() {
        return new BBModelReader();
    }

    /**
     * Creates a new instance of the implementation of this
     * interface to create {@link Model} instances from
     * <a href="https://blockbench.net">Blockbench</a>'s
     * <b>.bbmodel</b> files, which are just JSON files with a
     * special structure
     *
     * <p>Similar to {@link ModelReader#blockbench()} but
     * a specific {@link ModelDataCursor} can be used</p>
     *
     * @param cursor The custom model data cursor reference
     * @since 1.0.0
     */
    static ModelReader blockbench(ModelDataCursor cursor) {
        return new BBModelReader(cursor);
    }

}