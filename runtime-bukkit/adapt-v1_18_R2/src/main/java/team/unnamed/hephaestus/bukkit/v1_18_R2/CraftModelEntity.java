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
package team.unnamed.hephaestus.bukkit.v1_18_R2;

import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftMob;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.AnimationController;
import team.unnamed.hephaestus.bukkit.ModelEntity;
import team.unnamed.hephaestus.view.BaseBoneView;

import java.util.Collection;

/**
 * The implementation of the Bukkit-based {@link ModelEntity}
 * interface, and adapter for {@code net.minecraft.server}-based
 * {@link ModelEntityImpl} class
 */
public class CraftModelEntity
        extends CraftMob
        implements ModelEntity {

    public CraftModelEntity(CraftServer server, ModelEntityImpl entity) {
        super(server, entity);
    }

    @Override
    public ModelEntityImpl getHandle() {
        return (ModelEntityImpl) super.getHandle();
    }

    @Override
    public EntityType getType() {
        // TODO: Create a custom EntityType when it stops being an enum
        return EntityType.UNKNOWN;
    }

    @Override
    public Model model() {
        return getHandle().model();
    }

    @Override
    public Collection<? extends BaseBoneView> bones() {
        return getHandle().bones().values();
    }

    @Override
    public @Nullable BaseBoneView bone(String name) {
        return getHandle().bones().get(name);
    }

    @Override
    public AnimationController animationController() {
        return getHandle().animationController();
    }

    @Override
    public void tickAnimations() {
    }

    @Override
    public String toString() {
        return "CraftModelEntity { model='" + model().name() + "' }";
    }

}
