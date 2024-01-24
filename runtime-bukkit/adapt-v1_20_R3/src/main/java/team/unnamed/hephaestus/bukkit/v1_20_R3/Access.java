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
package team.unnamed.hephaestus.bukkit.v1_20_R3;

import java.lang.reflect.Field;

final class Access {

    public static class FieldReflect<T> {

        private final Field field;

        public FieldReflect(Field field) {
            this.field = field;
        }

        @SuppressWarnings("unchecked")
        public T get(Object object) {
            try {
                return (T) field.get(object);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        public void set(Object object, T value) {
            try {
                field.set(object, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

    }

    public static <T> FieldReflect<T> findFieldByType(Class<?> clazz, Class<? super T> type) {
        Field value = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType() == type) {
                value = field;
                field.setAccessible(true);
            }
        }
        if (value == null) {
            throw new IllegalStateException("Field with type " + type + " not found in " + clazz);
        }
        return new FieldReflect<>(value);
    }

}
