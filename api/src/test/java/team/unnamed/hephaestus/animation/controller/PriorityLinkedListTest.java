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
package team.unnamed.hephaestus.animation.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PriorityLinkedListTest {
    @Test
    void test_add() {
        final var list = new PriorityLinkedList<String>();

        final var obj1 = "1";
        final var obj2 = "2";
        final var obj3 = "3";
        final var obj4 = "4";
        final var obj5 = "5";

        final var obj11 = "new 1";
        final var obj22 = "new 2";
        final var obj33 = "new 3";
        final var obj44 = "new 4";

        // initial add
        list.add(obj2, 2);
        assertEquals("PriorityStack[2]", list.toString());

        // adding at the start
        list.add(obj3, 3);
        assertEquals("PriorityStack[3, 2]", list.toString());

        // adding at the end
        list.add(obj1, 1);
        assertEquals("PriorityStack[3, 2, 1]", list.toString());

        // adding at the start
        list.add(obj4, 4);
        assertEquals("PriorityStack[4, 3, 2, 1]", list.toString());

        // replacing at the last
        assertSame(obj1, list.add(obj11, 1));
        assertEquals("PriorityStack[4, 3, 2, new 1]", list.toString());

        assertSame(obj11, list.add(obj1, 1));
        assertEquals("PriorityStack[4, 3, 2, 1]", list.toString());

        // replacing at the start
        assertSame(obj4, list.add(obj44, 4));
        assertEquals("PriorityStack[new 4, 3, 2, 1]", list.toString());

        assertSame(obj44, list.add(obj4, 4));
        assertEquals("PriorityStack[4, 3, 2, 1]", list.toString());

        // replacing at the middle
        assertSame(obj2, list.add(obj22, 2));
        assertEquals("PriorityStack[4, 3, new 2, 1]", list.toString());

        assertSame(obj22, list.add(obj2, 2));
        assertEquals("PriorityStack[4, 3, 2, 1]", list.toString());

        assertSame(obj3, list.add(obj33, 3));
        assertEquals("PriorityStack[4, new 3, 2, 1]", list.toString());

        assertSame(obj33, list.add(obj3, 3));
        assertEquals("PriorityStack[4, 3, 2, 1]", list.toString());

        // adding at the start
        list.add(obj5, 5);
        assertEquals("PriorityStack[5, 4, 3, 2, 1]", list.toString());
    }

    @Test
    void test_remove() {
        final var list = new PriorityLinkedList<String>();

        final var obj1 = "1";
        final var obj2 = "2";
        final var obj3 = "3";
        final var obj4 = "4";
        final var obj5 = "5";

        list.add(obj1, 1);
        list.add(obj2, 2);
        list.add(obj3, 3);
        list.add(obj4, 4);
        list.add(obj5, 5);

        // removing at the start
        assertSame(obj5, list.remove("5"));
        assertEquals("PriorityStack[4, 3, 2, 1]", list.toString());

        // removing at the end
        assertSame(obj1, list.remove("1"));
        assertEquals("PriorityStack[4, 3, 2]", list.toString());

        assertSame(obj3, list.remove("3"));
        assertEquals("PriorityStack[4, 2]", list.toString());

        assertSame(obj2, list.remove("2"));
        assertEquals("PriorityStack[4]", list.toString());

        // remove single
        assertSame(obj4, list.remove("4"));
        assertEquals("PriorityStack[]", list.toString());

        // removing non existent
        assertSame(null, list.remove("4"));
        assertEquals("PriorityStack[]", list.toString());
    }
}
