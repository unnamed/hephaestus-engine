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
package team.unnamed.hephaestus.minestomce.entity;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.goal.FollowTargetGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.minestomce.ModelEntity;

import java.time.Duration;
import java.util.List;

public class RedstoneMonstrosity extends ModelEntity {
    private final Animation idleAnimation;
    private final Animation walkAnimation;
    private Animation currentAnimation;

    public RedstoneMonstrosity(final @NotNull Model model) {
        super(EntityType.PIG, model, 1F);

        addAIGroup(
                List.of(
                        new FollowTargetGoal(this, Duration.of(5, TimeUnit.CLIENT_TICK))
                ),
                List.of(
                        new ClosestEntityTarget(this, 15D, entity -> entity instanceof Player)
                )
        );
        idleAnimation = model.animations().get("idle");
        walkAnimation = model.animations().get("walk");

        animationController().queue(idleAnimation);
        currentAnimation = idleAnimation;
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        if (previousPosition.samePoint(position)) {
            if (idleAnimation != currentAnimation) {
                System.out.println("now idle");
                animationController().queue(idleAnimation, 0);
                currentAnimation = idleAnimation;
            }
        } else {
            if (walkAnimation != currentAnimation) {
                System.out.println("now walking");
                animationController().queue(walkAnimation, 0);
                currentAnimation = walkAnimation;
            }
        }
    }
}
