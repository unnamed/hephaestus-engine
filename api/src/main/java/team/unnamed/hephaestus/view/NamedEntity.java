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
package team.unnamed.hephaestus.view;

import net.kyori.adventure.text.Component;

public interface NamedEntity {

    /**
     * Sets the entity custom name
     *
     * @param customName The entity custom name
     * @since 1.0.0
     */
    void customName(Component customName);

    /**
     * Returns the entity custom name
     *
     * @return The entity display name
     * @since 1.0.0
     */
    Component customName();

    /**
     * Sets the entity custom name visibility,
     * true to make it visible
     *
     * @param visible True if the entity custom
     *                name should be visible
     * @since 1.0.0
     */
    void customNameVisible(boolean visible);

    /**
     * Determines whether the entity custom name
     * is visible or not
     *
     * @return True if the entity custom name is visible
     * @since 1.0.0
     */
    boolean customNameVisible();

}
