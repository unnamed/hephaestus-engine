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
import net.minestom.server.instance.Instance;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.util.Validate;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.ModelEngine;

public class MinestomModelEngine implements ModelEngine {

    private MinestomModelEngine() {
    }

    public ModelEntity spawn(EntityType entityType, Model model, BoneType boneType) {
        return new ModelEntity(entityType, model, boneType);
    }

    public ModelEntity spawn(EntityType entityType, Model model, BoneType boneType, Pos position, Instance world) {
        ModelEntity modelEntity = new ModelEntity(entityType, model, boneType);
        modelEntity.setInstance(world, position);
        return modelEntity;
    }

    @Override
    public ModelEntity spawn(Model model, Vector3Float position, float yaw, float pitch, Object world) {
        Validate.isTrue(world instanceof Instance);
        return spawn(
                EntityType.ARMOR_STAND,
                model,
                BoneType.ARMOR_STAND,
                new Pos(position.x(), position.y(), position.z(), yaw, pitch),
                (Instance) world
        );
    }

    public enum BoneType {
        ARMOR_STAND,
        AREA_EFFECT_CLOUD
    }

    public static MinestomModelEngine minestom() {
        return new MinestomModelEngine();
    }

}