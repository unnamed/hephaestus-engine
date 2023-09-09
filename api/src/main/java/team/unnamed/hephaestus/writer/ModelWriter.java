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
package team.unnamed.hephaestus.writer;

import team.unnamed.creative.ResourcePack;
import team.unnamed.hephaestus.Model;

import java.util.Collection;

/**
 * Responsible for writing {@link Model} instances
 * to a given {@code T} output type
 *
 * @since 1.0.0
 * @param <T> The target type
 */
@FunctionalInterface
public interface ModelWriter<T> {

    /**
     * Writes a {@link Model} to the given {@code target}
     *
     * @param target The target output type where the
     *               model will be written
     * @since 1.0.0
     */
    void write(T target, Collection<Model> models);

    /**
     * Creates a new {@link ModelWriter} instance that
     * writes {@link Model} instances to a final resource
     * pack represented by {@link ResourcePack}
     *
     * @param namespace The models namespace
     * @return The created {@link ModelWriter} instance
     * @since 1.0.0
     */
    static ModelWriter<ResourcePack> resource(String namespace) {
        return new ResourceModelWriter(namespace);
    }

    /**
     * Creates a new {@link ModelWriter} instance that
     * writes {@link Model} instances to a final resource
     * pack represented by {@link ResourcePack}
     *
     * @return The created {@link ModelWriter} instance
     * @since 1.0.0
     */
    static ModelWriter<ResourcePack> resource() {
        return new ResourceModelWriter();
    }

}
