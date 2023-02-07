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
package team.unnamed.hephaestus.view;

import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.AnimationController;

import java.util.Collection;
import java.util.Map;

public class MockModelView implements BaseModelView {

    private final Model model;
    private final AnimationController animationController;
    private final Map<String, BaseBoneView> bones;

    public MockModelView(
            Model model,
            AnimationController animationController,
            Map<String, BaseBoneView> bones
    ) {
        this.model = model;
        this.animationController = animationController;
        this.bones = bones;
    }

    @Override
    public Model model() {
        return model;
    }

    @Override
    public Collection<? extends BaseBoneView> bones() {
        return bones.values();
    }

    @Override
    public @Nullable BaseBoneView bone(String name) {
        return bones.get(name);
    }

    @Override
    public AnimationController animationController() {
        return animationController;
    }

    @Override
    public void tickAnimations() {
        animationController.tick(0);
    }

}
