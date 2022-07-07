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

package grphaqladapter.adaptedschema.utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils {

    private static <K, V, E> BiConsumer<Map<K, V>, E> nullSkipperAccumulator(Function<E, ? extends K> keyMapper, Function<E, ? extends V> valueMapper) {
        return (map, element) -> {
            K key = keyMapper.apply(element);
            V value = valueMapper.apply(element);
            if (key != null && value != null) {
                map.put(key, value);
            }
        };
    }

    private static <K, V, M extends Map<K, V>> BinaryOperator<M> uniqKeysMapMerger() {
        return (m1, m2) -> {
            for (Map.Entry<K, V> e : m2.entrySet()) {
                K k = e.getKey();
                V v = Objects.requireNonNull(e.getValue());
                V u = m1.putIfAbsent(k, v);
                if (u != null) throw new IllegalStateException("duplicate key :" + k + "values [" + u + "," + v + "]");
            }
            return m1;
        };
    }

    public static <T, K, V> Collector<T, ?, Map<K, V>> nullSkipperMapCollector(Function<T, ? extends K> keyMapper, Function<T, ? extends V> valueMapper) {
        return Collector.of(HashMap::new, nullSkipperAccumulator(keyMapper, valueMapper), uniqKeysMapMerger(), Collector.Characteristics.IDENTITY_FINISH);
    }

    public static <T, K, V> Collector<T, ?, Map<K, V>> toMap(Function<T, ? extends K> keyMapper, Function<T, ? extends V> valueMapper, boolean skipNullValues) {
        return skipNullValues ? Collector.of(HashMap::new, nullSkipperAccumulator(keyMapper, valueMapper), uniqKeysMapMerger(), Collector.Characteristics.IDENTITY_FINISH) :
                Collectors.toMap(keyMapper, valueMapper);
    }


    public static <T> List<T> combineToImmutableList(Collection<? extends T>... collections) {
        return Collections.unmodifiableList(combineToList(collections));
    }

    public static <T> List<T> combineToList(Collection<? extends T>... collections) {
        List<T> list = new ArrayList<>();
        for (Collection<? extends T> collection : collections) {
            list.addAll(collection);
        }
        return list;
    }

    public static <T, V extends T> Stream<V> separateToStream(Collection<T> collection, Class<V> valueClass) {
        return collection.stream().filter(object -> valueClass.isAssignableFrom(object.getClass()))
                .map(object -> (V) object);
    }

    public static <T, V extends T> List<V> separateToImmutableList(Collection<T> collection, Class<V> valueClass) {
        return Collections.unmodifiableList(separateToList(collection, valueClass));
    }

    public static <T, K, V extends T> Map<K, V> separateToImmutableMap(Collection<T> collection, Class<V> valueClass, Function<V, K> keyMapper, boolean skipNull) {
        return Collections.unmodifiableMap(separateToMap(collection, valueClass, keyMapper, skipNull));
    }

    public static <T, K, V extends T> Map<K, V> separateToImmutableMap(Collection<T> collection, Class<V> valueClass, Function<V, K> keyMapper) {
        return separateToImmutableMap(collection, valueClass, keyMapper, false);
    }

    public static <T, K, V extends T, NV> Map<K, NV> separateToImmutableMap(Collection<T> collection, Class<V> valueClass, Function<V, K> keyMapper, Function<V, NV> valueMapper, boolean skipNull) {
        return Collections.unmodifiableMap(separateToMap(collection, valueClass, keyMapper, valueMapper, skipNull));
    }

    public static <T, K, V extends T, NV> Map<K, NV> separateToImmutableMap(Collection<T> collection, Class<V> valueClass, Function<V, K> keyMapper, Function<V, NV> valueMapper) {
        return separateToImmutableMap(collection, valueClass, keyMapper, valueMapper, false);
    }

    public static <T, V extends T> List<V> separateToList(Collection<T> collection, Class<V> valueClass) {
        return separateToStream(collection, valueClass).collect(Collectors.toList());
    }

    public static <T, K, V extends T> Map<K, V> separateToMap(Collection<T> collection, Class<V> valueClass, Function<V, K> keyMapper, boolean skipNull) {
        return separateToMap(collection, valueClass, keyMapper, x -> x, skipNull);
    }

    public static <T, K, V extends T> Map<K, V> separateToMap(Collection<T> collection, Class<V> valueClass, Function<V, K> keyMapper) {
        return separateToMap(collection, valueClass, keyMapper, false);
    }

    public static <T, K, V extends T, NV> Map<K, NV> separateToMap(Collection<T> collection, Class<V> valueClass, Function<V, K> keyMapper, Function<V, NV> valueMapper, boolean skipNull) {
        return separateToStream(collection, valueClass).collect(toMap(
                keyMapper,
                valueMapper,
                skipNull)
        );
    }

    public static <T, K, V extends T, NV> Map<K, NV> separateToMap(Collection<T> collection, Class<V> valueClass, Function<V, K> keyMapper, Function<V, NV> valueMapper) {
        return separateToMap(collection, valueClass, keyMapper, valueMapper, false);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> void forEach(Collection<T> collection, Consumer<T> forEach, Consumer<T> forLast) {
        if (isEmpty(collection)) {
            return;
        }

        final int lastIndex = collection.size() - 1;
        int i = 0;

        for (T t : collection) {
            if (i++ == lastIndex) {
                forLast.accept(t);
            } else {
                forEach.accept(t);
            }
        }
    }

    public static <T> void forEach(T[] array, Consumer<T> forEach, Consumer<T> forLast) {
        if (isEmpty(array)) {
            return;
        }

        final int lastIndex = array.length - 1;
        int i = 0;

        for (T t : array) {
            if (i++ == lastIndex) {
                forLast.accept(t);
            } else {
                forEach.accept(t);
            }
        }
    }

    public static <T> void checkDuplicates(Consumer<T> handleDuplicateValue, Collection<T>... collections) {
        List<T> list = new ArrayList<>();
        for (Collection<T> collection : collections) {
            if (collection != null) {
                list.addAll(collection);
            }
        }
        list.forEach(x -> {
            if (list.stream().filter(other -> Objects.equals(other, x)).count() > 1) {
                handleDuplicateValue.accept(x);
            }
        });
    }
}
