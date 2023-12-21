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
package team.unnamed.hephaestus.bukkit.v1_20_R2;

import net.kyori.adventure.sound.Sound;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftMob;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.controller.AnimationPlayer;
import team.unnamed.hephaestus.bukkit.ModelEntity;
import team.unnamed.hephaestus.view.BaseBoneView;

import java.util.Collection;
import java.util.HashSet;

/**
 * The implementation of the Bukkit-based {@link ModelEntity}
 * interface, and adapter for {@code net.minecraft.server}-based
 * {@link MinecraftModelEntity} class
 */
public class CraftModelEntity
        extends CraftMob
        implements ModelEntity {

    public CraftModelEntity(CraftServer server, MinecraftModelEntity entity) {
        super(server, entity);
    }

    @Override
    public MinecraftModelEntity getHandle() {
        return (MinecraftModelEntity) super.getHandle();
    }

    @Override
    public Model model() {
        return getHandle().model();
    }

    @Override
    public void playSound(final @NotNull Sound sound) {
        super.playSound(sound);
    }

    @Override
    public Collection<Player> viewers() {
        final var viewers = new HashSet<Player>();
        for (final var connection : getHandle().tracker.seenBy) {
            viewers.add(connection.getPlayer().getBukkitEntity());
        }
        return viewers;
    }

    @Override
    public boolean addViewer(Player player) {
        getHandle().tracker.updatePlayer(((CraftPlayer) player).getHandle());
        return true;
    }

    @Override
    public boolean removeViewer(Player player) {
        getHandle().tracker.removePlayer(((CraftPlayer) player).getHandle());
        return true;
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
    public AnimationPlayer animationController() {
        return getHandle().animationController();
    }

    @Override
    public void tickAnimations() {
        getHandle().animationController().tick(
                getHandle().getYHeadRot(),
                0
        );
    }

    @Override
    public String toString() {
        return "CraftModelEntity { model='" + model().name() + "' }";
    }
}
