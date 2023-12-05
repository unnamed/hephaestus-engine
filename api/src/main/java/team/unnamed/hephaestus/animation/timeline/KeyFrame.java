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
package team.unnamed.hephaestus.animation.timeline;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.animation.interpolation.Interpolator;
import team.unnamed.hephaestus.animation.interpolation.KeyFrameInterpolator;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class KeyFrame<T> implements Comparable<KeyFrame<T>> {

    private final int time;
    private final T value;
    private final KeyFrameInterpolator<T> interpolator;
    private final Map<Class<? extends KeyFrameAttachment>, KeyFrameAttachment> attachments = new HashMap<>();

    public KeyFrame(int time, T value, @Nullable KeyFrameInterpolator<T> interpolator) {
        this.time = time;
        this.value = value;
        this.interpolator = interpolator;
    }

    public int time() {
        return time;
    }

    public T value() {
        return value;
    }

    public @Nullable KeyFrameInterpolator<T> interpolator() {
        return interpolator;
    }

    public KeyFrameInterpolator<T> interpolatorOr(KeyFrameInterpolator<T> fallback) {
        return interpolator == null ? fallback : interpolator;
    }

    /**
     * Adds an attachment to this keyframe.
     *
     * @param type The attachment type
     * @param attachment The attachment
     * @param <TAttachment> The attachment type
     * @since 1.0.0
     */
    public <TAttachment extends KeyFrameAttachment> void attachment(final @NotNull Class<TAttachment> type, final @NotNull TAttachment attachment) {
        requireNonNull(type, "type");
        requireNonNull(attachment, "attachment");
        attachments.put(type, attachment);
    }

    /**
     * Gets the attachment of the given type, or null if
     * there is no attachment of that type.
     *
     * @param type The attachment type
     * @return The attachment of the given type, or null if
     * there is no attachment of that type.
     * @param <TAttachment> The attachment type
     * @since 1.0.0
     */
    public <TAttachment extends KeyFrameAttachment> @Nullable TAttachment attachment(final @NotNull Class<TAttachment> type) {
        requireNonNull(type, "type");
        return type.cast(attachments.get(type));
    }

    @Override
    public String toString() {
        return "KeyFrame{" +
                "time=" + time +
                ", value=" + value +
                ", interpolator=" + interpolator +
                '}';
    }

    @Override
    public int compareTo(@NotNull KeyFrame<T> o) {
        return Integer.compare(time, o.time);
    }

}
