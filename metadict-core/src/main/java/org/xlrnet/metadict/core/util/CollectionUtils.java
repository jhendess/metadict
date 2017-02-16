/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.xlrnet.metadict.core.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Utility functions for operating on collections.
 */
public class CollectionUtils {

    private CollectionUtils() {

    }

    /**
     * Takes a collection of objects where each object will be mapped to a collection of objects, normalizes them and
     * filters out duplicates of the normalized value. If more than one object exists which maps to a certain normalized
     * value, the first one will be returned.
     *
     * @param sourceObjects
     *         A collection of source objects that should be divided and merged.
     * @param divider
     *         Divider function which splits a single object into multiple.
     * @param normalizer
     *         Mapping function which will be used for normalizing.
     * @param <T>
     *         Object type which will be divided into {@link V}.
     * @param <V>
     *         Value type which will be normalized and filtered.
     * @return A collection of of reduced objects of type {@link V}.
     */
    @NotNull
    public static <T, V> List<V> divideAndFilterNormalized(@NotNull Collection<T> sourceObjects, @NotNull Function<T, List<V>> divider, @NotNull Function<V, V> normalizer) {
        Map<V, V> normalizedAndActualObjects = new HashMap<>(sourceObjects.size());
        for (T sourceObject : sourceObjects) {
            Collection<V> collected = divider.apply(sourceObject);
            if (collected == null) {
                continue;
            }
            for (V toNormalize : collected) {
                V normalized = normalizer.apply(toNormalize);
                normalizedAndActualObjects.putIfAbsent(normalized, toNormalize);
            }
        }

        return Lists.newArrayList(normalizedAndActualObjects.values());
    }

    /**
     * Takes a collection, extracts Maps for each objects, normalizes them in the list and them and
     * filters out duplicates of the normalized value. If more than one object exists which maps to a certain normalized
     * value, the first one will be returned.
     *
     * @param sourceObjects
     *         A map of source objects that should be divided and merged.
     * @param normalizer
     *         Mapping function which will be used for normalizing.
     * @param merger
     *         Merging function, which will be applied on all objects of type {@link V} with the same key of type {@link
     *         K} and the last result of type {@link R} to create a new result. Both parameters may be null.
     * @param <T>
     *         Object type which will be divided into Maps of {@link K} and {@link V}.
     * @param <K>
     *         Key type which will be used for identification.
     * @param <V>
     *         Value type which will be normalized and filtered.
     * @param <R>
     *         Type of the result object.
     * @return A collection of of reduced objects of type {@link R}.
     */
    @NotNull
    public static <T, K, V, R> Map<K, R> divideAndMerge(@NotNull List<T> sourceObjects, @NotNull Function<T, Map<K, V>> divider, @NotNull Function<V, V> normalizer, @NotNull BiFunction<V, R, R> merger) {
        Multimap<K, V> multimap = MultimapBuilder.hashKeys().hashSetValues().build();   // Use hash-based values to avoid duplicates
        Map<V, V> normalizedAndActualObjects = new HashMap<>(sourceObjects.size());

        for (T sourceObject : sourceObjects) {
            Map<K, V> map = divider.apply(sourceObject);
            for (Map.Entry<K, V> entry : map.entrySet()) {
                K key = entry.getKey();
                V toNormalize = entry.getValue();

                if (toNormalize == null) {
                    continue;
                }
                V normalized = normalizer.apply(toNormalize);
                normalizedAndActualObjects.putIfAbsent(normalized, toNormalize);
                multimap.put(key, normalized);
            }
        }

        Map<K, R> result = new HashMap<>();

        for (K key : multimap.keys()) {
            R mergedObject = null;
            for (V v : multimap.get(key)) {
                mergedObject = merger.apply(normalizedAndActualObjects.get(v), mergedObject);
            }
            result.put(key, mergedObject);
        }

        return result;
    }
}
