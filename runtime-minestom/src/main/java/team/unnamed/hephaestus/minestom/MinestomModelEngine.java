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

public final class MinestomModelEngine implements ModelEngine<Player, MinestomLocation> {

    private MinestomModelEngine() {
    }

    @Override
    public ModelViewTracker<Player> tracker() {
        return MinestomModelViewTracker.INSTANCE;
    }

    public ModelEntity createView(EntityType entityType, Model model, BoneType boneType) {
        return new ModelEntity(entityType, model, boneType);
    }

    public ModelEntity createView(Model model) {
        return new ModelEntity(EntityType.ARMOR_STAND, model, BoneType.ARMOR_STAND);
    }

    public ModelEntity createView(EntityType entityType, Model model, BoneType boneType, Instance world, Pos position) {
        ModelEntity modelEntity = new ModelEntity(entityType, model, boneType);
        modelEntity.setInstance(world, position);
        return modelEntity;
    }

    public ModelEntity createView(Model model, Instance instance, Pos position) {
        return createView(EntityType.ARMOR_STAND, model, BoneType.ARMOR_STAND, instance, position);
    }

    @Override
    public ModelEntity createView(Model model, MinestomLocation location) {
        return createView(model, location.instance(), location.position());
    }

    public ModelEntity createViewAndTrack(Model model, Instance instance, Pos position) {
        ModelEntity view = createView(model, instance, position);
        tracker().startGlobalTracking(view);
        return view;
    }

    @Override
    public ModelEntity createViewAndTrack(Model model, MinestomLocation location) {
        return createViewAndTrack(model, location.instance(), location.position());
    }

    public enum BoneType {
        ARMOR_STAND,
        AREA_EFFECT_CLOUD
    }

    public static MinestomModelEngine minestom() {
        return new MinestomModelEngine();
    }

}