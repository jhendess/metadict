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

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by xolor on 01.04.15.
 */
public class BilingualDictionaryTest {

    @Test
    public void testFromLanguages_cachingMechanism() throws Exception {
        BilingualDictionary d1 = BilingualDictionary.fromLanguages(Language.ENGLISH, Language.GERMAN, true);
        BilingualDictionary d2 = BilingualDictionary.fromLanguages(Language.ENGLISH, Language.GERMAN, true);

        assertEquals("Cached dictionaries should have the same reference", d1, d2);
        assertTrue("Cached dictionaries should have the same reference", d1 == d2);
    }

    @Test
    public void testBuildQueryString_list() {
        ImmutableList<BilingualDictionary> dictionaries = ImmutableList.of(
                BilingualDictionary.fromLanguages(Language.GERMAN, Language.NORWEGIAN, false),
                BilingualDictionary.fromLanguages(Language.NORWEGIAN_BOKMÅL, Language.NORWEGIAN_NYNORSK, true)
        );
        String actual = BilingualDictionary.buildQueryString(dictionaries);

        assertEquals("de-no,no_bo<>no_ny", actual);
    }
}