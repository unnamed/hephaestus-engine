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
package team.unnamed.hephaestus.animation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.hephaestus.Bone;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.view.BaseBoneView;
import team.unnamed.hephaestus.view.MockBoneView;
import team.unnamed.hephaestus.view.MockModelView;
import team.unnamed.hephaestus.view.animation.AnimationController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AnimationControllerTest {

    private static final int LENGTH = 100;

    private static final Vector3Float START = Vector3Float.ZERO;
    private static final Vector3Float END = Vector3Float.ONE;

    private static final String BONE_NAME = "bone";

    private AnimationController controller;
    private MockBoneView bone;

    @BeforeEach
    public void clear() {
        Map<String, Bone> bones = new HashMap<>();
        Map<String, BaseBoneView> views = new HashMap<>();

        Bone baseBone = new Bone(
                BONE_NAME,
                Vector3Float.ZERO,
                Vector3Float.ZERO,
                Collections.emptyMap(),
                false, 0
        );
        bone = new MockBoneView(baseBone);

        bones.put(baseBone.name(), baseBone);
        views.put(baseBone.name(), bone);

        controller = AnimationController.nonDelayed(new MockModelView(
                new Model("test", bones, Vector2Float.ZERO, null, Collections.emptyMap()),
                controller,
                views
        ));
    }

//    @Test
//    public void test_loop_loop_mode() {
//        queue(Animation.LoopMode.LOOP);
//
//        // consumes all
//        consumeAll();
//        check(END);
//
//        // starts again
//        consume();
//        check(START);
//    }

//    @Test
//    public void test_once_loop_mode() {
//        queue(Animation.LoopMode.ONCE);
//
//        // finishes
//        consumeAll();
//        check(END);
//
//        // extra
//        consume();
//        check(bone.bone().position());
//    }

    @Test
    public void test_hold_loop_mode() {
        queue(Animation.LoopMode.HOLD);

        // consumes
        consumeAll();
        check(END);

        // should still be "END"
        consume();
        check(END);
    }

    private void queue(Animation.LoopMode loopMode) {
        Timeline timeline = Timeline.dynamic();
        timeline.put(0, Timeline.Channel.POSITION, START);
        timeline.put(LENGTH, Timeline.Channel.POSITION, END);

        Map<String, Timeline> timelines = new HashMap<>();
        timelines.put("bone", timeline);
        controller.queue(new Animation("test", loopMode, timelines));
    }

    private void consume() {
        controller.tick(0);
    }

    private void consumeAll() {
        for (int i = 0; i <= LENGTH; i++) {
            consume();
        }
    }

    private void check(Vector3Float expectedPosition) {
        Assertions.assertEquals(expectedPosition, bone.position());
    }

}
