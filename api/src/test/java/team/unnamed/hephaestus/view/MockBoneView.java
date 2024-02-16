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

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.util.Quaternion;

import java.awt.*;

public class MockBoneView implements BaseBoneView {

    private final Bone bone;
    private int color = BaseBoneView.DEFAULT_COLOR;
    private Vector3Float position;
    private Vector3Float rotation;
    private Component customName;
    private boolean customNameVisible;

    public MockBoneView(Bone bone) {
        this.bone = bone;
    }

    @Override
    public Bone bone() {
        return bone;
    }

    @Override
    public void colorize(int r, int g, int b) {
        this.color = new Color(r, g, b).getRGB();
    }

    @Override
    public void colorize(int rgb) {
        this.color = rgb;
    }

    @Override
    public void update(final @NotNull Vector3Float position, final @NotNull Quaternion rotation, final @NotNull Vector3Float scale) {
        this.position = position;
        this.rotation = rotation.toEulerDegrees();
    }

    public int color() {
        return color;
    }

    public Vector3Float position() {
        return position;
    }

    public Vector3Float rotation() {
        return rotation;
    }
}
