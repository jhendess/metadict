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

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Various test cases for methods in {@link BilingualDictionaryUtils}.
 */
public class BilingualDictionaryUtilsTest {

    public static BilingualDictionary ENGLISH_GERMAN_BI = BilingualDictionary.fromLanguages(Language.ENGLISH, Language.GERMAN, true);

    public static BilingualDictionary FRENCH_GERMAN_BI = BilingualDictionary.fromLanguages(Language.FRENCH, Language.GERMAN, true);

    public static BilingualDictionary GERMAN_ENGLISH_BI = BilingualDictionary.fromLanguages(Language.GERMAN, Language.ENGLISH, true);

    public static BilingualDictionary GERMAN_FRENCH_BI = BilingualDictionary.fromLanguages(Language.GERMAN, Language.FRENCH, true);

    public static List<BilingualDictionary> EXPECTED_SORTED_LIST = ImmutableList.of(GERMAN_ENGLISH_BI, GERMAN_FRENCH_BI);

    @Test
    public void testSortDictionaryListAlphabetically() throws Exception {
        List<BilingualDictionary> dictionaryList = new ArrayList<>();
        dictionaryList.add(FRENCH_GERMAN_BI);
        dictionaryList.add(ENGLISH_GERMAN_BI);

        BilingualDictionaryUtils.sortDictionaryListAlphabetically(dictionaryList);

        assertEquals(EXPECTED_SORTED_LIST, dictionaryList);
    }
}