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

import org.xlrnet.metadict.api.language.Dictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * This class can be used to build a {@link FeatureSet} object to describe a search engine's
 * features by using a simple builder pattern.
 */
public class FeatureSetBuilder {

    /**
     * An array of languages the engine supports. This can be e.g. English-English, German-English but also
     * English-German.
     */
    protected List<Dictionary> supportedDictionaries = new ArrayList<>();

    /**
     * True, if the engine can also provide external (i.e. non-vocabulary) content like e.g. websites or newsgroup
     * content.
     */
    protected boolean providesExternalContent = false;

    /**
     * True, if the engine i.e. the called website supports fuzzy search.
     */
    protected boolean supportsFuzzySearch = false;

    /**
     * True, if the engine provides alternatives to the given query. This can be e.g. a "did-you-mean" recommendation.
     */
    protected boolean providesAlternatives = false;

    /**
     * True, if the provider can test if the underlying engine works as expected. See the corresponding documentation
     * for more information about this.
     */
    protected boolean supportsSelfTest = false;

    /**
     * Add a new {@link Dictionary} that is supported by the engine. This can be e.g. English-English, German-English but also
     * English-German.
     *
     * @param newSupportedDictionary
     *         A new dictionary that should be added.
     */
    public FeatureSetBuilder addSupportedDictionary(Dictionary newSupportedDictionary) {
        this.supportedDictionaries.add(newSupportedDictionary);
        return this;
    }

    /**
     * Create a new instance of {@link FeatureSet}.
     *
     * @return a new instance of {@link FeatureSet}.
     */
    public FeatureSet build() {
        return new FeatureSetImpl(supportedDictionaries, providesExternalContent, supportsFuzzySearch, providesAlternatives);
    }

    public FeatureSetBuilder setProvidesAlternatives(boolean providesAlternatives) {
        this.providesAlternatives = providesAlternatives;
        return this;
    }

    public FeatureSetBuilder setProvidesExternalContent(boolean providesExternalContent) {
        this.providesExternalContent = providesExternalContent;
        return this;
    }

    public FeatureSetBuilder setSupportsAutoTest(boolean supportsSelfTest) {
        this.supportsSelfTest = supportsSelfTest;
        return this;
    }

    public FeatureSetBuilder setSupportsFuzzySearch(boolean supportsFuzzySearch) {
        this.supportsFuzzySearch = supportsFuzzySearch;
        return this;
    }
}
