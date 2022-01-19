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
package team.unnamed.hephaestus.animation;

import team.unnamed.hephaestus.ModelBone;

import java.util.Map;
import java.util.Objects;

/**
 * Class that represents a model animation,
 * applied to the {@link ModelBone} objects
 */
public class ModelAnimation {

    private final String name;

    /** Determines if the animation is infinite */
    private final boolean loop;

    /** Determines the animation length in ticks */
    private final int animationLength;

    /** Contains all the bone animations using the bone names as key */
    private final Map<String, KeyFrameList> framesByBone;

    private final Map<String, Map<Integer, Integer>> modelData;

    public ModelAnimation(
            String name,
            boolean loop,
            int animationLength,
            Map<String, KeyFrameList> framesByBone,
            Map<String, Map<Integer, Integer>> modelData
    ) {
        this.name = name;
        this.loop = loop;
        this.animationLength = animationLength;
        this.framesByBone = framesByBone;
        this.modelData = modelData;
    }

    public Map<String, Map<Integer, Integer>> getModelData() {
        return modelData;
    }

    public String getName() {
        return name;
    }

    public boolean isLoop() {
        return loop;
    }

    public int getAnimationLength() {
        return animationLength;
    }

    public Map<String, KeyFrameList> getAnimationsByBoneName() {
        return framesByBone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelAnimation that = (ModelAnimation) o;
        return loop == that.loop
                && Float.compare(that.animationLength, animationLength) == 0
                && Objects.equals(framesByBone, that.framesByBone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loop, animationLength, framesByBone);
    }

    @Override
    public String toString() {
        return "ModelAnimation{" +
                "loop=" + loop +
                ", animationLength=" + animationLength +
                ", framesByBone=" + framesByBone +
                '}';
    }
}