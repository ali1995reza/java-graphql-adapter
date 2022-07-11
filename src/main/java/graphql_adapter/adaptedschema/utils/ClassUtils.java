/*
 * Copyright 2022 Alireza Akhoundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package graphql_adapter.adaptedschema.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassUtils {

    @SafeVarargs
    public static void addPairTypesWithSameValue(Map<Class<?>, ?>... maps) {
        for (Map<Class<?>, ?> map : maps) {
            for (Class<?> clazz : new ArrayList<>(map.keySet())) {
                Class<?> pairType = getPairType(clazz);
                if (pairType == null) {
                    continue;
                }
                if (CollectionUtils.containsKey(pairType, maps)) {
                    continue;
                }
                map.put(pairType, cast(map.get(clazz)));
            }
        }
    }

    public static void addPairTypesWithSameValue(Map<Class<?>, ?> map) {
        for (Class<?> clazz : new ArrayList<>(map.keySet())) {
            Class<?> pairType = getPairType(clazz);
            if (pairType == null) {
                continue;
            }
            if (map.containsKey(pairType)) {
                continue;
            }
            map.put(pairType, cast(map.get(clazz)));
        }
    }

    public static <T> T cast(Object o) {
        return (T) o;
    }

    public static <T> T cast(Object o, Class<T> clazz) {
        return (T) o;
    }

    public static List<Method> getAllMethods(Class<?> cls) {
        Method[] methods = cls.getMethods();
        Method[] declaredMethods = cls.getDeclaredMethods();
        List<Method> list = new ArrayList<>();

        for (Method method : methods) {
            if (!method.isSynthetic())
                list.add(method);
        }

        for (Method method : declaredMethods) {
            if (!list.contains(method) && !method.isSynthetic())
                list.add(method);
        }

        return list;
    }

    public static Class<?> getPairType(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return ClassUtils.toNonePrimitive(clazz);
        } else {
            return ClassUtils.toPrimitive(clazz);
        }
    }

    public static boolean isOneOf(Class<?> clazz, Class<?>... others) {
        for (Class<?> other : others) {
            if (clazz == other) {
                return true;
            }
        }
        return false;
    }

    public static Class<?> toNonePrimitive(Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            return clazz;
        }

        if (clazz == int.class) {
            return Integer.class;
        }

        if (clazz == long.class) {
            return Long.class;
        }

        if (clazz == boolean.class) {
            return Boolean.class;
        }

        if (clazz == float.class) {
            return Float.class;
        }

        if (clazz == double.class) {
            return Double.class;
        }

        if (clazz == char.class) {
            return Character.class;
        }

        if (clazz == byte.class) {
            return Byte.class;
        }

        if (clazz == short.class) {
            return Short.class;
        }

        return null;
    }

    public static Class<?> toPrimitive(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return clazz;
        }

        if (clazz == Integer.class) {
            return int.class;
        }

        if (clazz == Long.class) {
            return long.class;
        }

        if (clazz == Boolean.class) {
            return boolean.class;
        }

        if (clazz == Float.class) {
            return float.class;
        }

        if (clazz == Double.class) {
            return double.class;
        }

        if (clazz == Character.class) {
            return char.class;
        }

        if (clazz == Byte.class) {
            return byte.class;
        }

        if (clazz == Short.class) {
            return short.class;
        }

        return null;
    }
}
