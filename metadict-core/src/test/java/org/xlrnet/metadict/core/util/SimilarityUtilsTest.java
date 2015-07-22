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

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for {@link SimilarityUtils}.
 */
public class SimilarityUtilsTest {

    @Test
    public void testDeepFieldSimilarity_bothNull() throws Exception {
        assertEquals(1.0, SimilarityUtils.deepFieldSimilarity(null, null), 0.0);
    }

    @Test
    public void testDeepFieldSimilarity_eitherNull() throws Exception {
        assertEquals(0, SimilarityUtils.deepFieldSimilarity("TEST", null), 0.0);
        assertEquals(0, SimilarityUtils.deepFieldSimilarity(null, "TEST"), 0.0);
    }

    @Test
    public void testDeepFieldSimilarity_String_String_same() throws Exception {
        assertEquals(1, SimilarityUtils.deepFieldSimilarity("TEST", "TEST"), 0.0);
    }

    @Test
    public void testDeepFieldSimilarity_String_String_different() throws Exception {
        assertEquals(0, SimilarityUtils.deepFieldSimilarity("TRUE", "FALSE"), 0.0);
    }

    @Test
    public void testDeepFieldSimilarity_String_Number() throws Exception {
        assertEquals(0, SimilarityUtils.deepFieldSimilarity("TRUE", 1), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeepFieldSimilarity_Array_Array_1() throws Exception {
        assertEquals(1, SimilarityUtils.deepFieldSimilarity(new String[] { "TEST" }, "TEST"), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeepFieldSimilarity_Array_Array_2() throws Exception {
        assertEquals(0, SimilarityUtils.deepFieldSimilarity("TEST", new String[] { "FALSE" }), 0.0);
    }

    @Test
    public void testDeepFieldSimilarity_Object_Object_same() throws Exception {
        TestClass testObject = new TestClass("TEST", 1, null);
        assertEquals(1, SimilarityUtils.deepFieldSimilarity(testObject, testObject), 0.0);
    }

    @Test
    public void testDeepFieldSimilarity_Object_Object_equal() throws Exception {
        TestClass testObject1 = new TestClass("TEST", 1, null);
        TestClass testObject2 = new TestClass("TEST", 1, null);
        assertEquals(1, SimilarityUtils.deepFieldSimilarity(testObject1, testObject2), 0.0);
    }

    @Test
    public void testDeepFieldSimilarity_Object_Object_different1() throws Exception {
        TestClass testObject1 = new TestClass("TEST", 1, null);
        TestClass testObject2 = new TestClass("TEST", 2, null);
        assertEquals(0.66, SimilarityUtils.deepFieldSimilarity(testObject1, testObject2), 0.01);
    }

    @Test
    public void testDeepFieldSimilarity_Object_Object_different2() throws Exception {
        TestClass testObject1 = new TestClass("TEST", 1, null);
        TestClass testObject2 = new TestClass("TEST", 2, "TEST");
        assertEquals(0.33, SimilarityUtils.deepFieldSimilarity(testObject1, testObject2), 0.01);
    }

    @Test
    public void testDeepFieldSimilarity_Object_Object_different3() throws Exception {
        TestClass testObject1 = new TestClass("TEST", 1, null);
        TestClass testObject2 = new TestClass("NOPE", 2, "TEST");
        assertEquals(0, SimilarityUtils.deepFieldSimilarity(testObject1, testObject2), 0.01);
    }

    @Test
    public void testDeepFieldSimilarity_String_Object() throws Exception {
        TestClass testObject1 = new TestClass("TEST", 1, null);
        assertEquals(0, SimilarityUtils.deepFieldSimilarity(testObject1, "TEST"), 0.0);
    }

    @Test
    public void testDeepFieldSimilarity_Boolean_different() throws Exception {
        assertEquals(0, SimilarityUtils.deepFieldSimilarity(Boolean.TRUE, Boolean.FALSE), 0.0);
    }

    @Test
    public void testDeepFieldSimilarity_Boolean_same() throws Exception {
        assertEquals(0, SimilarityUtils.deepFieldSimilarity(Boolean.TRUE, Boolean.FALSE), 0.0);
    }

    @Test
    public void testDeepFieldSimilarity_nested() throws Exception {
        assertEquals(1, SimilarityUtils.deepFieldSimilarity(Optional.empty(), Optional.empty()), 0.0);
    }

    @Test
    public void testDeepFieldSimilarity_Object_Object_nested_same() throws Exception {
        TestClass nestedTestObject = new TestClass("TEST", 1, null);
        TestClass testObject1 = new TestClass("TEST", 1, nestedTestObject);
        TestClass testObject2 = new TestClass("TEST", 2, nestedTestObject);
        // Everything but 1/2 in the top-objects is equal
        assertEquals(0.8, SimilarityUtils.deepFieldSimilarity(testObject1, testObject2), 0.01);
    }

    @Test
    public void testDeepFieldSimilarity_Object_Object_nested_different() throws Exception {
        TestClass nestedTestObject1 = new TestClass("TRUE", 1, null);
        TestClass nestedTestObject2 = new TestClass("FALSE", 2, null);
        TestClass testObject1 = new TestClass("TEST", 1, nestedTestObject1);
        TestClass testObject2 = new TestClass("TEST", 2, nestedTestObject2);
        // Only null in the nested objects is equal and top-Level string are equal
        assertEquals(0.4, SimilarityUtils.deepFieldSimilarity(testObject1, testObject2), 0.01);
    }

    @Test
    public void testFieldSimilarity_bothNull() throws Exception {
        assertEquals(1.0, SimilarityUtils.fieldSimilarity(null, null), 0.0);
    }

    @Test
    public void testFieldSimilarity_eitherNull() throws Exception {
        assertEquals(0, SimilarityUtils.fieldSimilarity("TEST", null), 0.0);
        assertEquals(0, SimilarityUtils.fieldSimilarity(null, "TEST"), 0.0);
    }

    @Test
    public void testFieldSimilarity_String_String_same() throws Exception {
        assertEquals(1, SimilarityUtils.fieldSimilarity("TEST", "TEST"), 0.0);
    }

    @Test
    public void testFieldSimilarity_String_String_different() throws Exception {
        assertEquals(0, SimilarityUtils.fieldSimilarity("TRUE", "FALSE"), 0.0);
    }

    @Test
    public void testFieldSimilarity_String_Number() throws Exception {
        assertEquals(0, SimilarityUtils.fieldSimilarity("TRUE", 1), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFieldSimilarity_Array_Array_1() throws Exception {
        assertEquals(1, SimilarityUtils.fieldSimilarity(new String[]{"TEST"}, "TEST"), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFieldSimilarity_Array_Array_2() throws Exception {
        assertEquals(0, SimilarityUtils.fieldSimilarity("TEST", new String[]{"FALSE"}), 0.0);
    }

    @Test
    public void testFieldSimilarity_Object_Object_same() throws Exception {
        TestClass testObject = new TestClass("TEST", 1, null);
        assertEquals(1, SimilarityUtils.fieldSimilarity(testObject, testObject), 0.0);
    }

    @Test
    public void testFieldSimilarity_Object_Object_equal() throws Exception {
        TestClass testObject1 = new TestClass("TEST", 1, null);
        TestClass testObject2 = new TestClass("TEST", 1, null);
        assertEquals(1, SimilarityUtils.fieldSimilarity(testObject1, testObject2), 0.0);
    }

    @Test
    public void testFieldSimilarity_Object_Object_different1() throws Exception {
        TestClass testObject1 = new TestClass("TEST", 1, null);
        TestClass testObject2 = new TestClass("TEST", 2, null);
        assertEquals(0.66, SimilarityUtils.fieldSimilarity(testObject1, testObject2), 0.01);
    }

    @Test
    public void testFieldSimilarity_Object_Object_different2() throws Exception {
        TestClass testObject1 = new TestClass("TEST", 1, null);
        TestClass testObject2 = new TestClass("TEST", 2, "TEST");
        assertEquals(0.33, SimilarityUtils.fieldSimilarity(testObject1, testObject2), 0.01);
    }

    @Test
    public void testFieldSimilarity_Object_Object_different3() throws Exception {
        TestClass testObject1 = new TestClass("TEST", 1, null);
        TestClass testObject2 = new TestClass("NOPE", 2, "TEST");
        assertEquals(0, SimilarityUtils.fieldSimilarity(testObject1, testObject2), 0.01);
    }

    @Test
    public void testFieldSimilarity_String_Object() throws Exception {
        TestClass testObject1 = new TestClass("TEST", 1, null);
        assertEquals(0, SimilarityUtils.fieldSimilarity(testObject1, "TEST"), 0.0);
    }

    @Test
    public void testFieldSimilarity_Object_Object_nested_same() throws Exception {
        TestClass nestedTestObject = new TestClass("TEST", 1, null);
        TestClass testObject1 = new TestClass("TEST", 1, nestedTestObject);
        TestClass testObject2 = new TestClass("TEST", 2, nestedTestObject);
        assertEquals(0.66, SimilarityUtils.fieldSimilarity(testObject1, testObject2), 0.01);
    }

    static class TestClass {

        private String someString;

        private int someInt;

        private Object someObject;

        public TestClass(String someString, int someInt, Object someObject) {
            this.someString = someString;
            this.someInt = someInt;
            this.someObject = someObject;
        }
    }

}