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

package org.xlrnet.metadict.api.engine;

import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;

import java.util.ArrayList;
import java.util.List;

/**
 * This class can be used to build a {@link FeatureSet} object to describe a search engine's
 * features by using a simple builder pattern.
 */
public class FeatureSetBuilder {

    /**
     * A collection of dictionaries that the engine supports for bilingual lookups. This can be e.g. English-English or
     * German-English but also English-German.
     */
    private List<BilingualDictionary> supportedBilingualDictionaries = new ArrayList<>();

    /**
     * A collection of languages that the engine supports for monolingual lookups. This can be a normal language like
     * English or German.
     */
    private List<Language> supportedLexicographicLanguages = new ArrayList<>();

    /**
     * True, if the engine can also provide external (i.e. non-vocabulary) content like e.g. websites or newsgroup
     * content.
     */
    private boolean providesExternalContent = false;

    /**
     * True, if the engine i.e. the called website supports fuzzy search.
     */
    private boolean supportsFuzzySearch = false;

    /**
     * True, if the engine provides alternatives to the given query. This can be e.g. a "did-you-mean" recommendation.
     */
    private boolean providesAlternatives = false;

    /**
     * True, if the provider can test if the underlying engine works as expected. See the corresponding documentation
     * for more information about this.
     */
    private boolean supportsAutoTest = false;

    /**
     * True, if the engine supports searching for lexicographic entries. A lexicographic entry is a monolingual
     * dictionary lookup with detailed information about one entry in one language.
     */
    private boolean providesLexicographicEntries = false;

    /**
     * True, if the engine supports searching for dictionary entries. A dictionary entry is a bilingual
     * dictionary lookup that provides a translation between two different languages.
     */
    private boolean providesBilingualDictionaryEntries = false;

    FeatureSetBuilder() {

    }

    /**
     * Add a new {@link BilingualDictionary} for bilingual lookups that is supported by the engine. This can be e.g.
     * English-English, German-English but also English-German.
     *
     * @param newSupportedDictionary
     *         A new dictionary that should be added.
     */
    public FeatureSetBuilder addSupportedBilingualDictionary(BilingualDictionary newSupportedDictionary) {
        this.supportedBilingualDictionaries.add(newSupportedDictionary);
        return this;
    }

    /**
     * Add a new {@link Language} that is supported by the engine for lexicographic entries (i.e. monolingual lookups).
     * This can be e.g. English to search only in English dictionaries.
     *
     * @param newLexicographicLanguage
     *         A new language that should be added.
     */
    public FeatureSetBuilder addSupportedLexicographicLanguage(Language newLexicographicLanguage) {
        this.supportedLexicographicLanguages.add(newLexicographicLanguage);
        return this;
    }

    /**
     * Create a new instance of {@link FeatureSet}.
     *
     * @return a new instance of {@link FeatureSet}.
     */
    public FeatureSet build() {
        return new ImmutableFeatureSet(supportedBilingualDictionaries, supportedLexicographicLanguages, providesExternalContent, supportsFuzzySearch, providesAlternatives, supportsAutoTest, providesLexicographicEntries, providesBilingualDictionaryEntries);
    }

    /**
     * True, if the engine provides alternatives to the given query. This can be e.g. a "did-you-mean" recommendation.
     */
    public FeatureSetBuilder setProvidesAlternatives(boolean providesAlternatives) {
        this.providesAlternatives = providesAlternatives;
        return this;
    }

    /**
     * True, if the engine supports searching for dictionary entries. A dictionary entry is a bilingual
     * dictionary lookup that provides a translation between two different languages.
     */
    public FeatureSetBuilder setProvidesBilingualDictionaryEntries(boolean providesBilingualDictionaryEntries) {
        this.providesBilingualDictionaryEntries = providesBilingualDictionaryEntries;
        return this;
    }

    /**
     * True, if the engine can also provide external (i.e. non-vocabulary) content like e.g. websites or newsgroup
     * content.
     */
    public FeatureSetBuilder setProvidesExternalContent(boolean providesExternalContent) {
        this.providesExternalContent = providesExternalContent;
        return this;
    }

    /**
     * True, if the engine supports searching for lexicographic entries. A lexicographic entry is a monolingual
     * dictionary lookup with detailed information about one entry in one language.
     */
    public FeatureSetBuilder setProvidesMonolingualEntries(boolean providesLexicographicEntries) {
        this.providesLexicographicEntries = providesLexicographicEntries;
        return this;
    }

    /**
     * True, if the provider can test if the underlying engine works as expected. See the corresponding documentation
     * for more information about this.
     */
    public FeatureSetBuilder setSupportsAutoTest(boolean supportsSelfTest) {
        this.supportsAutoTest = supportsSelfTest;
        return this;
    }

    /**
     * True, if the engine i.e. the called website supports fuzzy search.
     */
    public FeatureSetBuilder setSupportsFuzzySearch(boolean supportsFuzzySearch) {
        this.supportsFuzzySearch = supportsFuzzySearch;
        return this;
    }
}
