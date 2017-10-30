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

package org.xlrnet.metadict.api.language;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link Language} logic.
 */
public class LanguageTest {

    @Test
    public void testIsValidIdentifer_fail() {
        assertFalse(Language.isValidIdentifier(""));
        assertFalse(Language.isValidIdentifier("1aB"));
        assertFalse(Language.isValidIdentifier("A 123"));
        assertFalse(Language.isValidIdentifier(" ab"));
    }

    @Test
    public void testIsValidIdentifer_success() {
        assertTrue(Language.isValidIdentifier("a"));
        assertTrue(Language.isValidIdentifier("aB"));
        assertTrue(Language.isValidIdentifier("ABcZ"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLanguageById_fail1() {
        Language.getLanguageById("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLanguageById_fail2() {
        Language.getLanguageById("12_34");
    }

    @Test
    public void testGetLanguageById_regular() {
        Language language = Language.getLanguageById("XX");
        assertEquals("xx", language.getIdentifier());
    }

    @Test
    public void testGetLanguageById_dialect() {
        Language language = Language.getLanguageById("XX_yy");
        assertEquals("xx", language.getIdentifier());
        assertEquals("yy", language.getDialect());
    }

}