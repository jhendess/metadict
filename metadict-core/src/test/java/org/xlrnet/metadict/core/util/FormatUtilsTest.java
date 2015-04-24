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
import org.xlrnet.metadict.api.language.Dictionary;
import org.xlrnet.metadict.api.language.Language;

import static org.junit.Assert.assertEquals;

/**
 * Various test cases for the methods in {@link FormatUtils}.
 */
public class FormatUtilsTest {

    @Test
    public void testFormatDictionary_unidirected_noDialects() throws Exception {
        Dictionary testDictionary = Dictionary.fromLanguages(Language.GERMAN, Language.ENGLISH, false);

        String expected = "German -> English";
        String actual = FormatUtils.formatDictionaryName(testDictionary);

        assertEquals(expected, actual);
    }

    @Test
    public void testFormatDictionary_bidirected_dialects() throws Exception {
        Language britishEnglish = Language.forSimpleLanguage("en", "english", "gb", "great britain");
        Dictionary testDictionary = Dictionary.fromLanguages(Language.NORWEGIAN_NYNORSK, britishEnglish, true);

        String expected = "Norwegian (Nynorsk) <-> English (Great Britain)";
        String actual = FormatUtils.formatDictionaryName(testDictionary);

        assertEquals(expected, actual);
    }
}