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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Various test cases for methods in {@link CommonUtils}.
 */
public class CommonUtilsTest {

    @Test
    public void testIsValidStorageServiceName_valid() throws Exception {
        assertTrue(CommonUtils.isValidStorageServiceName("abc"));
        assertTrue(CommonUtils.isValidStorageServiceName("AbC"));
        assertTrue(CommonUtils.isValidStorageServiceName("A123f"));
        assertTrue(CommonUtils.isValidStorageServiceName("a9-b1"));
        assertTrue(CommonUtils.isValidStorageServiceName("H---H"));
    }

    @Test
    public void testIsValidStorageServiceName_invalid() throws Exception {
        assertFalse(CommonUtils.isValidStorageServiceName("123"));
        assertFalse(CommonUtils.isValidStorageServiceName("1abC"));
        assertFalse(CommonUtils.isValidStorageServiceName("-a12"));
        assertFalse(CommonUtils.isValidStorageServiceName(" 12"));
        assertFalse(CommonUtils.isValidStorageServiceName("abc "));
        assertFalse(CommonUtils.isValidStorageServiceName(" abc "));
    }
}