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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Small implementation of a priority linked list.
 *
 * <p>In this collection, no elements with the same
 * priority can be added, if an element with the same
 * priority is added, the old element is replaced by
 * the new element.</p>
 *
 * <p>Also note that the elements have a priority,
 * higher priority elements are added at the start
 * of the list, and lower priority elements are
 * added at the end of the list.</p>
 *
 * @author yusshu (Andre Roldan)
 */
class PriorityLinkedList<E> implements Iterable<E> {
    private Node<E> head;

    @Nullable E add(final int priority, final Function<@Nullable E, @NotNull E> provider) {
        requireNonNull(provider, "provider");

        if (head == null) {
            // first element
            head = new Node<>(provider.apply(null), priority);
            return null;
        }

        final int headPriority = head.priority;

        if (headPriority == priority) {
            // same priority, old head is replaced by new head
            final E removed = head.value;
            head.value = provider.apply(removed);
            return removed;
        }

        if (headPriority < priority) {
            // new element turns into the new head,
            // the old head is the next node, nothing
            // is removed
            final Node<E> oldHead = head;
            head = new Node<>(provider.apply(null), priority);
            head.next = oldHead;
            return null;
        }

        Node<E> previous = head;
        Node<E> current = head.next;
        while (true) {
            if (current == null) {
                // reached the end of the stack,
                // add the element as the last
                // node
                previous.next = new Node<>(provider.apply(null), priority);
                return null;
            }

            final int currentPriority = current.priority;
            if (currentPriority == priority) {
                // same priority, replace current
                // node
                final E removed = current.value;
                current.value = provider.apply(removed);
                return removed;
            }

            if (currentPriority < priority) {
                // new animation is added between
                // previous and current
                previous.next = new Node<>(provider.apply(null), priority);
                previous.next.next = current;
                return null;
            }

            // move to next node
            previous = current;
            current = current.next;
        }
    }

    @Nullable E add(final @NotNull E value, int priority) {
        return add(priority, old -> value);
    }

    @Nullable E removeFirstMatching(final @NotNull Predicate<? super E> predicate) {
        requireNonNull(predicate, "predicate");

        if (head == null) {
            // empty, no need to check anything
            return null;
        }

        if (predicate.test(head.value)) {
            // removes head
            final E removed = head.value;
            head = head.next;
            return removed;
        }

        Node<E> previous = head;
        Node<E> current = head.next;
        while (current != null) {
            if (predicate.test(current.value)) {
                // removes current node
                final E removed = current.value;
                previous.next = current.next;
                return removed;
            }

            // move to next node
            previous = current;
            current = current.next;
        }

        // didnt match anything
        return null;
    }

    @Nullable E remove(final @NotNull E value) {
        return removeFirstMatching(e -> e.equals(value));
    }

    boolean isEmpty() {
        return head == null;
    }

    void clear() {
        head = null;
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return new Iterator<>() {
            Node<E> next = head;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public E next() {
                if (next == null) {
                    throw new NoSuchElementException();
                }
                final E value = next.value;
                next = next.next;
                return value;
            }
        };
    }

    @Override
    public @NotNull String toString() {
        final var builder = new StringBuilder(64);
        builder.append("PriorityStack[");
        Node<E> current = head;
        while (current != null) {
            builder.append(current.value);
            if (current.next != null) {
                builder.append(", ");
            }
            current = current.next;
        }
        builder.append(']');
        return builder.toString();
    }

    private static class Node<E> {
        E value;
        int priority;
        Node<E> next;

        Node(E value, int priority) {
            this.value = value;
            this.priority = priority;
        }
    }
}
