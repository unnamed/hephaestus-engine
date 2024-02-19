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
package team.unnamed.hephaestus.reader;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Readable;
import team.unnamed.hephaestus.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
     * Reads a model from a {@link InputStream}, this
     * method <strong>should not close</strong> the
     * provided input stream
     *
     * @param input The byte input stream used to read
     *              and parse the {@link Model} instance
     * @return The parsed model
     * @throws ModelFormatException If parsing fails
     * @since 1.0.0
     */
    @NotNull Model read(final @NotNull InputStream input);

    /**
     * Reads a model from the given {@code readable}
     * instance
     *
     * @param readable The readable object
     * @return The read model
     * @throws ModelFormatException If parsing fails
     * @since 1.0.0
     */
    default @NotNull Model read(final @NotNull Readable readable) {
        try (final var input = readable.open()) {
            return read(input);
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to read model from readable: " + readable, e);
        }
    }

    /**
     * Reads a model from a {@link File}.
     *
     * @param file The file containing the model data
     * @return The parsed model
     * @throws ModelFormatException If parsing fails
     * @since 1.0.0
     */
    default @NotNull Model read(final @NotNull File file) {
        try (final var input = new FileInputStream(file)) {
            return read(input);
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to read model from file: " + file, e);
        }
    }

    /**
     * Reads a model from the given {@code path}.
     *
     * @param path The path to the file containing the model data
     * @return The parsed model
     * @throws ModelFormatException If parsing fails
     * @since 1.0.0
     */
    default @NotNull Model read(final @NotNull Path path) {
        try (final var input = Files.newInputStream(path)) {
            return read(input);
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to read model from path: " + path, e);
        }
    }
}