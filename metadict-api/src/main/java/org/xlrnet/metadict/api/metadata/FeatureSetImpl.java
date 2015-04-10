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
import org.xlrnet.metadict.api.language.Dictionary;

/**
 * Concrete implementation of a settable {@link FeatureSet}.
 */
public class FeatureSetImpl implements FeatureSet {

    /**
     * An array of languages the engine supports. This can be e.g. English-English, German-English but also
     * English-German.
     */
    protected final Iterable<Dictionary> supportedDictionaries;

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
    protected final boolean supportsSelfTest = false;

    FeatureSetImpl(Iterable<Dictionary> supportedDictionaries, boolean providesExternalContent, boolean supportsFuzzySearch, boolean providesAlternatives) {
        this.supportedDictionaries = supportedDictionaries;
        this.providesExternalContent = providesExternalContent;
        this.supportsFuzzySearch = supportsFuzzySearch;
        this.providesAlternatives = providesAlternatives;
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
                Objects.equal(supportedDictionaries, that.supportedDictionaries);
    }

    public Iterable<Dictionary> getSupportedDictionaries() {
        return supportedDictionaries;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(supportedDictionaries, providesExternalContent, supportsFuzzySearch, providesAlternatives, supportsSelfTest);
    }

    public boolean isProvidesAlternatives() {
        return providesAlternatives;
    }

    public boolean isProvidesExternalContent() {
        return providesExternalContent;
    }

    public boolean isSupportsFuzzySearch() {
        return supportsFuzzySearch;
    }

    public boolean isSupportsSelfTest() {
        return supportsSelfTest;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("supportedDictionaries", supportedDictionaries)
                .add("providesExternalContent", providesExternalContent)
                .add("supportsFuzzySearch", supportsFuzzySearch)
                .add("providesAlternatives", providesAlternatives)
                .add("supportsSelfTest", supportsSelfTest)
                .toString();
    }
}
