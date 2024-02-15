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
package team.unnamed.hephaestus.minestom;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.ModelEngine;
import team.unnamed.hephaestus.view.BaseModelView;
import team.unnamed.hephaestus.view.track.ModelViewTracker;

public class MinestomModelEngine implements ModelEngine<Player, MinestomLocation>  {

    public ModelEntity createView(EntityType entityType, Model model, Instance world, Pos position, float scale) {
        ModelEntity modelEntity = new ModelEntity(entityType, model, scale);
        modelEntity.setInstance(world, position);
        return modelEntity;
    }

    public ModelEntity createView(Model model, Instance instance, Pos position, float scale) {
        return createView(EntityType.ARMOR_STAND, model, instance, position, scale);
    }

    public ModelEntity createView(Model model, Instance instance, Pos position) {
        return createView(model, instance, position, 1);
    }

    public ModelEntity createViewAndTrack(Model model, Instance instance, Pos position, float scale) {
        ModelEntity view = createView(model, instance, position, scale);
        tracker().startGlobalTracking(view);
        return view;
    }

    @Override
    public BaseModelView<Player> createView(Model model, MinestomLocation location) {
        return createView(model, location.instance(), location.position());
    }

    @Override
    public ModelEntity createViewAndTrack(Model model, MinestomLocation location) {
        return createViewAndTrack(model, location.instance(), location.position(), 1);
    }

    @Override
    public ModelViewTracker<Player> tracker() {
        return MinestomModelViewTracker.INSTANCE;
    }

    public static MinestomModelEngine minestom() {
        return new MinestomModelEngine();
    }

}
