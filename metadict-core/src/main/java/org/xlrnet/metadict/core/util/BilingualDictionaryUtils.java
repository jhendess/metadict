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

import org.apache.commons.lang3.StringUtils;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.language.UnsupportedDictionaryException;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Helper method for dictionary related tasks.
 */
public class BilingualDictionaryUtils {

    public static List<BilingualDictionary> resolveDictionaries(String dictionaryQuery, boolean bidirectional) throws UnsupportedDictionaryException {

        String[] explodedQuery = StringUtils.split(dictionaryQuery, ",");
        List<BilingualDictionary> dictionaryList = new ArrayList<>(explodedQuery.length);

        for (String query : explodedQuery) {
            checkArgument(BilingualDictionary.DICTIONARY_QUERY_PATTERN.matcher(query).matches(), "Invalid dictionary query: %s", query);
            if (bidirectional) {
                query = query.replace(BilingualDictionary.UNIDIRECTIONAL_FLAG, BilingualDictionary.BIDIRECTIONAL_FLAG);
            } else {
                query = query.replace(BilingualDictionary.BIDIRECTIONAL_FLAG, BilingualDictionary.UNIDIRECTIONAL_FLAG);
            }

            BilingualDictionary dictionary = BilingualDictionary.fromQueryString(query);
            dictionaryList.add(dictionary);
        }

        return dictionaryList;
    }

    public static void sortDictionaryListAlphabetically(List<BilingualDictionary> dictionaries) {
        Map<Language, Integer> languagePriorityMap = new HashMap<>();

        // Count how often each language is used
        for (BilingualDictionary dictionary : dictionaries) {
            languagePriorityMap.put(dictionary.getSource(), (languagePriorityMap.getOrDefault(dictionary.getSource(), 0) + 1));
            if (dictionary.isBidirectional()) {
                languagePriorityMap.put(dictionary.getTarget(), (languagePriorityMap.getOrDefault(dictionary.getTarget(), 0) + 1));
            }
        }

        // Use inverted dictionaries if the output language has a higher priority than the input language
        for (int i = 0; i < dictionaries.size(); i++) {
            BilingualDictionary dictionary = dictionaries.get(i);
            if (!dictionary.isBidirectional()) {
                continue;
            }
            int inputPriority = languagePriorityMap.get(dictionary.getSource());
            int outputPriority = languagePriorityMap.get(dictionary.getTarget());

            if (outputPriority > inputPriority) {
                dictionaries.set(i, BilingualDictionary.inverse(dictionary));
            }
        }

        Collections.sort(dictionaries);
    }
}
