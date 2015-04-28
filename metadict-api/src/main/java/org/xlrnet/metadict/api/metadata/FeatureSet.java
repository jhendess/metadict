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

package org.xlrnet.metadict.api.metadata;

import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;

import java.util.Collection;

/**
 * The {@link FeatureSet} interface is used to describe the features a metadict search engine provides. To build new
 * instances and configure your available features, use the {@link FeatureSetBuilder} class.
 */
public interface FeatureSet {

    /**
     * Return a collection of dictionaries that the engine supports for bilingual lookups. This can be e.g.
     * English-English or German-English but also English-German.
     */
    Collection<BilingualDictionary> getSupportedBilingualDictionaries();

    /**
     * Return a collection of languages that the engine supports for monolingual lookups. This can be a normal language
     * like English or German.
     */
    Collection<Language> getSupportedLexicographicLanguages();

    /**
     * True, if the engine provides alternatives to the given query. This can be e.g. a "did-you-mean" recommendation.
     */
    boolean isProvidesAlternatives();

    /**
     * True, if the engine supports searching for dictionary entries. A dictionary entry is a bilingual
     * dictionary lookup that provides a translation between two different languages.
     */
    boolean isProvidesBilingualDictionaryEntries();

    /**
     * True, if the engine can also provide external (i.e. non-vocabulary) content like e.g. websites or newsgroup
     * content.
     */
    boolean isProvidesExternalContent();

    /**
     * True, if the engine supports searching for lexicographic entries. A lexicographic entry is a monolingual
     * dictionary lookup with detailed information about one entry in one language.
     */
    boolean isProvidesMonolingualEntries();

    /**
     * True, if the engine i.e. the called website supports fuzzy search.
     */
    boolean isSupportsFuzzySearch();

    /**
     * True, if the provider can test if the underlying engine works as expected. See the corresponding documentation
     * for more information about this.
     */
    boolean isSupportsSelfTest();
}
