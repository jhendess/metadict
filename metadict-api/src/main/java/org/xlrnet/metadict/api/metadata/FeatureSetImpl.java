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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;

import java.util.Collection;

/**
 * Concrete implementation of a settable {@link FeatureSet}.
 */
public class FeatureSetImpl implements FeatureSet {

    private static final long serialVersionUID = 4593476472125853031L;

    /**
     * A collection of dictionaries the engine supports for bilingual lookups. This can be e.g. English-English or
     * German-English but also English-German.
     */
    protected final Collection<BilingualDictionary> supportedBilingualDictionaries;

    /**
     * True, if the engine can also provide external (i.e. non-vocabulary) content like e.g. websites or newsgroup
     * content.
     */
    protected final boolean providesExternalContent;

    /**
     * True, if the engine i.e. the called website supports fuzzy search.
     */
    protected final boolean supportsFuzzySearch;

    /**
     * True, if the engine provides alternatives to the given query. This can be e.g. a "did-you-mean" recommendation.
     */
    protected final boolean providesAlternatives;

    /**
     * True, if the provider can test if the underlying engine works as expected. See the corresponding documentation
     * for more information about this.
     */
    protected final boolean supportsSelfTest;

    /**
     * True, if the engine supports searching for lexicographic entries. A lexicographic entry is a monolingual
     * dictionary lookup with detailed information about one entry in one language.
     */
    protected final boolean providesMonolingualEntries;

    /**
     * True, if the engine supports searching for dictionary entries. A dictionary entry is a bilingual
     * dictionary lookup that provides a translation between two different languages.
     */
    protected final boolean providesBilingualDictionaryEntries;

    /**
     * A collection of languages that the engine supports for monolingual lookups. This can be a normal language like
     * English or German.
     */
    private final Collection<Language> supportedLexicographicLanguages;

    FeatureSetImpl(Collection<BilingualDictionary> supportedBilingualDictionaries, Collection<Language> supportedLexicographicLanguages, boolean providesExternalContent, boolean supportsFuzzySearch, boolean providesAlternatives, boolean supportsSelfTest, boolean providesMonolingualEntries, boolean providesBilingualDictionaryEntries) {
        this.supportedBilingualDictionaries = supportedBilingualDictionaries;
        this.supportedLexicographicLanguages = supportedLexicographicLanguages;
        this.providesExternalContent = providesExternalContent;
        this.supportsFuzzySearch = supportsFuzzySearch;
        this.providesAlternatives = providesAlternatives;
        this.supportsSelfTest = supportsSelfTest;
        this.providesMonolingualEntries = providesMonolingualEntries;
        this.providesBilingualDictionaryEntries = providesBilingualDictionaryEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureSetImpl)) return false;
        FeatureSetImpl that = (FeatureSetImpl) o;
        return Objects.equal(providesExternalContent, that.providesExternalContent) &&
                Objects.equal(supportsFuzzySearch, that.supportsFuzzySearch) &&
                Objects.equal(providesAlternatives, that.providesAlternatives) &&
                Objects.equal(supportsSelfTest, that.supportsSelfTest) &&
                Objects.equal(providesMonolingualEntries, that.providesMonolingualEntries) &&
                Objects.equal(providesBilingualDictionaryEntries, that.providesBilingualDictionaryEntries) &&
                Objects.equal(supportedBilingualDictionaries, that.supportedBilingualDictionaries) &&
                Objects.equal(supportedLexicographicLanguages, that.supportedLexicographicLanguages);
    }

    /**
     * A collection of dictionaries the engine supports for bilingual lookups. This can be e.g. English-English or
     * German-English but also English-German.
     */
    @NotNull
    @Override
    public Collection<BilingualDictionary> getSupportedBilingualDictionaries() {
        return supportedBilingualDictionaries;
    }

    /**
     * Return a collection of languages that the engine supports for monolingual lookups. This can be a normal language
     * like English or German.
     */
    @NotNull
    @Override
    public Collection<Language> getSupportedLexicographicLanguages() {
        return this.supportedLexicographicLanguages;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(supportedBilingualDictionaries, providesExternalContent, supportsFuzzySearch, providesAlternatives, supportsSelfTest, providesMonolingualEntries, providesBilingualDictionaryEntries, supportedLexicographicLanguages);
    }

    /**
     * True, if the engine provides alternatives to the given query. This can be e.g. a "did-you-mean" recommendation.
     */
    @Override
    public boolean isProvidesAlternatives() {
        return providesAlternatives;
    }

    /**
     * True, if the engine supports searching for dictionary entries. A dictionary entry is a bilingual
     * dictionary lookup that provides a translation between two different languages.
     */
    @Override
    public boolean isProvidesBilingualDictionaryEntries() {
        return this.providesBilingualDictionaryEntries;
    }

    /**
     * True, if the engine can also provide external (i.e. non-vocabulary) content like e.g. websites or newsgroup
     * content.
     */
    @Override
    public boolean isProvidesExternalContent() {
        return providesExternalContent;
    }

    /**
     * True, if the engine supports searching for lexicographic entries. A lexicographic entry is a monolingual
     * dictionary lookup with detailed information about one entry in one language.
     */
    @Override
    public boolean isProvidesMonolingualEntries() {
        return this.providesMonolingualEntries;
    }

    /**
     * True, if the engine i.e. the called website supports fuzzy search.
     */
    @Override
    public boolean isSupportsFuzzySearch() {
        return supportsFuzzySearch;
    }

    /**
     * True, if the provider can test if the underlying engine works as expected. See the corresponding documentation
     * for more information about this.
     */
    @Override
    public boolean isSupportsSelfTest() {
        return supportsSelfTest;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("supportedBilingualDictionaries", supportedBilingualDictionaries)
                .add("providesExternalContent", providesExternalContent)
                .add("supportsFuzzySearch", supportsFuzzySearch)
                .add("providesAlternatives", providesAlternatives)
                .add("supportsSelfTest", supportsSelfTest)
                .add("providesMonolingualEntries", providesMonolingualEntries)
                .add("providesBilingualDictionaryEntries", providesBilingualDictionaryEntries)
                .add("supportedLexicographicLanguages", supportedLexicographicLanguages)
                .toString();
    }

}
