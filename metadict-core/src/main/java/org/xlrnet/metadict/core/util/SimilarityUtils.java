/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jakob Hendeß
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

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 * Helper class with static methods for calculating the similarity between two objects.
 */
public class SimilarityUtils {

    private static final SimilarityStatistics ONE = new SimilarityStatistics(1, 1);

    private static final SimilarityStatistics ZERO = new SimilarityStatistics(1, 0);

    /**
     * Calculate the similarity between two objects by counting how many fields with the same name in both objects have
     * the same value. For each field that exists in both objects, {@link java.util.Objects#equals(Object, Object)} will
     * be called. The returned similarity is the number of equal fields divided by the number of fields the two objects
     * have in common.
     * <p>
     * Special behaviour: <ul> <li>If both objects are of type {@link CharSequence}, then {@link Objects#equals(Object,
     * Object)} will be called with the two parameters directly.</li> <li>If any of the two objects is an array, an
     * {@link IllegalArgumentException} will be thrown.</li> </ul>
     * <p>
     * This method does <i>not</i> compare the two objects recursively!
     *
     * @param object1
     *         The first object to compare.
     * @param object2
     *         The second object to compare.
     * @return Number of equal fields divided by the number of common fields of those two objects. If the objects share
     * no fields in common or any of them is null, zero will be returned. If both objects are null, one will be
     * returned.
     */
    public static double fieldSimilarity(@Nullable Object object1, @Nullable Object object2) {
        return calculateFieldSimilarity(object1, object2, false, null).getRatio();
    }

    /**
     * Calculate the similarity between two objects by counting how many fields with the same name in both objects have
     * the same value. If both objects contain a field which is neither null nor a subclass of either {@link Number} or
     * {@link CharSequence}, then the field similarity of this field in both objects will be calculated recursively. The
     * returned similarity is the number of equal atomic fields divided by the number of atomic fields the two objects
     * have in common. An atomic field is a field that is either null or a subclass of either {@link Number} or {@link
     * CharSequence}.
     * <p>
     * Special behaviour: <ul> <li>If both objects are of type {@link CharSequence}, then {@link Objects#equals(Object,
     * Object)} will be called with the two parameters directly.</li> <li>If any of the two objects is an array, an
     * {@link IllegalArgumentException} will be thrown.</li> <li>Whenever an object loop has been detected, {@link
     * Objects#equals(Object, Object)} will be called on the current field to prevent a {@link StackOverflowError}</li>
     * </ul>
     * <p>
     *
     * @param object1
     *         The first object to compare.
     * @param object2
     *         The second object to compare.
     * @return Number of equal fields divided by the number of common fields of those two objects. If the objects share
     * no fields in common or any of them is null, zero will be returned. If both objects are null, one will be
     * returned.
     */
    public static double deepFieldSimilarity(@Nullable Object object1, @Nullable Object object2) {
        return calculateFieldSimilarity(object1, object2, true, new Stack<>()).getRatio();
    }

    private static SimilarityStatistics calculateFieldSimilarity(@Nullable Object object1, @Nullable Object object2, boolean invokeRecursively, @Nullable Stack<Object> recursiveStack) {
        if (object1 == null && object2 == null)
            return ONE;

        if (object1 == null || object2 == null)
            return ZERO;

        if (object1 instanceof Object[] || object2 instanceof Object[])
            throw new IllegalArgumentException("Arrays cannot be compared");

        if (object1 instanceof Number || object2 instanceof Number)
            return Objects.equals(object1, object2) ? ONE : ZERO;

        if (object1 instanceof Boolean || object2 instanceof Boolean)
            return Objects.equals(object1, object2) ? ONE : ZERO;

        if (object1 instanceof CharSequence || object2 instanceof CharSequence)
            return Objects.equals(object1, object2) ? ONE : ZERO;

        Map<String, Object> fieldValueMap1 = getFieldNamesAndValues(object1);
        Map<String, Object> fieldValueMap2 = getFieldNamesAndValues(object2);

        double equalFieldValues = 0, sameFieldNames = 0;

        for (Map.Entry<String, Object> entry : fieldValueMap1.entrySet()) {
            String key = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = fieldValueMap2.get(key);

            if (fieldValueMap2.containsKey(key)) {
                if (!invokeRecursively)
                    sameFieldNames++;

                if (invokeRecursively) {
                    SimilarityStatistics similarityStatistics;

                    assert recursiveStack != null;
                    recursiveStack.push(value1);

                    // Try to detect potential stack overflow
                    if (recursiveStack.contains(value1)) {
                        similarityStatistics = calculateFieldSimilarity(value1, value2, false, recursiveStack);
                    } else {
                        similarityStatistics = calculateFieldSimilarity(value1, value2, true, recursiveStack);
                    }

                    recursiveStack.pop();

                    sameFieldNames += similarityStatistics.sameFieldNames;
                    equalFieldValues += similarityStatistics.equalFieldValues;

                } else if (Objects.equals(value1, value2)) {
                    equalFieldValues++;
                }
            }
        }
        return new SimilarityStatistics(sameFieldNames, equalFieldValues);
    }

    private static Map<String, Object> getFieldNamesAndValues(final Object valueObj) throws IllegalArgumentException {
        Class clazz = valueObj.getClass();
        Map<String, Object> fieldMap = new HashMap<>();
        Field[] valueObjFields = clazz.getDeclaredFields();

        for (Field valueObjField : valueObjFields) {
            String fieldName = valueObjField.getName();
            valueObjField.setAccessible(true);
            try {
                Object fieldObject = valueObjField.get(valueObj);
                fieldMap.put(fieldName, fieldObject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return fieldMap;
    }

    private static class SimilarityStatistics {

        final double sameFieldNames;

        final double equalFieldValues;

        public SimilarityStatistics(double sameFieldNames, double equalFieldValues) {
            this.sameFieldNames = sameFieldNames;
            this.equalFieldValues = equalFieldValues;
        }

        public double getRatio() {
            return (sameFieldNames > 0) ? (equalFieldValues / sameFieldNames) : 0;
        }

    }

}
