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
package team.unnamed.hephaestus.bukkit;

import org.bukkit.Location;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.view.BaseModelView;

/**
 * Responsible for spawning {@link Model}, or
 * converting {@link Model} to {@link BaseModelView}
 * concrete instances at specific world locations
 *
 * @since 1.0.0
 */
public interface ModelViewRenderer {

    /**
     * Spawns the given {@link Model} model
     * instance at the given world location
     *
     * @param model    The rendered model
     * @param location The model view location
     * @param options  The model view rendering options
     * @return The created model view
     * @since 1.0.0
     */
    ModelView render(Model model, Location location, ModelViewOptions options);

    /**
     * Spawns a {@link Model} instance at the
     * given location
     *
     * <p>Invoking this method is the same as invoking
     * {@link ModelViewRenderer#render(Model, Location, ModelViewOptions)}
     * with the default options</p>
     *
     * @param model The rendered model
     * @param location The model view location
     * @return The created model view
     * @since 1.0.0
     */
    default ModelView render(Model model, Location location) {
        return render(model, location, ModelViewOptions.DEFAULT);
    }

}